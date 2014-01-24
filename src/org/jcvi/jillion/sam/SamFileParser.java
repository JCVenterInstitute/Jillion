package org.jcvi.jillion.sam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.TextLineParser;
import org.jcvi.jillion.sam.attribute.InvalidAttributeException;
import org.jcvi.jillion.sam.attribute.ReservedAttributeValidator;
import org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys;
import org.jcvi.jillion.sam.attribute.SamAttribute;
import org.jcvi.jillion.sam.attribute.SamAttributeKey;
import org.jcvi.jillion.sam.attribute.SamAttributeKeyFactory;
import org.jcvi.jillion.sam.attribute.SamAttributeType;
import org.jcvi.jillion.sam.attribute.SamAttributeValidator;
import org.jcvi.jillion.sam.cigar.Cigar;
import org.jcvi.jillion.sam.header.ReadGroup;
import org.jcvi.jillion.sam.header.ReadGroup.PlatformTechnology;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamHeader.Builder;
import org.jcvi.jillion.sam.header.SamProgram;
import org.jcvi.jillion.sam.header.SamVersion;
import org.jcvi.jillion.sam.header.SortOrder;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;

public class SamFileParser implements SamParser{

	private static final String COMMENT_KEY = "@CO";

	private static final String PROGRAM_KEY = "@PG";

	private static final String READ_GROUP_KEY = "@RG";

	private static final String SEQUENCE_DICTIONARY_KEY = "@SQ";



	private static final String HEADER_KEY = "@HD";
	private static final String HEADER_VERSION_TAG = "VN";
	private static final String HEADER_SORT_TAG = "SO";
	
	private static final Pattern HEADER_TAG_VALUE_PATTERN = Pattern.compile("([A-Za-z][A-Za-z0-9]):([ -~]+)");
	
	private static final Pattern SPLIT_LINE_PATTERN = Pattern.compile("\t");
	
	private static final Pattern TYPED_TAG_VALUE_PATTERN = Pattern.compile("([A-Za-z][A-Za-z0-9]):(([AifZHB]):)?(.+)");
	
	private final DateFormat isoDateFormat =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	private final File samFile;
	private final SamAttributeValidator validator;
	
	public SamFileParser(File samFile) throws IOException {
		if(samFile ==null){
			throw new NullPointerException("sam file can not be null");
		}
		if(!FileUtil.getExtension(samFile).equals("sam")){
			throw new IllegalArgumentException("must be .sam file" + samFile.getAbsolutePath());
		}
		if(!samFile.exists()){
			throw new FileNotFoundException(samFile.getAbsolutePath());
		}
		if(!samFile.canRead()){
			throw new IllegalArgumentException("sam file not readable " + samFile.getAbsolutePath());
		}
		this.samFile = samFile;
		validator = ReservedAttributeValidator.INSTANCE;
	}

	@Override
	public boolean canAccept() {
		return true;
	}

	@Override
	public void accept(SamVisitor visitor) throws IOException {
		if(visitor ==null){
			throw new NullPointerException("visitor can not be null");
		}
		TextLineParser parser =null;
		
		try{
			parser= new TextLineParser(samFile);
			
			SamHeader header = parseHeader(parser);
			visitor.visitHeader(header);
			while(parser.hasNextLine()){
				String line = parser.nextLine().trim();
				if(line.isEmpty()){
					//skip blanks?
					continue;
				}			
				SamRecord record = parseRecord(header, line);
				visitor.visitRecord(record);
			}
			visitor.visitEnd();
		}finally{
			IOUtil.closeAndIgnoreErrors(parser);
		}
	}
	
	private SamRecord parseRecord(SamHeader header, String line) throws IOException{
		String[] fields = SPLIT_LINE_PATTERN.split(line);
		if(fields.length <11){
			//not a sam line?
			throw new IOException("invalid sam record line : " + line);
		}
		SamRecord.Builder builder = new SamRecord.Builder(header, validator);
		
		builder.setQueryName(fields[0]);
		builder.setFlags(SamRecordFlags.parseFlags(Integer.parseInt(fields[1])));
		builder.setReferenceName(fields[2]);
		builder.setStartPosition(Integer.parseInt(fields[3]));
		builder.setMappingQuality(Byte.parseByte(fields[4]));
		builder.setCigar(Cigar.parse(fields[5]));
		builder.setNextReferenceName(fields[6]);
		builder.setNextPosition(Integer.parseInt(fields[7]));
		builder.setObservedTemplateLength(Integer.parseInt(fields[8]));
		builder.setSequence(parseSequence(fields[9]));
		builder.setQualities(parseQualities(fields[10]));
		
		//anything else is an optional field
		for(int i=11; i<fields.length; i++){
			Matcher matcher = TYPED_TAG_VALUE_PATTERN.matcher(fields[i]);
			if(matcher.matches()){
				String key = matcher.group(1);
				String optionalType = matcher.group(3);
				String value = matcher.group(4);
				if(optionalType == null){
					//type not specified, check is reserved?
					ReservedSamAttributeKeys reserved =ReservedSamAttributeKeys.parseKey(key);
					if(reserved ==null){
						//not reserved...
						throw new IOException("unknown optional attribute without type information (not reserved) : "  + fields[i]);
					}
					try {
						builder.addAttribute(new SamAttribute(reserved, value));
					} catch (InvalidAttributeException e) {
						throw new IOException("invalid attribute value for " + fields[i], e);
					}
				}else{
					SamAttributeKey customKey = SamAttributeKeyFactory.getKey(key);
					SamAttributeType type = SamAttributeType.parseType(optionalType.charAt(0), value);
					try {
						builder.addAttribute(new SamAttribute(customKey, type, value));
					} catch (InvalidAttributeException e) {
						throw new IOException("invalid attribute value for " + fields[i], e);
					}
				}
			}else{
				throw new IOException("invalid attribute format " + fields[i]);
			}
		}
		return builder.build();
	}
	private static NucleotideSequence parseSequence(String s){
		if(SamRecord.UNAVAILABLE.equals(s)){
			return null;
		}
		return new NucleotideSequenceBuilder(s).build();
	}
	private static QualitySequence parseQualities(String s){
		if(SamRecord.UNAVAILABLE.equals(s)){
			return null;
		}
		//always encoded in sanger format
		return FastqQualityCodec.SANGER.decode(s);

	}

	private SamHeader parseHeader(TextLineParser parser) throws IOException {
		
		SamHeader.Builder headerBuilder = new SamHeader.Builder();
		String currentLine = parser.nextLine();
		if(currentLine.startsWith(HEADER_KEY)){
			handleHeaderLine(currentLine, headerBuilder);
			//next line
			currentLine = parser.nextLine();
		}
		boolean inHeader = true;
		while(currentLine !=null && inHeader){
			String trimmedLine = currentLine.trim();
			//not sure if blank lines allowed?
			if(!trimmedLine.isEmpty()){
					
				if(currentLine.startsWith(SEQUENCE_DICTIONARY_KEY)){
					handleSequenceDictionary(currentLine, headerBuilder);
				}else if(currentLine.startsWith(READ_GROUP_KEY)){
					handleReadGroup(currentLine, headerBuilder);
				}else if(currentLine.startsWith(PROGRAM_KEY)){
					handleProgram(currentLine, headerBuilder);
				}else if(currentLine.startsWith(COMMENT_KEY)){
					handleComment(currentLine, headerBuilder);
				}else{
					//not in header?
					inHeader = false;
				}
			}
			if(inHeader){
				currentLine = parser.nextLine();
			}
		}
		return headerBuilder.build();
	}

	private void handleComment(String line, Builder headerBuilder) {
		//trim off first 3 characters to get rid of @CO
		headerBuilder.addComment(line.substring(3).trim());
		
	}

	private void handleProgram(String line, Builder headerBuilder) {
		Map<String,String> tags = parseTags(line);
		String id = tags.get("ID");
		SamProgram.Builder builder = new SamProgram.Builder(id);
		if(tags.containsKey("PN")){
			builder.setName(tags.get("PN"));
		}
		if(tags.containsKey("CL")){
			builder.setCommandLine(tags.get("CL"));
		}
		if(tags.containsKey("PP")){
			builder.setPrevousProgramId(tags.get("PP"));
		}
		if(tags.containsKey("DS")){
			builder.setDescription(tags.get("DS"));
		}
		if(tags.containsKey("VN")){
			builder.setVersion(tags.get("VN"));
		}
		headerBuilder.addProgram(builder.build());
		
	}

	private void handleReadGroup(String line, Builder headerBuilder) {
		Map<String,String> tags = parseTags(line);
		String id = tags.get("ID");
		ReadGroup.Builder builder = new ReadGroup.Builder(id);
		
		if(tags.containsKey("CN")){
			builder.setSequencingCenter(tags.get("CN"));
		}
		if(tags.containsKey("DS")){
			builder.setDescription(tags.get("DS"));
		}
		if(tags.containsKey("DT")){
			try {
				Date date = isoDateFormat.parse(tags.get("DT"));
				builder.setRunDate(date);
			} catch (ParseException e) {
				throw new IllegalStateException("invalid date format" + tags.get("DT"));
			}
		}
		if(tags.containsKey("FO")){
			NucleotideSequence flowOrder = new NucleotideSequenceBuilder(tags.get("FO")).build();
			builder.setFlowOrder(flowOrder);
		}
		if(tags.containsKey("KS")){
			NucleotideSequence keySequence = new NucleotideSequenceBuilder(tags.get("KS")).build();
			builder.setKeySequence(keySequence);
		}
		if(tags.containsKey("LB")){
			builder.setLibrary(tags.get("LB"));
		}
		if(tags.containsKey("PG")){
			builder.setPrograms(tags.get("PG"));
		}
		if(tags.containsKey("PI")){
			int insertSize = Integer.parseInt(tags.get("PI"));
			builder.setPredictedInsertSize(insertSize);
		}
		if(tags.containsKey("PL")){
			String value = tags.get("PL");
			PlatformTechnology platform = PlatformTechnology.valueOf(value);
			if(platform==null){
				throw new IllegalStateException("unknown platform " + value);
			}
			builder.setPlatform(platform);
		}
		if(tags.containsKey("PU")){
			builder.setPlatformUnit(tags.get("PU"));
		}
		if(tags.containsKey("SM")){
			builder.setSampleOrPoolName(tags.get("SM"));
		}
		
		headerBuilder.addReadGroup(builder.build());
		
	}

	private void handleSequenceDictionary(String line,
			Builder headerBuilder) {
		Map<String,String> tags = parseTags(line);
		
		String name = tags.get("SN");
		int length = Integer.parseInt(tags.get("LN"));
		ReferenceSequence.Builder builder = new ReferenceSequence.Builder(name, length);
		if(tags.containsKey("AS")){
			builder.setGenomeAssemblyId(tags.get("AS"));
		}
		if(tags.containsKey("M5")){
			builder.setMd5("M5");
		}
		if(tags.containsKey("SP")){
			builder.setSpecies(tags.get("SP"));
		}
		if(tags.containsKey("UR")){
			builder.setUri(tags.get("UR"));
		}
		
		headerBuilder.addReferenceSequence(builder.build());
		
	}
	
	private Map<String, String> parseTags(String line){
		Matcher matcher = HEADER_TAG_VALUE_PATTERN.matcher(line);
		Map<String, String> map = new HashMap<String, String>();
		while(matcher.find()){
			String tag = matcher.group(1);
			String value = matcher.group(2);
			map.put(tag, value);
		}
		return map;
	}

	private void handleHeaderLine(String firstLine,
			SamHeader.Builder headerBuilder) {
		Map<String,String> tags = parseTags(firstLine);
		//version required
		SamVersion version = SamVersion.parseVersion(tags.get(HEADER_VERSION_TAG));
		headerBuilder.setVersion(version);
		
		headerBuilder.setSortOrder(SortOrder.parseSortOrder(tags.get(HEADER_SORT_TAG)));
		
	}
	
	
	
	
	
}
