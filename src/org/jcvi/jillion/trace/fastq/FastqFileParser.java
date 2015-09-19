/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.fastq;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.io.InputStreamSupplier;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback;
import org.jcvi.jillion.trace.fastq.FastqVisitor.FastqVisitorCallback.FastqVisitorMemento;
/**
 * {@code FastqFileParser}  will parse a single 
 * fastq encoded file and call the appropriate
 * visitXXX methods on the given {@link FastqVisitor}.
 * @author dkatzel
 *
 */
abstract class FastqFileParser implements FastqParser{

	private static final Pattern CASAVA_1_8_DEFLINE_PATTERN = Pattern.compile("^@(\\S+\\s+\\d:[N|Y]:\\d+:(\\S+)?)\\s*$");
	
	private final StringBuilder sequenceBuilder = new StringBuilder(2000);
	private final StringBuilder qualityBuilder = new StringBuilder(2000);
	
	private final boolean hasComments;
	
	private final  boolean multiLine;
	
	
	/**
	 * Create a new {@link FastqFileParser} instance
	 * that will parse the given fastq encoded
	 * inputStream that does not contain defline comments.  Please Note that inputStream implementations
	 * of the FastqFileParser can not create {@link FastqVisitorMemento}s
	 * or use {@link #accept(FastqVisitor, FastqVisitorMemento)}
	 * method.
	 * 
	 * @apiNote this is the same as {@link #create(InputStream, boolean) create(in, false)}
	 * 
	 * @param in the fastq encoded inputstream to parse.
	 * @throws NullPointerException if inputstream is null.
	 * 
	 * @return a new {@link FastqParser} instance; will never be null.
	 */
	public static FastqParser create(InputStream in){
		return create(in, false, true);
	}
	
	/**
         * Create a new {@link FastqFileParser} instance
         * that will parse the given fastq encoded
         * inputStream.  Please Note that inputStream implementations
         * of the FastqFileParser can not create {@link FastqVisitorMemento}s
         * or use {@link #accept(FastqVisitor, FastqVisitorMemento)}
         * method.
         * @param in the fastq encoded inputstream to parse.
         * @param hasComments do the deflines of the sequences contain comments.  If 
         * set to {@code true} then a more computationally intensive parsing is performed
         * to try to distinguish the id from the comment.  Remember some Fastq id's can have spaces
         * which makes comment detection difficult and complex.
         * 
         * @throws NullPointerException if inputstream is null.
         * 
         * @return a new {@link FastqParser} instance; will never be null.
         */
        static FastqParser create(InputStream in, boolean hasComments, boolean multiLine){
                return new InputStreamFastqFileParser(in, hasComments, multiLine);
        }
        
        
        static FastqFileParser create(InputStreamSupplier supplier, boolean hasComments, boolean multiLine) throws IOException{
            return new FileBasedFastqFileParser(supplier,hasComments, multiLine);
        }
        
	private FastqFileParser(boolean hasComments, boolean multiLine){
		this.hasComments = hasComments;
		this.multiLine = multiLine;
	}
	
	protected void parseFastqFile(FastqVisitor visitor, TextLineParser parser) throws IOException{
		ParserState parserState = new ParserState(parser.getPosition());
		while(parserState.keepParsing() && parser.hasNextLine()){
			parserState=parseNextRecord(visitor, parser, parserState);
		}
		if(parserState.keepParsing()){
			visitor.visitEnd();
		}else{
			visitor.halted();
		}
	}
	
	private ParserState parseNextRecord(FastqVisitor visitor, TextLineParser parser, ParserState parserState) throws IOException{
		String deflineText = parser.nextLine();
		 AbstractFastqVisitorCallback callback = createCallback(parserState);
		 FastqRecordVisitor recordVisitor;
		 String id;
		if(hasComments){
    		    Defline defline = Defline.parse(deflineText);
    		    id = defline.getId();
    		
                   recordVisitor= visitor.visitDefline(callback, id, defline.getComment());
		}else{
		    if(deflineText.charAt(0) != '@'){
		        throw new IllegalStateException(String.format("invalid fastq file, could not parse seq id from '%s'",deflineText));
		            
		    }
		    id = deflineText.substring(1).trim();
		    recordVisitor= visitor.visitDefline(callback, id, null);
		}
            if(!parserState.keepParsing()){
            	return parserState;
            }
            return parseRecordBody(parser,recordVisitor,parserState, id);		
        
	}
	
	private ParserState parseRecordBody(TextLineParser parser,
			FastqRecordVisitor recordVisitor, ParserState parserState, String currentId) throws IOException {
		//if we aren't visiting this read
		//we shouldn't spend any time parsing the
		//bases or qualities	
		if(recordVisitor ==null){
			skipCurrentRecord(parser);
			//set new end position for mementos to work
			return parserState.setOffset(parser.getPosition());
		}
		
		//default to 2000 bp since most sequences are only that much anyway
        //builder will grow if we get too big
        
        String line = parser.nextLine();
        if(line ==null){
        	//end of file before we got enough sequence
			throw new IOException(
    				String.format("unexpected end of file. no sequence for current record '%s'",currentId));
        }
    	sequenceBuilder.append(line.trim());
    	boolean inBasecallBlock = multiLine;
        while(inBasecallBlock){
        	line = parser.nextLine();
        	inBasecallBlock = notQualityDefLine(line);
        	if(inBasecallBlock){
        		sequenceBuilder.append(line.trim());
        	}
        }
        
       
        recordVisitor.visitNucleotides(sequenceBuilder.toString());
        
        if(!parserState.keepParsing()){
            recordVisitor.halted();
            return parserState.setOffset(parser.getPosition());
        }
        if(!multiLine){
            //read qual defline
            String qualDefline=parser.nextLine();
            //should start with +
            if(qualDefline ==null || qualDefline.charAt(0) != '+'){
                throw new IOException("invalid quality defline. should start with '+' but was " + qualDefline);
            }
        }
        //now parse the qualities
        int expectedQualities =  (int)sequenceBuilder.length();
        
        //clear builders
        sequenceBuilder.setLength(0);
        
        qualityBuilder.setLength(0);
        //needs to be a do-while loop
        //to cover the case where the read is empty
        //(contains 0 bases) we still need to read a quality line
        do{    	
    		line = parser.nextLine();
    		if(line ==null){
    			//end of file before we got enough qualities
    			throw new IOException(
        				String.format("too few quality values for current record '%s' : "
        						+ "expected %d but was %d", currentId, expectedQualities, qualityBuilder.length()));
        	
    		}
    		qualityBuilder.append(line.trim());
    	}while(qualityBuilder.length() < expectedQualities);
        
        
    	if(qualityBuilder.length()> expectedQualities){
    		//we actually might have read too much and are somewhere inside the next 
    		//record 
    		//(reading the defline and possibly even the bases line of the next record)
    		
    		throw new IOException(
    				String.format("incorrect number of quality values for current record: expected %d "
    						+ "but was %d if there are too few qualities the parser may have "
    						+ "read into the next record", expectedQualities, qualityBuilder.length()));
    	}
    	recordVisitor.visitEncodedQualities(qualityBuilder.toString());
    	
		ParserState endParserState = parserState.setOffset(parser.getPosition());
		if (endParserState.keepParsing()){
			recordVisitor.visitEnd();
		}else{
			recordVisitor.halted();
		}

		return endParserState;
	}
	private void skipCurrentRecord(TextLineParser parser) throws IOException {
        
	    if(multiLine){
		String line = parser.nextLine();
		int numberOfBasesSeen=0;
     	
		while(notQualityDefLine(line)){
			//still in bases 
			numberOfBasesSeen += line.trim().length();
			line = parser.nextLine();
		}
		
		//handle special case of empty read
		if(numberOfBasesSeen==0){
			//skip blank line
			parser.nextLine();
			return;
		}
		int numberOfQualitiesLeft= numberOfBasesSeen;
		while(numberOfQualitiesLeft>0){
			line = parser.nextLine();
			numberOfQualitiesLeft -= line.trim().length();
		}
		//be consistent with errors if too many 
		//qualities
		if(numberOfQualitiesLeft< 0 ){
    		    throw new IOException(
    				String.format("too many quality values for current record: expected %d but was %d", 
    						numberOfBasesSeen, 
    						numberOfBasesSeen - numberOfQualitiesLeft));
    	        }
	    }else{
	        parser.nextLine(); //bases
	        parser.nextLine(); //qual defline
	        parser.nextLine(); // qualities
	    }
		
	}
	private boolean notQualityDefLine(String line) {
		return line.charAt(0) !='+';
	}

	protected abstract AbstractFastqVisitorCallback createCallback(ParserState parserState);
	
	
	
	
	private static final class Defline{
		private final String id,comment;

		private Defline(String id, String comment) {
			this.id = id;
			this.comment = comment;
		}
		
		public static Defline parse(String fastqDefline){
			Matcher casava18Matcher = CASAVA_1_8_DEFLINE_PATTERN.matcher(fastqDefline);
			if(casava18Matcher.matches()){
				return new Defline(casava18Matcher.group(1),null);
			}
			Matcher beginSeqMatcher =FastqUtil.SEQ_DEFLINE_PATTERN.matcher(fastqDefline);
	        if(!beginSeqMatcher.find()){
	            throw new IllegalStateException(String.format("invalid fastq file, could not parse seq id from '%s'",fastqDefline));
	        }
	        return new Defline(beginSeqMatcher.group(1), beginSeqMatcher.group(3));
		}
		public String getId() {
			return id;
		}

		public String getComment() {
			return comment;
		}

	}
	
	private abstract static class AbstractFastqVisitorCallback implements FastqVisitorCallback{
		private final ParserState parserState;
		
		
		public AbstractFastqVisitorCallback(ParserState parserState) {
			this.parserState = parserState;
		}

		@Override
		public void haltParsing() {
			parserState.stopParsing();
			
		}

		final ParserState getParserState() {
			return parserState;
		}

		
		
	}
	
	private static class NoMementoCallback extends AbstractFastqVisitorCallback{

		
		
		public NoMementoCallback(ParserState parserState) {
			super(parserState);
		}

		@Override
		public boolean canCreateMemento() {
			return false;
		}

		@Override
		public FastqVisitorMemento createMemento() {
			throw new UnsupportedOperationException("can not create memento");
		}
		
	}
	
	private static class MementoCallback extends AbstractFastqVisitorCallback{
		
		public MementoCallback(ParserState parserState){
			super(parserState);
		}

		@Override
		public boolean canCreateMemento() {
			return true;
		}

		@Override
		public FastqVisitorMemento createMemento() {
			return OffsetMemento.valueOf(getParserState().getCurrentOffset());
			//return new LongOffsetMemento(getParserState().getCurrentOffset());
		}
		
	}

	
	
	private static class ParserState{
		private final long currentOffset;
		private final AtomicBoolean keepParsing;
		
		
		ParserState(long startOffset){
			this(startOffset, new AtomicBoolean(true));
		}
		

		public final long getCurrentOffset() {
			return currentOffset;
		}
		private ParserState(long startOffset, AtomicBoolean keepParsing){
			this.currentOffset = startOffset;
			this.keepParsing = keepParsing;
		}
		
		void stopParsing(){
			keepParsing.set(false);
		}
		
		public boolean keepParsing(){
			return keepParsing.get();
		}
		
		ParserState setOffset(long newOffset){
			return new ParserState(newOffset, keepParsing);
		}
	}
	
	private static class FileBasedFastqFileParser extends FastqFileParser{
		private final InputStreamSupplier supplier;
		
		
		public FileBasedFastqFileParser(InputStreamSupplier supplier, boolean hasComments, boolean multiLine) throws IOException {
		    super(hasComments, multiLine);
		    Objects.requireNonNull(supplier);
			
			this.supplier=supplier;
		}


		@Override
		public boolean canCreateMemento() {
			return true;
		}


		@Override
		public boolean isReadOnceOnly() {
			return false;
		}


		@Override
		public boolean canAccept() {
			return true;
		}


		@Override
		protected AbstractFastqVisitorCallback createCallback(
				ParserState parserState) {
			return new MementoCallback(parserState);
		}


		@Override
		public void parse(FastqVisitor visitor) throws IOException {
			if(visitor ==null){
				throw new NullPointerException("visitor can not be null");
			}
			
			try(InputStream in = supplier.get()){
				TextLineParser parser = new TextLineParser(in);
				parseFastqFile(visitor, parser);			
			}
		}


		@Override
		public void parse(FastqVisitor visitor, FastqVisitorMemento memento)
				throws IOException {
			if(!(memento instanceof OffsetMemento)){
				throw new IllegalArgumentException("unknown memento type, instance must be generated by this parser");
			}
			if(visitor ==null){
				throw new NullPointerException("visitor can not be null");
			}
			long startOffset = ((OffsetMemento)memento).getValue();
			
                        try (InputStream in = supplier.get(startOffset)) {
                           
                            TextLineParser parser = new TextLineParser(in, startOffset);
                            parseFastqFile(visitor, parser);
                        }
			
		}		
	}
	
	private static class InputStreamFastqFileParser extends FastqFileParser{
		private final OpenAwareInputStream in;
		
		public InputStreamFastqFileParser(InputStream in,boolean hasComments, boolean multiLine) {
		        super(hasComments, multiLine);
			if(in==null){
				throw new NullPointerException("inputstream can not be null");
			}
			this.in = new OpenAwareInputStream(in);
		}

		
		@Override
		public boolean canCreateMemento() {
			return false;
		}


		@Override
		public boolean isReadOnceOnly() {
			return true;
		}


		@Override
		public synchronized void parse(FastqVisitor visitor) throws IOException {
			if(!canAccept()){
				throw new IllegalStateException("can not accept, inputStream closed");
			}
			//synchronized to only let in one visitor at a time since they will
			//all share the inputstream...
			if(visitor ==null){
				throw new NullPointerException("visitor can not be null");
			}
			try{
				TextLineParser parser = new TextLineParser(in);
				parseFastqFile(visitor, parser);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
			
		}

		@Override
		public boolean canAccept() {
			return in.isOpen();
		}

		@Override
		public void parse(FastqVisitor visitor, FastqVisitorMemento memento)
				throws IOException {
			throw new UnsupportedOperationException("mementos not supported");
			
		}

		@Override
		protected AbstractFastqVisitorCallback createCallback(
				ParserState parserState) {
			return new NoMementoCallback(parserState);
		}
		
	}
	
	public abstract static class OffsetMemento implements FastqVisitorMemento {
		private static final long UNSIGNED_MAX_BYTE = 0xFF;
		private static final long UNSIGNED_MAX_SHORT = 0xFFFF;
		//need the "L" at the end to make the value a long otherwise it's an int with value -1 !
		private static final long UNSIGNED_MAX_INT = 0xFFFFFFFFL;
		/**
		 * Create a new instance of a {@link OffsetMemento}
		 * which will wrap the given value but use
		 * as few bytes as possible.
		 * @param value the value to wrap; may
		 * be negative.
		 * @return a n{@link OffsetMemento} instance that
		 * wraps the given value in as few bytes as possible.
		 */
		public static OffsetMemento valueOf(long value){
			//TODO: should we do caching to return 
			//already created instances (flyweight)?
			//This is probably going to be used mostly
			//for file offsets. If we wrap
			//several fastq files, each of which have
			//the same number of bases we might get a lot of
			//duplicate instances...
			
			if(value <0){
				throw new IllegalArgumentException("can not have negative offset");
			}
			if(value <=UNSIGNED_MAX_BYTE){
				return new ByteWidthOffsetMemento(value);
			}else if(value <=UNSIGNED_MAX_SHORT){
				return new ShortWidthOffsetMemento(value);
			}
			else if(value <=UNSIGNED_MAX_INT){
				return new IntWidthOffsetMemento(value);
			}
			return new LongWidthOffsetMemento(value);
		}
		/**
		 * Get the wrapped value as a long.
		 * @return the value; may be negative.
		 */
		public abstract long getValue();
		
		@Override
		public String toString() {
			return Long.toString(getValue());
		}
		@Override
		public boolean equals(Object obj){
			if(obj ==null){
				return false;
			}
			if(obj instanceof OffsetMemento){
				return getValue()==((OffsetMemento)obj).getValue();
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			long value = getValue();
			return prime * (int) (value ^ (value >>> 32));
		}
		
		
	}
	
	private static class ByteWidthOffsetMemento extends OffsetMemento{
		
		private final byte value;

		public ByteWidthOffsetMemento(long value) {
			this.value = IOUtil.toSignedByte((int)value);
		}

		@Override
		public long getValue() {
			return IOUtil.toUnsignedByte(value);
		}
		
	}
	
	private static class ShortWidthOffsetMemento extends OffsetMemento{
		
		private final short value;

		public ShortWidthOffsetMemento(long value) {
			this.value = IOUtil.toSignedShort((int)value);
		}

		@Override
		public long getValue() {
			return IOUtil.toUnsignedShort(value);
		}
		
	}
	
	private static class IntWidthOffsetMemento extends OffsetMemento{
		
		private final int value;

		public IntWidthOffsetMemento(long value) {
			this.value = IOUtil.toSignedInt(value);
		}

		@Override
		public long getValue() {
			return IOUtil.toUnsignedInt(value);
		}
		
	}
	private static class LongWidthOffsetMemento extends OffsetMemento{
		
		private final long value;

		public LongWidthOffsetMemento(long value) {
			this.value = value;
		}

		@Override
		public long getValue() {
			return value;
		}	
	}
}
