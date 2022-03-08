package org.jcvi.jillion.vcf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jcvi.jillion.core.io.InputStreamSupplier;
import org.jcvi.jillion.core.util.streams.ThrowingSupplier;
import org.jcvi.jillion.internal.core.io.TextLineParser;
import org.jcvi.jillion.vcf.VcfVisitor.VcfMemento;
import org.jcvi.jillion.vcf.VcfVisitor.VcfVisitorCallback;

import lombok.AllArgsConstructor;
import lombok.Data;

public class VcfFileParser implements VcfParser{

	private static final Pattern META_PATTERN = Pattern.compile("^##(\\S+)=(\\S+)$"); 
	
	private static final Pattern FILTER_PATTERN = Pattern.compile("^##FILTER=<ID=(\\S+),Description=\"(.+)\">$"); 
	
	private static final Pattern FORMAT_PATTERN = Pattern.compile("^##FORMAT=<(.+)>$"); 
	private static final Pattern INFO_PATTERN = Pattern.compile("^##INFO=<(.+)>$"); 
	private static final Pattern CONTIG_PATTERN = Pattern.compile("^##contig=<(.+)>$"); 
	
	private static final  Pattern DESCRIPTION_PATTERN = Pattern.compile("Description=\"(.+?)\"");
	//This isn't actually good enough because a description might have a comma so we have to check for that
	//but it's a very complicated step
	private static final Pattern INNER_PATTERN = Pattern.compile("(\\s*,\\s*)?(.+?)=([^,]+)");
	
	
	private static final Pattern TAB_PATTERN = Pattern.compile("\t");
	
	
	private final InputStreamSupplier inputStreamSupplier;
	
	private static final String DESCRIPTION = "Description";
	
	private VcfFileParser(InputStreamSupplier inputStreamSupplier) {
		this.inputStreamSupplier = Objects.requireNonNull(inputStreamSupplier);
	}
	
	public static VcfParser createParserFor(File f) throws IOException{
		return createParserFor(InputStreamSupplier.forFile(f));
	}

	public static VcfParser createParserFor(InputStreamSupplier inputStreamSupplier) {
		return new VcfFileParser(inputStreamSupplier);
	}
	
	private abstract class AbstractParserCallback implements VcfVisitorCallback{

		private volatile boolean halt=false;
		private Long position=null;
		
		private Class<? extends VcfStateMachine> momentoClass = BEGIN.class;
		
		@Override
		public void haltParsing() {
			halt=true;
			
		}
		
		
		
		public Long getPosition() {
			return position;
		}



		public void setPosition(Long position) {
			this.position = position;
		}



		public boolean wasHalted() {
			return halt;
		}

		public Class<? extends VcfStateMachine> getMomentoClass() {
			return momentoClass;
		}

		public void setMomentoClass(Class<? extends VcfStateMachine> momentoClass) {
			this.momentoClass = momentoClass;
		}
		
		
		
		
		
		
	}
	private class ParserCallback extends AbstractParserCallback{

		private final TextLineParser lineParser;
		
		
		
		public ParserCallback(TextLineParser lineParser) {
			this.lineParser = lineParser;
		}

		@Override
		public boolean canCreateMemento() {
			return lineParser.tracksPosition();
		}

		@Override
		public VcfMemento createMemento() {
			if(! canCreateMemento()) {
				throw new UnsupportedOperationException("momento not supported");
			}
			return new Memento(getPosition()==null? lineParser.getPosition(): getPosition(), getMomentoClass());
		}
		
		
		
	}
	
	@Data
	@AllArgsConstructor
	private static class Memento implements VcfMemento{
		private long offset;
		private Class<? extends VcfStateMachine> momentoClass;
		
		
		
		
	}
	private class NoMementoParserCallback extends AbstractParserCallback{

		
		

		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public VcfMemento createMemento() {
			throw new UnsupportedOperationException("momento not supported");
		}
		
		
		
	}

	
	private static Map<String, String> handleTypedDataLine(String betweenAngleBrackets) {
		
		Map<String, String> map = new HashMap<>();
		
		Matcher descriptionMatcher = DESCRIPTION_PATTERN.matcher(betweenAngleBrackets);
		String restOfInput = betweenAngleBrackets;
		if(descriptionMatcher.find()) {
			//found a description
			String description = descriptionMatcher.group(1);
			restOfInput = new StringBuilder(betweenAngleBrackets)
								.replace(descriptionMatcher.start(), descriptionMatcher.end(), "")
								.toString();
			map.put(DESCRIPTION, description);
		}
		Matcher m = INNER_PATTERN.matcher(restOfInput);
		while(m.find()) {
			//group 1 will be the comma we are skipping over
			String key = m.group(2);
			String value = m.group(3);
			map.put(key, value);
		}
		return map;
	}
	
	private interface VcfStateMachine{
		default boolean keepParsing() {
			return true;
		}
		VcfStateMachine parse(TextLineParser parser, VcfVisitor visitor, AbstractParserCallback callback) throws Exception;
		
		public Class<? extends VcfStateMachine> getInstanceClass();
	}
	public static class HEADER implements VcfStateMachine{

		
		
		public HEADER(String headerLine, VcfVisitor visitor, AbstractParserCallback callback, long startOfHeaderPosition) {
			String[] fields = TAB_PATTERN.split(headerLine);
			//there are 9 mandatory fields but there might be others
			
			List<String> extraFields = Arrays.stream(fields).skip(9).collect(Collectors.toList());
			callback.setMomentoClass(BEGIN.class);
			callback.setPosition(startOfHeaderPosition);
			visitor.visitHeader(callback, extraFields);
		}


		@Override
		public VcfStateMachine parse(TextLineParser parser, VcfVisitor visitor, AbstractParserCallback callback) {
			
			callback.setMomentoClass(DATA.class);
			callback.setPosition(parser.getPosition());
			return new DATA();
		}


		@Override
		public Class<? extends VcfStateMachine> getInstanceClass() {
			return HEADER.class;
		}
	}
	
	public static class DATA implements VcfStateMachine{
		
		@Override
		public Class<? extends VcfStateMachine> getInstanceClass() {
			return DATA.class;
		}

		@Override
		public VcfStateMachine parse(TextLineParser parser, VcfVisitor visitor, AbstractParserCallback callback) throws Exception {
			String line;
			
			callback.setPosition(parser.getPosition());
			while(!callback.wasHalted() && (line =parser.nextLine()) !=null) {
				String trimmed = line.trim();
				String[] fields = TAB_PATTERN.split(trimmed);
				//there are 9 mandatory fields but there might be others
				
				if(fields.length>9) {
					List<String> extraFields = Arrays.stream(fields).skip(9).collect(Collectors.toList());
					
					visitor.visitData(callback, 
							fields[0], Integer.parseInt(fields[1]), 
							fields[2], fields[3], fields[4],
							Integer.parseInt(fields[5]),
							fields[6], fields[7], fields[8], extraFields);
				}else {
					visitor.visitData(callback, 
							fields[0], Integer.parseInt(fields[1]), 
							fields[2], fields[3], fields[4],
							Integer.parseInt(fields[5]),
							fields[6], fields[7], fields[8], Collections.emptyList());
				}
				callback.setPosition(parser.getPosition());
			}
			
			return new END(visitor, callback);
		}
		
	}
	
	public static class END implements VcfStateMachine{

		END( VcfVisitor visitor, AbstractParserCallback callback){
			if(callback.wasHalted()) {
				visitor.halted();
			}else {
				visitor.visitEnd();
			}
		}
		
		
		@Override
		public boolean keepParsing() {
			return false;
		}

		@Override
		public Class<? extends VcfStateMachine> getInstanceClass() {
			return END.class;
		}

		@Override
		public VcfStateMachine parse(TextLineParser parser, VcfVisitor visitor, AbstractParserCallback callback) {
			
			return null;
		}
		
	}
		
	public static class BEGIN implements VcfStateMachine{
		public BEGIN() {
			//needed for reflection getConstructor?
		}
		@Override
		public Class<? extends VcfStateMachine> getInstanceClass() {
			return BEGIN.class;
		}
		
		@Override
		public VcfStateMachine parse(TextLineParser parser, VcfVisitor visitor, AbstractParserCallback callback) throws IOException {
			// parse the header
			String line;
			long oldPosition=parser.getPosition();
			
			while( (line =parser.nextLine()) !=null) {
				
				//I think we still want trim() here not strip() ? do we have unicode spaces in vcf ?
				String trimmed = line.trim();
				long currentPosition=parser.getPosition();
				if(trimmed.startsWith("##")) {
					if(trimmed.startsWith("##INFO=")) {
						Matcher m = INFO_PATTERN.matcher(trimmed);
						if(m.find()) {
							Map<String, String> map = handleTypedDataLine(m.group(1));
							VcfValueType type = VcfValueType.valueOf(map.get("Type"));
							
							visitor.visitInfo(callback,  map.get("ID"), type,
									VcfNumber.parse(map.get("Number")), 
									map.get(DESCRIPTION), map);
						}
					}else if(trimmed.startsWith("##FILTER=")) {
						Matcher m = FILTER_PATTERN.matcher(trimmed);
						if(m.matches()) {
							visitor.visitFilter(callback, m.group(1), m.group(2));
						}else {
							throw new IOException("invalid FILTER LINE '" + trimmed + "'");
						}
					}else if(trimmed.startsWith("##FORMAT=")) {
						Matcher m = FORMAT_PATTERN.matcher(trimmed);
						if(m.find()) {
							Map<String, String> map = handleTypedDataLine(m.group(1));
							
							VcfValueType type = VcfValueType.valueOf(map.get("Type"));
							

							visitor.visitFormat(callback, map.get("ID"),
									type , VcfNumber.parse(map.get("Number")), map.get(DESCRIPTION), map);
						}
					}else if(trimmed.startsWith("##contig=")) {
						
						Matcher m = CONTIG_PATTERN.matcher(trimmed);
						if(m.find()) {
							Map<String, String> map = handleTypedDataLine(m.group(1));
							Long length=null;
							try {
								length = Long.parseLong(map.get("length"));
							}catch(NumberFormatException e) {
								//ignore?
							}
							visitor.visitContigInfo(callback, map.get("ID"), length, map);
						}
					}else {
						//handle meta
						Matcher m = META_PATTERN.matcher(trimmed);
						if(m.matches()) {
							visitor.visitMetaInfo(callback, m.group(1), m.group(2));
						}
					}
				}else if(trimmed.startsWith("#CHROM")) {
					//header line
					return new HEADER(trimmed, visitor, callback, oldPosition);
				}
				
				if(callback.wasHalted()) {
					break;
				}
				//update old Position so we know where the beginning of line is
				oldPosition=currentPosition;
			}
		//if we get here we've reached end of file without a header ?
			return new END(visitor, callback);
			
		}
		
		
		
		
		
	}
	@Override
	public void parse(VcfVisitor visitor, VcfMemento momento) throws IOException {
		Objects.requireNonNull(momento, "momento can not be null");
		Objects.requireNonNull(visitor, "visitor can not be null");
		
		if(!(momento instanceof Memento)) {
			throw new IllegalArgumentException("momento must be created by this parser class"); 
		}
		Memento myMomento = (Memento) momento;
		VcfStateMachine currentState; 
		try {
			Constructor<? extends VcfStateMachine> declaredConstructor = myMomento.getMomentoClass().getConstructor();
			currentState = declaredConstructor.newInstance();
		} catch (Exception e) {
			throw new IOException("error creating state machine from momento: " + e.getMessage(), e);
		} 
		
		parse(visitor, currentState, ()->inputStreamSupplier.get(myMomento.getOffset()));
		
	}
	@Override
	public void parse(VcfVisitor visitor) throws IOException {
		
		parse(visitor, new BEGIN(), inputStreamSupplier);
		
	}

	private void parse(VcfVisitor visitor, VcfStateMachine state, ThrowingSupplier<InputStream, IOException> inputStreamSupplier) throws IOException {
		Objects.requireNonNull(visitor, "visitor can not be null");
		
		try(TextLineParser parser = new TextLineParser(inputStreamSupplier.get())) {
			
			AbstractParserCallback callback = createCallback(parser);
			
			callback.setMomentoClass(state.getInstanceClass());
			try {
				while(state.keepParsing()) {
					
					state = state.parse(parser, visitor, callback);
				}
			}catch(Exception e) {
				if(e instanceof IOException) {
					throw (IOException) e;
				}
				throw new IOException("error parsing vcf: " + e.getMessage(), e);
				
			}
			
		}
	}

	/**
	 * Create a Callback implementation that either can or can not
	 * make momentos depending on the inputStreamSupplier and line parser implementation.
	 * @param parser the parser we are using.
	 * @return a new {@link AbstractParserCallback} implementation.
	 */
	private AbstractParserCallback createCallback(TextLineParser parser) {
		if( inputStreamSupplier.isReReadable() && parser.tracksPosition()) {
			return new ParserCallback(parser);
		}
		return new NoMementoParserCallback();
		
	}

	
}
