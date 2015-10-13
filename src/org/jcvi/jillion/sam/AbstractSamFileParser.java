/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.LineParser;
import org.jcvi.jillion.internal.sam.SamUtil;
import org.jcvi.jillion.sam.SamVisitor.SamVisitorCallback;
import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamHeaderBuilder;
import org.jcvi.jillion.sam.header.SamProgramBuilder;
import org.jcvi.jillion.sam.header.SamReadGroup.PlatformTechnology;
import org.jcvi.jillion.sam.header.SamReadGroupBuilder;
import org.jcvi.jillion.sam.header.SamReferenceSequenceBuilder;
import org.jcvi.jillion.sam.header.SamVersion;

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

	protected SamHeaderBuilder parseHeader(LineParser parser) throws IOException {
		
		SamHeaderBuilder headerBuilder = new SamHeaderBuilder();
		String currentLine = parser.peekLine();
		if(currentLine ==null){
			return headerBuilder;
		}
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
			SamHeaderBuilder headerBuilder) {
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
	
	void handleComment(String line, SamHeaderBuilder headerBuilder) {
		//trim off first 3 characters to get rid of @CO
		headerBuilder.addComment(line.substring(3).trim());
		
	}

	void handleProgram(String line, SamHeaderBuilder headerBuilder) {
		Map<String,String> tags = parseTags(line);
		String id = tags.get("ID");
		SamProgramBuilder builder = new SamProgramBuilder(id);
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

	void handleReadGroup(String line, SamHeaderBuilder headerBuilder) {
		Map<String,String> tags = parseTags(line);
		String id = tags.get("ID");
		SamReadGroupBuilder builder = new SamReadGroupBuilder(id);
		
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
				throw new IllegalStateException("invalid date format : " + tags.get("DT"), e);
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
			PlatformTechnology platform = PlatformTechnology.parse(value);
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
			SamHeaderBuilder headerBuilder) {
		Map<String,String> tags = parseTags(line);
		
		String name = tags.get("SN");
		int length = Integer.parseInt(tags.get("LN"));
		SamReferenceSequenceBuilder builder = new SamReferenceSequenceBuilder(name, length);
		if(tags.containsKey("AS")){
			builder.setGenomeAssemblyId(tags.get("AS"));
		}
		if(tags.containsKey("M5")){
			builder.setMd5(tags.get("M5"));
		}
		if(tags.containsKey("SP")){
			builder.setSpecies(tags.get("SP"));
		}
		if(tags.containsKey("UR")){
			builder.setUri(tags.get("UR"));
		}
		
		headerBuilder.addReferenceSequence(builder.build());
		
	}
	
	
	abstract static class AbstractCallback implements SamVisitorCallback{
		private final AtomicBoolean keepParsing;		
		

		public AbstractCallback(AtomicBoolean keepParsing) {
			this.keepParsing = keepParsing;
		}

		@Override
		public void haltParsing() {
			keepParsing.set(false);
			
		}
		
		public boolean keepParsing(){
			return keepParsing.get();
		}
		
	}
	
	public SamHeader getHeader() throws IOException {
		final SamHeader[] headerArray = new SamHeader[1];
		this.accept(new AbstractSamVisitor() {
			
			@Override
			public void visitHeader(SamVisitorCallback callback, SamHeader header) {
				headerArray[0] = header;
				callback.haltParsing();
				
			}
		
		});
		return headerArray[0];
	}

}
