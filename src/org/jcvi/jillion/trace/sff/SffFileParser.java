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
package org.jcvi.jillion.trace.sff;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.RandomAccessFileInputStream;
import org.jcvi.jillion.trace.sff.SffVisitorCallback.SffVisitorMemento;
/**
 * {@code SffFileParser} can parse an SFF
 * binary encoded
 * flowgram file.
 * @author dkatzel
 * @see <a href ="http://www.ncbi.nlm.nih.gov/Traces/trace.cgi?cmd=show&f=formats&m=doc&s=format#sff">SFF file format spec from NCBI</a>
 */
public abstract class SffFileParser implements SffParser{
	
	protected SffCommonHeader header;
	/**
	 * Create a new instance of {@link SffParser}
	 * that will parse the given sff encoded file.
	 * The file isn't actually parsed until
	 * one of the accept methods is called.
	 * @param sffFile the sff file to parse
	 * @throws NullPointerException if sffFile is null.
	 * @throws FileNotFoundException if sffFile does not exist.
	 */
	public static SffParser create(File sffFile) throws FileNotFoundException{
		return new FileBasesSffParser(sffFile);
	}

	/**
	 * Create a new instance of {@link SffParser}
	 * that will parse the given sff encoded {@link InputStream}.
	 * Please Note that inputStream implementations
	 * of the FastaFileParser can not create {@link SffVisitorMemento}s
	 * or use {@link #accept(SffVisitor, SffVisitorMemento)}.
	 * The {@link InputStream} isn't actually parsed until
	 * one of the {@link SffFileParser#accept(SffVisitor)}
	 * is called.
	 * @param inputStream an {@link InputStream} that contains
	 * sff encoded data to be parsed; can not be null.
	 * @throws NullPointerException if sffFile is null.
	 * @throws FileNotFoundException if sffFile does not exist.
	 */
	public static SffParser create(InputStream inputStream) throws FileNotFoundException{
		return new InputStreamBasedSffParser(inputStream);
	}
	
	private SffFileParser(){
		//can not instantiate outside of this class file
	}
		
	
    /**
     * Parse the given {@link InputStream} containing sff encoded
     * data and call the appropriate visitXXX methods on the given visitor.
     * @param in {@link InputStream} containing sff encoded
     * data
     * @param visitor the visitor to visit.
     * @throws SffDecoderException if there is a problem parsing the sff data.
     * @throws NullPointerException if the inputstream or visitor are null.
     */
    protected void accept(InputStream in, SffVisitor visitor) throws IOException{
        DataInputStream dataIn = new DataInputStream(in);

        header =DefaultSFFCommonHeaderDecoder.INSTANCE.decodeHeader(dataIn);
        ParserState parserState = new ParserState();
        visitor.visitHeader(createCommonHeaderCallback(parserState), header);
        if(!parserState.keepParsing()){
        	return;
        }
        parseReads(visitor, dataIn, header);
        visitor.end();
        
    }
    
    protected abstract SffVisitorCallback createCommonHeaderCallback(final ParserState parserState);
    
	private  void parseReads(SffVisitor visitor,
			DataInputStream dataIn, SffCommonHeader commonHeader)
			throws IOException {
		final long numberOfReads = commonHeader.getNumberOfReads();
		
		int keyLength = (int)commonHeader.getKeySequence().getLength();
        int size = 31+commonHeader.getNumberOfFlowsPerRead()+ keyLength;
        int padding =SffUtil.caclulatePaddedBytes(size);
        int commonHeaderLength = size+padding;
        
        ParserState parserState  = new ParserState(commonHeaderLength);
		for(int i=0;parserState.keepParsing() && i<numberOfReads; i++){
			parserState = handleSingleRead(visitor, dataIn, parserState,
					i);
		    
		}
	}

	protected ParserState handleSingleRead(SffVisitor visitor,
			DataInputStream dataIn, ParserState parserState,
			int readCount) throws IOException {
		SffVisitorCallback readHeaderCallback = createReadHeaderCallback(parserState, readCount);
		SffReadHeader readHeader = DefaultSffReadHeaderDecoder.INSTANCE.decodeReadHeader(dataIn);
   
		
		
		SffFileReadVisitor readVisitor = visitor.visitRead(readHeaderCallback, readHeader);
		int unpaddedHeaderLength = 16+readHeader.getId().length();
		int readHeaderPadding = SffUtil.caclulatePaddedBytes(unpaddedHeaderLength);
		int paddedHeaderLength = unpaddedHeaderLength+readHeaderPadding;
		ParserState updatedParserState= parserState.incrementPosition(paddedHeaderLength);
		int numberOfFlowsPerRead = header.getNumberOfFlowsPerRead();
		int readDataLength = SffUtil.getReadDataLength(numberOfFlowsPerRead, readHeader.getNumberOfBases());
		int readDataPadding =SffUtil.caclulatePaddedBytes(readDataLength);
		
		if(readVisitor==null){
			//skip read data	    	
			IOUtil.blockingSkip(dataIn, readDataLength+readDataPadding);		    
		}else{
			final int numberOfBases = readHeader.getNumberOfBases();
			SffReadData readData = DefaultSffReadDataDecoder.INSTANCE.decode(dataIn, numberOfFlowsPerRead, numberOfBases);
			readVisitor.visitReadData(readData);
			readVisitor.visitEnd();
		}
		updatedParserState= updatedParserState.incrementPosition(readDataLength+readDataPadding);
		return updatedParserState;
	}
	
	
	protected abstract SffVisitorCallback createReadHeaderCallback(final ParserState parserState,final int readCount);
	
	
	
	
	private abstract static class AbstractSffFileMemento implements SffVisitorMemento{
		private final long position;

		public AbstractSffFileMemento(long position) {
			this.position = position;
		}

		public long getPosition() {
			return position;
		}
		
		
	}
	
	private static class ReadRecordSffFileMemento extends AbstractSffFileMemento{
		private final int readCount;
		public ReadRecordSffFileMemento(long position, int readCount) {
			super(position);
			this.readCount = readCount;
		}
		
	}
	
	private static class BeginningSffFileMomento extends AbstractSffFileMemento{

		public BeginningSffFileMomento() {
			super(0L);
		}
		
	}
	
	private static class ParserState{
		private final long position;
		//keepParsing is an object instead of a primitive
		//so we can pass around the same reference to each
		//ParserState object at increment call
		private AtomicBoolean keepParsing= new AtomicBoolean(true);
		
		public ParserState(){
			this(0L);
		}
		public ParserState(long initialPosition){
			this.position = initialPosition;
		}
		private ParserState(long position, AtomicBoolean keepParsing) {
			this.position = position;
			this.keepParsing = keepParsing;
		}

		public ParserState incrementPosition(long increment){
			return new ParserState(this.position +increment, keepParsing);
		}
		
		public void stop(){
			keepParsing.set(false);
		}
		
		public boolean keepParsing(){
			return keepParsing.get();
		}
	}
	

	/**
	 * InputStream implementation of sff parser
	 * that can not create mementos
	 * since we won't always be able to "rewind" the stream.
	 * @author dkatzel
	 *
	 */
	private static class InputStreamBasedSffParser extends SffFileParser{
		private final OpenAwareInputStream in;
		
		public InputStreamBasedSffParser(InputStream in) {
			this.in = new OpenAwareInputStream(in);
		}

		@Override
		public void parse(SffVisitor visitor) throws IOException {
			if(!canParse()){
				throw new IllegalStateException("inputstream is not open");
			}
			this.accept(in, visitor);			
		}

		@Override
		public boolean canParse() {
			return in.isOpen();
		}

		@Override
		public void parse(SffVisitor visitor, SffVisitorMemento memento)
				throws IOException {
			throw new UnsupportedOperationException("can not accept mementos when inputStream is provided");
			
		}

		@Override
		protected SffVisitorCallback createReadHeaderCallback(
				ParserState parserState, int readCount) {
			return createMementolessCallback(parserState);
		}
		
		@Override
		protected SffVisitorCallback createCommonHeaderCallback(
				ParserState parserState) {
			return createMementolessCallback(parserState);
		}

		private SffVisitorCallback createMementolessCallback(final ParserState parserState){
			return new SffVisitorCallback(){

				@Override
				public boolean mementoSupported() {
					return false;
				}

				@Override
				public SffVisitorMemento createMemento() {
					throw new UnsupportedOperationException("can not create mementos from inputstream");
				}

				@Override
				public void haltParsing() {
					parserState.stop();				
				}
	    		
	    	};
		}
	}
	
	private static final class FileBasesSffParser extends SffFileParser{
		private final File sffFile;

		/**
		 * Create a new instance of {@link SffFileParser}
		 * that will parse the given sff encoded file.
		 * The file isn't actually parsed until
		 * one of the accept methods is called.
		 * @param sffFile the sff file to parse
		 * @throws NullPointerException if sffFile is null.
		 * @throws FileNotFoundException if sffFile does not exist.
		 */
		private FileBasesSffParser(File sffFile) throws FileNotFoundException{
			if(sffFile ==null){
				throw new NullPointerException("sff file can not be null");
			}
			if(!sffFile.exists()){
				throw new FileNotFoundException(String.format("sff file %s does not exist", sffFile.getAbsolutePath()));
			}
			this.sffFile = sffFile;
		}
		
		@Override
		public boolean canParse() {
			return true;
		}

		/**
		 * Visit the sff file (starting from the beginning)
		 * and call the visit methods on the given visitor.
		 * @param visitor the visitor to call the visit methods on.
		 * @throws IOException if there is a problem parsing the file.
		 * @throws NullPointerException if visitor is null.
		 */
		@Override
		public void parse(SffVisitor visitor) throws IOException{
			if(visitor==null){
				throw new NullPointerException("visitor can not be null");
			}
			InputStream in = null;
			try{
				in = new BufferedInputStream(new FileInputStream(sffFile));
				accept(in, visitor);
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
		}
		/**
		 * Visit the sff file starting from the portion
		 * of the file specified by the given {@link SffFileMemento}.
		 * and call the visit methods on the given visitor.
		 * 
		 * @param visitor the visitor to call the visit methods on.
		 * @param memento the {@link SffFileMemento} instance which will
		 * tell the parser where to start parsing.  Only {@link SffFileMemento}s
		 * generated by this parser instance are valid.
		 * @throws IOException if there is a problem parsing the file.
		 * @throws NullPointerException if visitor is null.
		 */
		@Override
		public void parse(SffVisitor visitor, SffVisitorMemento memento) throws IOException{
			
			if(!(memento instanceof AbstractSffFileMemento)){
				throw new IllegalArgumentException("don't know how to handle this memento");
			}
			//if the header is null,
			//then we haven't yet parsed the file,
			//therefore we don't have a valid memento
			if(header ==null){
				throw new IllegalStateException("parser has not yet been initialized, must call accept(visitor) method first");
			}
			InputStream in = null;
			try{
				
				if(memento instanceof ReadRecordSffFileMemento){
					
					ReadRecordSffFileMemento readRecordSffFileMemento = (ReadRecordSffFileMemento)memento;
					ParserState parserState = new ParserState(readRecordSffFileMemento.getPosition());
					in = new BufferedInputStream(new RandomAccessFileInputStream(sffFile, parserState.position));
					DataInputStream dataIn = new DataInputStream(in);
					for(int i=readRecordSffFileMemento.readCount; parserState.keepParsing() && i<header.getNumberOfReads(); i++){
						parserState = handleSingleRead(visitor, dataIn, parserState,
								i);
					    
					}
				}else{
					in = new BufferedInputStream(new FileInputStream(sffFile));
					accept(in, visitor);
				}
			}finally{
				IOUtil.closeAndIgnoreErrors(in);
			}
		}
		
		@Override
		protected SffVisitorCallback createReadHeaderCallback(final ParserState parserState,final int readCount){
	    	return new SffVisitorCallback(){

				@Override
				public boolean mementoSupported() {
					return true;
				}

				@Override
				public SffVisitorMemento createMemento() {
					return new ReadRecordSffFileMemento(parserState.position, readCount);
				}

				@Override
				public void haltParsing() {
					parserState.stop();				
				}
	    		
	    	};
	    }
		@Override
		protected SffVisitorCallback createCommonHeaderCallback(final ParserState parserState){
	    	return new SffVisitorCallback(){

				@Override
				public boolean mementoSupported() {
					return true;
				}

				@Override
				public SffVisitorMemento createMemento() {
					return new BeginningSffFileMomento();
				}

				@Override
				public void haltParsing() {
					parserState.stop();				
				}
	    		
	    	};
	    }
	}
}
