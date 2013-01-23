package org.jcvi.jillion.trace.sff;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.trace.sff.SffFileParserCallback.SffFileMemento;

public class SffFileParser {

	private final File sffFile;
	private SffCommonHeader header;
	
	public SffFileParser(File sffFile){
		this.sffFile = sffFile;
	}
	
	public void accept(SffFileVisitor visitor) throws IOException{
		InputStream in = null;
		try{
			in = new BufferedInputStream(new FileInputStream(sffFile));
			accept(in, visitor);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	
	public void accept(SffFileVisitor visitor, SffFileMemento memento) throws IOException{
		
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
			in = new BufferedInputStream(new FileInputStream(sffFile));
			
			if(memento instanceof ReadRecordSffFileMemento){
				ReadRecordSffFileMemento readRecordSffFileMemento = (ReadRecordSffFileMemento)memento;
				ParserState parserState = new ParserState(readRecordSffFileMemento.getPosition());
				IOUtil.blockingSkip(in, parserState.position);
				DataInputStream dataIn = new DataInputStream(in);
				for(int i=readRecordSffFileMemento.readCount; parserState.keepParsing && i<header.getNumberOfReads(); i++){
					parserState = handleSingleRead(visitor, dataIn, header,
							header.getNumberOfFlowsPerRead(), parserState,i);
				    
				}
			}else{
				accept(in, visitor);
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
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
    private  void accept(InputStream in, SffFileVisitor visitor) throws IOException{
        DataInputStream dataIn = new DataInputStream(in);

        header =DefaultSFFCommonHeaderDecoder.INSTANCE.decodeHeader(dataIn);
        ParserState parserState = new ParserState();
        visitor.visitHeader(createCommonHeaderCallback(parserState), header);
        if(!parserState.keepParsing){
        	return;
        }
        parseReads(visitor, dataIn, header);
        visitor.endSffFile();
        
    }
    
    private SffFileParserCallback createCommonHeaderCallback(final ParserState parserState){
    	return new SffFileParserCallback(){

			@Override
			public boolean mementoSupported() {
				return true;
			}

			@Override
			public SffFileMemento createMemento() {
				return new BeginningSffFileMomento();
			}

			@Override
			public void stopParsing() {
				parserState.stop();				
			}
    		
    	};
    }
	private  void parseReads(SffFileVisitor visitor,
			DataInputStream dataIn, SffCommonHeader commonHeader)
			throws IOException {
		final long numberOfReads = commonHeader.getNumberOfReads();
		final int numberOfFlowsPerRead = commonHeader.getNumberOfFlowsPerRead();
		int keyLength = (int)commonHeader.getKeySequence().getLength();
        int size = 31+commonHeader.getNumberOfFlowsPerRead()+ keyLength;
        int padding =SffUtil.caclulatePaddedBytes(size);
        int commonHeaderLength = size+padding;
        
        ParserState parserState  = new ParserState(commonHeaderLength);
		for(int i=0;parserState.keepParsing && i<numberOfReads; i++){
			parserState = handleSingleRead(visitor, dataIn, commonHeader,
					numberOfFlowsPerRead, parserState,i);
		    
		}
	}

	private ParserState handleSingleRead(SffFileVisitor visitor,
			DataInputStream dataIn, SffCommonHeader commonHeader,
			final int numberOfFlowsPerRead,  ParserState parserState, int readCount) throws IOException {
		SffFileParserCallback readHeaderCallback = createReadHeaderCallback(this, parserState, readCount);
		SffReadHeader readHeader = DefaultSffReadHeaderDecoder.INSTANCE.decodeReadHeader(dataIn);
   
		
		
		SffFileReadVisitor readVisitor = visitor.visitRead(readHeaderCallback, readHeader);
		int unpaddedHeaderLength = 16+readHeader.getId().length();
		int readHeaderPadding = SffUtil.caclulatePaddedBytes(unpaddedHeaderLength);
		int paddedHeaderLength = unpaddedHeaderLength+readHeaderPadding;
		ParserState updatedParserState= parserState.incrementPosition(paddedHeaderLength);
		
		int readDataLength = SffUtil.getReadDataLength(numberOfFlowsPerRead, readHeader.getNumberOfBases());
		int readDataPadding =SffUtil.caclulatePaddedBytes(readDataLength);
		
		if(readVisitor!=null){
		    final int numberOfBases = readHeader.getNumberOfBases();
		    SffReadData readData = DefaultSffReadDataDecoder.INSTANCE.decode(dataIn,
		                    numberOfFlowsPerRead,
		                    numberOfBases);
		    
		    SffFileParserCallback readDataCallback = createReadDataCallback(this,parserState);
			readVisitor.visitReadData(readDataCallback, readData);
		    readVisitor.visitEndOfRead(readDataCallback);
		    
		}else{
			//skip read data	    	
			IOUtil.blockingSkip(dataIn, readDataLength+readDataPadding);
		}
		updatedParserState= parserState.incrementPosition(readDataLength+readDataPadding);
		return updatedParserState;
	}
	
	
	private SffFileParserCallback createReadHeaderCallback(final SffFileParser parser,final ParserState parserState, final int readCount){
    	return new SffFileParserCallback(){

			@Override
			public boolean mementoSupported() {
				return true;
			}

			@Override
			public SffFileMemento createMemento() {
				return new ReadRecordSffFileMemento(parserState.position, readCount);
			}

			@Override
			public void stopParsing() {
				parserState.stop();				
			}
    		
    	};
    }
	
	private SffFileParserCallback createReadDataCallback(final SffFileParser parser, final ParserState parserState){
    	return new SffFileParserCallback(){

			@Override
			public boolean mementoSupported() {
				return false;
			}

			@Override
			public SffFileMemento createMemento() {
				throw new UnsupportedOperationException("can not create Momento inside of read record"); 
			}

			@Override
			public void stopParsing() {
				parserState.stop();				
			}
    		
    	};
    }
	
	
	private static abstract class AbstractSffFileMemento implements SffFileMemento{
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
		private long position;
		private boolean keepParsing=true;
		
		public ParserState(){
			this(0L);
		}
		public ParserState(long initialPosition){
			this.position = initialPosition;
		}
		private ParserState(long position, boolean keepParsing) {
			this.position = position;
			this.keepParsing = keepParsing;
		}

		public ParserState incrementPosition(long increment){
			return new ParserState(this.position +=increment, keepParsing);
		}
		
		public void stop(){
			keepParsing=false;
		}
		
	}
}
