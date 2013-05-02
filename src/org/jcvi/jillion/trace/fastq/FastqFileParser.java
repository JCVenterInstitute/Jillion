/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.trace.fastq;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
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
public abstract class FastqFileParser {

	private static final Pattern CASAVA_1_8_DEFLINE_PATTERN = Pattern.compile("^@(\\S+\\s+\\d:[N|Y]:\\d+:\\S+)\\s*$");
	/**
	 * Create a new {@link FastqFileParser} instance
	 * that will parse the given fastq encoded
	 * file.
	 * @param fastqFile the file to parse.
	 * @throws FileNotFoundException  if the file does not exist.
	 * @throws NullPointerException if fastqFile is null.
	 */
	public static FastqFileParser create(File fastqFile) throws FileNotFoundException{
		return new FileBasedFastqFileParser(fastqFile);
	}
	/**
	 * Create a new {@link FastqFileParser} instance
	 * that will parse the given fastq encoded
	 * inputStream.  Please Note that inputStream implementations
	 * of the FastqFileParser can not create {@link FastqVisitorMemento}s
	 * or use {@link #accept(FastqVisitor, FastqVisitorMemento)}
	 * method.
	 * @param in the fastq encoded inputstream to parse.
	 * @throws NullPointerException if inputstream is null.
	 */
	public static FastqFileParser create(InputStream in){
		return new InputStreamFastqFileParser(in);
	}
	private FastqFileParser(){
		//can not instantiate outside of this class file.
	}
	
	public abstract void accept(FastqVisitor visitor) throws IOException;
	
	public abstract void accept(FastqVisitor visitor, FastqVisitorMemento memento) throws IOException;
	
	protected void parseFastqFile(FastqVisitor visitor, TextLineParser parser, long initialOffset) throws IOException{
		ParserState parserState = new ParserState(initialOffset);
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
		Defline defline = Defline.parse(deflineText);
		AbstractFastqVisitorCallback callback = createCallback(parserState);
        FastqRecordVisitor recordVisitor= visitor.visitDefline(callback, defline.getId(), defline.getComment());
        if(!parserState.keepParsing()){
        	return parserState;
        }
        return parseRecordBody(parser,recordVisitor,parserState, deflineText.length());		
        
	}
	
	private ParserState parseRecordBody(TextLineParser parser,
			FastqRecordVisitor recordVisitor, ParserState parserState, int lengthOfDefline) throws IOException {
		int numBytesRead = lengthOfDefline;
		boolean inBasecallBlock;
		//default to 200 bp since most sequences are only that much anyway
        //builder will grow if we get too big
        NucleotideSequenceBuilder sequenceBuilder = new NucleotideSequenceBuilder(200);
        String line = parser.nextLine();
        numBytesRead+= line.length();
    	sequenceBuilder.append(line);
        do{
        	line = parser.nextLine();
        	numBytesRead+= line.length();
        	Matcher beginQualityMatcher =FastqUtil.QUAL_DEFLINE_PATTERN.matcher(line);
        	inBasecallBlock = !beginQualityMatcher.find();
        	if(inBasecallBlock){
        		sequenceBuilder.append(line);
        	}
        }while(inBasecallBlock);
        
        NucleotideSequence sequence = sequenceBuilder.build();
        if(recordVisitor!=null){
        	recordVisitor.visitNucleotides(sequence);
        }
        if(!parserState.keepParsing()){
        	if(recordVisitor!=null){
            	recordVisitor.halted();
            }
        	return parserState.incrementOffset(numBytesRead);
        }
        //now parse the qualities
        int expectedQualities =  (int)sequence.getLength();
		 
        StringBuilder qualityBuilder = new StringBuilder(expectedQualities);
        
    	while(qualityBuilder.length() < expectedQualities){
    		line = parser.nextLine();
    		numBytesRead+= line.length();
    		qualityBuilder.append(line.trim());
    	}
    	if(qualityBuilder.length()> expectedQualities){
    		throw new IOException(
    				String.format("too many quality values for current record: expected %d but was %d", expectedQualities, qualityBuilder.length()));
    	}
    	if(recordVisitor!=null){
    		recordVisitor.visitEncodedQualities(qualityBuilder.toString());
    	}
    	ParserState endParserState = parserState.incrementOffset(numBytesRead);
		 if(recordVisitor !=null){
			 if(endParserState.keepParsing()){
				 recordVisitor.visitEnd();
			 }else{
				 recordVisitor.halted();
			 }
		 }
		return endParserState;
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
	            throw new IllegalStateException("invalid fastq file, could not parse seq id from "+ fastqDefline);
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
	
	private static abstract class AbstractFastqVisitorCallback implements FastqVisitorCallback{
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
		
		ParserState incrementOffset(long increment){
			return new ParserState(currentOffset+increment, keepParsing);
		}
	}
	
	private static class FileBasedFastqFileParser extends FastqFileParser{
		private final File fastqFile;
		
		
		public FileBasedFastqFileParser(File fastqFile) throws FileNotFoundException {
			if(!fastqFile.exists()){
				throw new FileNotFoundException(fastqFile.getAbsolutePath() + " does not exist");
			}
			this.fastqFile = fastqFile;
		}


		@Override
		protected AbstractFastqVisitorCallback createCallback(
				ParserState parserState) {
			return new MementoCallback(parserState);
		}


		@Override
		public void accept(FastqVisitor visitor) throws IOException {
			if(visitor ==null){
				throw new NullPointerException("visitor can not be null");
			}
			InputStream in = new BufferedInputStream(new FileInputStream(fastqFile));
			try{
				TextLineParser parser = new TextLineParser(in);
				parseFastqFile(visitor, parser, 0L);			
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
		}


		@Override
		public void accept(FastqVisitor visitor, FastqVisitorMemento memento)
				throws IOException {
			if(!(memento instanceof OffsetMemento)){
				throw new IllegalArgumentException("unknown memento type, instance must be generated by this parser");
			}
			if(visitor ==null){
				throw new NullPointerException("visitor can not be null");
			}
			long startOffset = ((OffsetMemento)memento).getValue();
			InputStream in = null;
			try{
				in = new RandomAccessFileInputStream(fastqFile, startOffset);
				TextLineParser parser = new TextLineParser(in);
				parseFastqFile(visitor, parser, startOffset);	
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
		}		
	}
	
	private static class InputStreamFastqFileParser extends FastqFileParser{
		private final InputStream in;
		
		public InputStreamFastqFileParser(InputStream in) {
			if(in==null){
				throw new NullPointerException("inputstream can not be null");
			}
			this.in = in;
		}

		@Override
		public synchronized void accept(FastqVisitor visitor) throws IOException {
			//synchronized to only let in one visitor at a time since they will
			//all share the inputstream...
			if(visitor ==null){
				throw new NullPointerException("visitor can not be null");
			}
			try{
				TextLineParser parser = new TextLineParser(in);
				parseFastqFile(visitor, parser, 0L);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
			
		}

		@Override
		public void accept(FastqVisitor visitor, FastqVisitorMemento memento)
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
