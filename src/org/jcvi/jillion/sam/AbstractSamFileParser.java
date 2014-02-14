package org.jcvi.jillion.sam;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.TextLineParser;
import org.jcvi.jillion.sam.header.ReadGroup;
import org.jcvi.jillion.sam.header.ReadGroup.PlatformTechnology;
import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamHeader.Builder;
import org.jcvi.jillion.sam.header.SamProgram;
import org.jcvi.jillion.sam.header.SamVersion;
import org.jcvi.jillion.sam.header.SortOrder;

abstract class AbstractSamFileParser implements SamParser{

	static final String COMMENT_KEY = "@CO";

	static final String PROGRAM_KEY = "@PG";

	static final String READ_GROUP_KEY = "@RG";

	static final String SEQUENCE_DICTIONARY_KEY = "@SQ";



	static final String HEADER_KEY = "@HD";
	private static final String HEADER_VERSION_TAG = "VN";
	private static final String HEADER_SORT_TAG = "SO";
	
	private static final Pattern HEADER_TAG_VALUE_PATTERN = Pattern.compile("([A-Za-z][A-Za-z0-9]):([ -~]+)");
	
	
	public AbstractSamFileParser() {
		super();
	}

	protected SamHeader.Builder parseHeader(TextLineParser parser) throws IOException {
		
		SamHeader.Builder headerBuilder = new SamHeader.Builder();
		String currentLine = parser.peekLine();
		if(currentLine.startsWith(HEADER_KEY)){
			handleHeaderLine(currentLine, headerBuilder);
			//actually consume the line we just peeked.
			parser.nextLine();
			//next line			
			currentLine = parser.peekLine();
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
				//actually consume the line we just peeked.
				parser.nextLine();
				currentLine = parser.peekLine();
			}
		}
		return headerBuilder;
	}

	private void handleHeaderLine(String firstLine,
			SamHeader.Builder headerBuilder) {
		Map<String,String> tags = parseTags(firstLine);
		//version required
		SamVersion version = SamVersion.parseVersion(tags.get(HEADER_VERSION_TAG));
		headerBuilder.setVersion(version);
		
		headerBuilder.setSortOrder(SortOrder.parseSortOrder(tags.get(HEADER_SORT_TAG)));
		
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
	
	void handleComment(String line, Builder headerBuilder) {
		//trim off first 3 characters to get rid of @CO
		headerBuilder.addComment(line.substring(3).trim());
		
	}

	void handleProgram(String line, Builder headerBuilder) {
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

	void handleReadGroup(String line, Builder headerBuilder) {
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
				Date date = SamUtil.toDate(tags.get("DT"));
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

	void handleSequenceDictionary(String line,
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
	
}