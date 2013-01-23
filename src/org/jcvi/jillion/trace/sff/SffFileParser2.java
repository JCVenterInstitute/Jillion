package org.jcvi.jillion.trace.sff;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.trace.sff.SffFileParserCallback.SffFileMemento;

public class SffFileParser2 {

	private final File sffFile;
	
	
	public SffFileParser2(File sffFile){
		this.sffFile = sffFile;
	}
	
	public void accept(SffFileVisitor2 visitor) throws IOException{
		InputStream in = null;
		try{
			in = new BufferedInputStream(new FileInputStream(sffFile));
			accept(in, visitor);
		}finally{
			IOUtil.closeAndIgnoreErrors(in);
		}
	}
	
	public void accept(SffFileVisitor2 visitor, SffFileMemento momento) throws IOException{
		
		if(!(momento instanceof AbstractSffFileMomento)){
			throw new IllegalArgumentException("don't know how to handle this momento");
		}
		InputStream in = null;
		try{
			in = new BufferedInputStream(new FileInputStream(sffFile));
			
			if(momento instanceof ReadRecordSffFileMomento){
				ReadRecordSffFileMomento readRecordSffFileMomento = (ReadRecordSffFileMomento)momento;
				SffCommonHeader header = readRecordSffFileMomento.header;
				ParserState parserState = new ParserState(readRecordSffFileMomento.getPosition());
				IOUtil.blockingSkip(in, parserState.position);
				DataInputStream dataIn = new DataInputStream(in);
				for(int i=readRecordSffFileMomento.readCount; parserState.keepParsing && i<header.getNumberOfReads(); i++){
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
    private  void accept(InputStream in, SffFileVisitor2 visitor) throws IOException{
        DataInputStream dataIn = new DataInputStream(in);

        SffCommonHeader commonHeader =DefaultSFFCommonHeaderDecoder.INSTANCE.decodeHeader(dataIn);
        ParserState parserState = new ParserState();
        visitor.visitHeader(createCommonHeaderCallback(parserState), commonHeader);
        if(!parserState.keepParsing){
        	return;
        }
        parseReads(visitor, dataIn, commonHeader);
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
	private  void parseReads(SffFileVisitor2 visitor,
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

	private ParserState handleSingleRead(SffFileVisitor2 visitor,
			DataInputStream dataIn, SffCommonHeader commonHeader,
			final int numberOfFlowsPerRead,  ParserState parserState, int readCount) throws IOException {
		SffFileParserCallback readHeaderCallback = createReadHeaderCallback(this, parserState, commonHeader,readCount);
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
	
	
	private SffFileParserCallback createReadHeaderCallback(final SffFileParser2 parser,final ParserState parserState, final SffCommonHeader header, final int readCount){
    	return new SffFileParserCallback(){

			@Override
			public boolean mementoSupported() {
				return true;
			}

			@Override
			public SffFileMemento createMemento() {
				return new ReadRecordSffFileMomento(parserState.position, header, readCount);
			}

			@Override
			public void stopParsing() {
				parserState.stop();				
			}
    		
    	};
    }
	
	private SffFileParserCallback createReadDataCallback(final SffFileParser2 parser, final ParserState parserState){
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
	
	
	private static abstract class AbstractSffFileMomento implements SffFileMemento{
		private final long position;

		public AbstractSffFileMomento(long position) {
			this.position = position;
		}

		public long getPosition() {
			return position;
		}
		
		
	}
	
	private static class ReadRecordSffFileMomento extends AbstractSffFileMomento{
		private final SffCommonHeader header;
		private final int readCount;
		public ReadRecordSffFileMomento(long position,SffCommonHeader header, int readCount) {
			super(position);
			this.header = header;
			this.readCount = readCount;
		}
		
	}
	
	private static class BeginningSffFileMomento extends AbstractSffFileMomento{

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
