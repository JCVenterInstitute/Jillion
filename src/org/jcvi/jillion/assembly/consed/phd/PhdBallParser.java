package org.jcvi.jillion.assembly.consed.phd;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.assembly.consed.phd.PhdBallVisitorCallback.PhdBallVisitorMemento;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;

/**
 * {@code PhdBallParser} can parse
 * {@literal phd.ball} files and individual phd files.
 * @author dkatzel
 *
 */
public abstract class PhdBallParser {

	 private static final String BEGIN_COMMENT = "BEGIN_COMMENT";
    private static final String END_SEQUENCE = "END_SEQUENCE";
    private static final String END_COMMENT = "END_COMMENT";
    
    private static final String BEGIN_DNA = "BEGIN_DNA";
    private static final String END_DNA = "END_DNA";
    
    private static final String BEGIN_TAG = "BEGIN_TAG";
    private static final String END_TAG = "END_TAG";

    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("^\\s*(\\w+):\\s+(.*?)$");
    private static final Pattern CALLED_INFO_PATTERN = Pattern.compile("^\\s*(\\w)\\s+(\\d+)\\s*(\\d+)?\\s*?");
    private static final Pattern BEGIN_SEQUENCE_PATTERN = Pattern.compile("BEGIN_SEQUENCE\\s+(\\S+)\\s*(\\d+)?\\s*$");
    private static final String BEGIN_WR = "WR{";
    private static final String END_WR = "}";

    private static final Pattern FILE_COMMENT_PATTERN = Pattern.compile("^#(.*)\\s*$");
	
    
    private static final Pattern RIGHT_TRIM_PATTERN = Pattern.compile("(.*)\\s+$");
	/**
	 * 
	 * @param phdBall
	 * @return
	 * @throws FileNotFoundException
	 */
	public static PhdBallParser create(File phdBall) throws FileNotFoundException{
		return new FileBasedPhdBallParser(phdBall);
	}
	public static PhdBallParser create(InputStream phdBallStream) throws FileNotFoundException{
		return new InputStreamBasedPhdBallParser(phdBallStream);
	}

	private PhdBallParser(){
		//can not instantiate outside of this .java file
	}
	
	
	public abstract void accept(PhdBallVisitor visitor) throws IOException;
	
	
	public abstract void accept(PhdBallVisitor visitor, PhdBallVisitorMemento memento) throws IOException;
	
	protected void accept(TextLineParser parser, PhdBallVisitor visitor) throws IOException{
		ParserState parserState = new ParserState();
		boolean seenFileComment=false;
		 PhdVisitor phdVisitor =null;
		while(parser.hasNextLine() && parserState.keepParsing()){
			
			long currentOffset = parser.getPosition();
			String line = parser.nextLine();
			Matcher beginSequenceMatcher = BEGIN_SEQUENCE_PATTERN.matcher(line);
			if(beginSequenceMatcher.matches()){
				if(phdVisitor !=null){
					phdVisitor.visitEnd();
				}
				//if the previous phdVisitor's visitEnd()
				//was just called, it may have used a callback
				//to halt parsing so check flag to see 
				//if we should still continue parsing/visiting
				if(!parserState.keepParsing()){
					phdVisitor=null; //set to null to avoid calling visitEnd() twice
					break;
				}
				String readId = beginSequenceMatcher.group(1);
				String optionalVersion = beginSequenceMatcher.group(2);
				PhdBallVisitorCallback callback = createCallback(parserState,currentOffset);
				if(optionalVersion ==null){
					phdVisitor = visitor.visitPhd(callback, readId, null);
				}else{
					phdVisitor =visitor.visitPhd(callback, readId, Integer.parseInt(optionalVersion));
				}
				if(phdVisitor ==null){
					skipSequence(parser);
				}else{
					handleSequence(parserState, parser, phdVisitor);
				}
			}else{				
				if(line.startsWith(BEGIN_WR)){
					handleWholeReadTag(parserState, parser, phdVisitor);
				}else if(!seenFileComment){
					Matcher fileCommentMatcher = FILE_COMMENT_PATTERN.matcher(line);
					if(fileCommentMatcher.matches()){
						seenFileComment=true;
						visitor.visitFileComment(fileCommentMatcher.group(1));
					}
				}
			}
		}
		if(parserState.keepParsing()){
			if(phdVisitor !=null){
				phdVisitor.visitEnd();
			}
			visitor.visitEnd();
		}else{
			if(phdVisitor !=null){
				phdVisitor.halted();
			}
			visitor.halted();
		}
	}
	
	private void skipSequence(TextLineParser parser) throws IOException {
		boolean entireSequenceBlockRead=false;
		while(entireSequenceBlockRead && parser.hasNextLine()){
			String line = parser.nextLine();
			entireSequenceBlockRead = line.startsWith(END_SEQUENCE);
		}
		
	}


	protected abstract  PhdBallVisitorCallback createCallback(ParserState parserState, long offset);

	private void handleWholeReadTag(ParserState parserState,
			TextLineParser parser, PhdVisitor visitor) throws IOException {
		final PhdWholeReadItemVisitor itemVisitor;
		if(visitor ==null){
			itemVisitor=null;
		}else{
			itemVisitor =visitor.visitWholeReadItem();
		}
		
		while(parser.hasNextLine() && parserState.keepParsing()){
			String line = parser.nextLine();
			if(line.startsWith(END_WR)){
				if(itemVisitor!=null){
					itemVisitor.visitEnd();
				}
				break;
			}
			if(itemVisitor !=null){
				itemVisitor.visitLine(rightTrim(line));
			}
		}
		if(itemVisitor !=null && !parserState.keepParsing()){
			visitor.halted();
		}
	}

	private String rightTrim(String line){
		Matcher matcher = RIGHT_TRIM_PATTERN.matcher(line);
		matcher.find();
		return matcher.group(1);
	}

	private void handleSequence(ParserState parserState, TextLineParser parser,
			PhdVisitor visitor) throws IOException {
		//format of each sequence is:
		//BEGIN_COMMENT
		//<comments>
		//END_COMMENT
		//BEGIN_DNA
		//<lines of base qual pos>
		//pos is now optional as of Consed 20.0 ?
		//END_DNA
		//BEGIN_TAG
		//<tag data>
		//END_TAG
		//..multiple tags allowed
		//END_SEQUENCE
		
		parseCommentBlock(parser, visitor);
		if(!parserState.keepParsing()){
			visitor.halted();
			return;
		}
		parseReadData(parserState, parser, visitor);
		
		if(!parserState.keepParsing()){
			visitor.halted();
			return;
		}
		parseTags(parserState, parser, visitor);
		if(!parserState.keepParsing()){
			visitor.halted();
			return;
		}
		
	}

	private void parseTags(ParserState parserState, TextLineParser parser,
			PhdVisitor visitor) throws IOException {
		while(parser.hasNextLine() && parserState.keepParsing()){
			String line = parser.nextLine();
			if(line.startsWith(END_SEQUENCE)){
				//no tags
				return;
			}
			if(line.startsWith(BEGIN_TAG)){
				parseSingleTag(parserState, parser, visitor.visitReadTag());
			}
		}
		
		
		
	}


	private void parseSingleTag(ParserState parserState, TextLineParser parser,
			PhdReadTagVisitor visitor) throws IOException {
		boolean inTag=true;
		do{
			String line = parser.nextLine();
			if(line.startsWith(END_TAG)){
				inTag=false;
			}else{
				Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(line);
				if(keyValueMatcher.matches()){
					String key = keyValueMatcher.group(1);
					String value = keyValueMatcher.group(2);
					if("TYPE".equals(key)){
						visitor.visitType(value);
					}else if("SOURCE".equals(key)){
						visitor.visitSource(value);
					}else if("UNPADDED_READ_POS".equals(key)){
						//use tokenizer instead of Scanner
						//for performance improvement
						StringTokenizer tokenizer = new StringTokenizer(value);						
						visitor.visitUngappedRange(Range.of(
								Range.CoordinateSystem.RESIDUE_BASED,
								Integer.valueOf(tokenizer.nextToken()),
								Integer.valueOf(tokenizer.nextToken())));
					}else if("DATE".equals(key)){
						try {
							visitor.visitDate(PhdUtil.parseReadTagDate(value));
						} catch (ParseException e) {
							throw new IOException("error parsing read tag date: " + value, e);
						}
					}else{
						//unrecognized key-value pair
						//could be free-form misc data that happened to be in key:value format?
						visitor.visitFreeFormData(rightTrim(line));
					}
				}else{
					//not a key value pair
					if(line.startsWith(BEGIN_COMMENT)){
						visitor.visitComment( parseReadTagComment(parser));
					}else{
						//free form misc data?
						visitor.visitFreeFormData(rightTrim(line));
					}
				}
			}
		}while(inTag && parser.hasNextLine() && parserState.keepParsing());
		if(!parserState.keepParsing()){
			visitor.halted();
		}else{
			visitor.visitEnd();
		}
	}

	private String parseReadTagComment(TextLineParser parser) throws IOException{
		boolean inCommentBlock=true;
		StringBuilder comment = new StringBuilder();
		do{
			String line = parser.nextLine();
			if(line.startsWith(END_COMMENT)){
				inCommentBlock=false;
			}else{
				comment.append(line);
			}
		}while(inCommentBlock && parser.hasNextLine());
		return comment.toString();
	}

	private void parseReadData(ParserState parserState, TextLineParser parser, PhdVisitor visitor) throws IOException {
		boolean inDnaBlock =false;
		while(!inDnaBlock && parser.hasNextLine()){
			String line = parser.nextLine();
			inDnaBlock = line.startsWith(BEGIN_DNA);
		}
		
		do{
			String line = parser.nextLine();
			Matcher matcher = CALLED_INFO_PATTERN.matcher(line);
			if(matcher.matches()){
				Nucleotide base = Nucleotide.parse(matcher.group(1).charAt(0));
				PhredQuality qual = PhredQuality.valueOf(Integer.parseInt(matcher.group(2)));
				if(matcher.group(3)==null){
					visitor.visitBasecall(base, qual, null);
				}else{
					visitor.visitBasecall(base, qual, Integer.parseInt(matcher.group(3)));
				}
			}else{
				inDnaBlock = !line.startsWith(END_DNA);
			}
		}while(inDnaBlock && parser.hasNextLine() && parserState.keepParsing());
	}


	private void parseCommentBlock(TextLineParser parser, PhdVisitor visitor) throws IOException {
		boolean inCommentBlock =false;
		while(!inCommentBlock && parser.hasNextLine()){
			String line = parser.nextLine();
			inCommentBlock = line.startsWith(BEGIN_COMMENT);
		}
		Map<String, String> comments = parseComments(parser);
		
		visitor.visitComments(comments);
	}


	private Map<String, String> parseComments(TextLineParser parser) throws IOException {
		boolean inCommentBlock=true;
		Map<String, String> comments = new LinkedHashMap<String, String>();
		do{
			String line = parser.nextLine();
			if(line.startsWith(END_COMMENT)){
				inCommentBlock=false;
			}else{
				Matcher commentMatcher = KEY_VALUE_PATTERN.matcher(line);
	            if(commentMatcher.find()){
	            	comments.put(commentMatcher.group(1), commentMatcher.group(2));
	            }
			}
		}while(inCommentBlock && parser.hasNextLine());
		return comments;
	}

	private static class ParserState{
		private final AtomicBoolean keepParsing;
		
		public ParserState(){
			keepParsing = new AtomicBoolean(true);
		}
		
		public boolean keepParsing(){
			return keepParsing.get();
		}
		
		public void haltParsing(){
			keepParsing.set(false);
		}
	}
	
	private static class MementoedPhdBallVisitorCallbackImpl implements PhdBallVisitorCallback{

		private final long byteOffset;
		private final ParserState parserState;
		
		public MementoedPhdBallVisitorCallbackImpl(long byteOffset,
				ParserState parserState) {
			this.byteOffset = byteOffset;
			this.parserState = parserState;
		}

		@Override
		public boolean canCreateMemento() {
			return true;
		}

		@Override
		public PhdBallVisitorMemento createMemento() {
			return new PhdBallVisitorMementoImpl(byteOffset);
		}

		@Override
		public void haltParsing() {
			parserState.haltParsing();			
		}
		
	}
	
	private static class NoMementoPhdBallVisitorCallbackImpl implements PhdBallVisitorCallback{

		private final ParserState parserState;
		
		public NoMementoPhdBallVisitorCallbackImpl(ParserState parserState) {
			this.parserState = parserState;
		}

		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public PhdBallVisitorMemento createMemento() {
			throw new UnsupportedOperationException("can not create mementos from inputstream");
		}

		@Override
		public void haltParsing() {
			parserState.haltParsing();			
		}
		
	}
	
	private static class PhdBallVisitorMementoImpl implements PhdBallVisitorMemento{
		private final long offset;

		
		public PhdBallVisitorMementoImpl(long offset) {
			this.offset = offset;
		}
		
		public final long getOffset() {
			return offset;
		}

	}
	
	private static class FileBasedPhdBallParser extends PhdBallParser{
		private final File phdBall;
		
		private FileBasedPhdBallParser(File phdBall) throws FileNotFoundException{
			if(phdBall ==null){
				throw new NullPointerException("phdball can not be null");
			}
			if(!phdBall.exists()){
				throw new FileNotFoundException("phdball must exist");
			}
			this.phdBall = phdBall;
		}
		
		public void accept(PhdBallVisitor visitor) throws IOException{
			if(visitor==null){
				throw new NullPointerException("visitor can not be null");
			}
			TextLineParser parser =null;
			try{
				parser = new TextLineParser(new BufferedInputStream(new FileInputStream(phdBall)));
				accept(parser, visitor);
			}finally{
				IOUtil.closeAndIgnoreErrors(parser);
			}
		}
		
		
		public void accept(PhdBallVisitor visitor, PhdBallVisitorMemento memento) throws IOException{
			if(visitor ==null){
	            throw new NullPointerException("visitor can not be null");
	        }
	        if(memento ==null){
	            throw new NullPointerException("memento can not be null");
	        }
		    if(!(memento instanceof PhdBallVisitorMementoImpl)){
		    	throw new IllegalArgumentException("unknown memento type " + memento);
		    }
		    long offset = ((PhdBallVisitorMementoImpl)memento).getOffset();
		    //TODO add check to make sure its the same parser object?
	        TextLineParser parser=null;
	        try{
		        InputStream in = new RandomAccessFileInputStream(phdBall, offset);
		        
		        parser = new TextLineParser(in, offset);
		        accept(parser, visitor);
	        }finally{
	        	IOUtil.closeAndIgnoreErrors(parser);
	        }
		}
		
		protected PhdBallVisitorCallback createCallback(ParserState parserState, long offset) {
			return new MementoedPhdBallVisitorCallbackImpl(offset, parserState);
		}
	}
	
	private static final class InputStreamBasedPhdBallParser extends PhdBallParser{

		private final OpenAwareInputStream in;
    	
		public InputStreamBasedPhdBallParser(InputStream in) {
			if(in ==null){
				throw new NullPointerException("input stream can not be null");
			}
			this.in = new OpenAwareInputStream(new BufferedInputStream(in));
		}

		@Override
		public void accept(PhdBallVisitor visitor) throws IOException {
			if(visitor==null){
				throw new NullPointerException("visitor can not be null");
			}
			if(!in.isOpen()){
				throw new IllegalStateException("inputstream has been closed");
			}
			TextLineParser parser =null;
			try{
				parser = new TextLineParser(in);
				accept(parser, visitor);
			}finally{
				IOUtil.closeAndIgnoreErrors(parser);
			}
			
		}

		@Override
		public void accept(PhdBallVisitor visitor,
				PhdBallVisitorMemento memento) throws IOException {
			throw new UnsupportedOperationException("mementos not supported");
			
		}

		@Override
		protected PhdBallVisitorCallback createCallback(
				ParserState parserState, long offset) {
			return new NoMementoPhdBallVisitorCallbackImpl(parserState);
		}
		
		
		
	}
}
