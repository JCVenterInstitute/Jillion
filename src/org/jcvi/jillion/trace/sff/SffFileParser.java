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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Nov 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.trace.sff.SffFileVisitor.CommonHeaderReturnCode;
import org.jcvi.jillion.trace.sff.SffFileVisitor.ReadDataReturnCode;
import org.jcvi.jillion.trace.sff.SffFileVisitor.ReadHeaderReturnCode;
/**
 * {@code SffFileParser} contains
 * methods for parsing sff encoded
 * files.
 * @author dkatzel
 * @see <a href ="http://www.ncbi.nlm.nih.gov/Traces/trace.cgi?cmd=show&f=formats&m=doc&s=format#sff">SFF file format spec from NCBI</a>
 */
public final class SffFileParser {
    
    private SffFileParser(){}
    /**
     * Parse the given sffFile and call the appropriate visitXXX methods
     * on the given visitor.  If the sff file
     * is less than 2GB than this method
     * will use memory mapping
     * to parse the file more efficiently
     * (at the cost of swap space).
     * If the sff file is larger than 2GB 
     * than this method will stream through
     * the file using a {@link FileInputStream}.
     * @param sffFile the sff file to visit.
     * @param visitor the visitor to visit.
     * @throws IOException if the sff file does not exist,
     * or if there is a problem parsing the sff data.
     * @throws NullPointerException if either parameter is null.
     * @see MappedByteBuffer
     */
    public static void parse(File sffFile, SffFileVisitor visitor) throws IOException{
    	if(sffFile ==null){
    		throw new NullPointerException("sff file can not be null");
    	}
    	if(visitor==null){
    		throw new NullPointerException("visitor can not be null");
    	}
    	long fileLength =sffFile.length();
    	//memory mapping
    	//is only allowed for files <2GB
    	//according to the javadoc
    	if(fileLength <= Integer.MAX_VALUE){
    		parseUsingNio(sffFile, visitor);
    	}else{
	        InputStream in = new FileInputStream(sffFile);
	        try{
	            parse(in,visitor);
	        }finally{
	            IOUtil.closeAndIgnoreErrors(in);
	        }
    	}
    }
    
    /**
     * Parse the given sffFile using NIO
     * memory mapped ByteBuffers and call the appropriate visitXXX methods
     * on the given visitor.
     * @param sffFile the sff file to visit.
     * @param visitor the visitor to visit.
     * @throws IOException 
     * @throws NullPointerException if the sffFile or visitor are null.
     */
    private static void parseUsingNio(File sffFile, SffFileVisitor visitor) throws IOException{
    	FileChannel channel =new RandomAccessFile(sffFile, "r").getChannel();
		MappedByteBuffer memMappedBuffer = channel.map(MapMode.READ_ONLY, 0, (int)channel.size());
        try{
            parse(memMappedBuffer,visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(channel);
        }
    }
    /**
     * Parse the given {@link InputStream} containing sff encoded
     * data and call the appropriate visitXXX methods on the given visitor.
     * This method may be less I/O efficient
     * than calling {@link #parse(File, SffFileVisitor)}
     * but is able to handle more use cases.  For example,
     * this method should be used to parse compressed
     * data by passing in the appropriate deflater inputStream.
     * <p/>
     * NOTE: The inputStream will <strong>not</strong>
     * be closed when this method completes. Clients 
     * must close the {@link InputStream} themselves when they are done
     * in case their use cases require the stream remains open
     * for reading other downstream data.
     * @param in {@link InputStream} containing sff encoded
     * data
     * @param visitor the visitor to visit.
     * @throws IOException if there is a problem parsing the sff data.
     * @throws NullPointerException if the inputstream or visitor are null.
     */
    public static void parse(InputStream in, SffFileVisitor visitor) throws IOException{
        DataInputStream dataIn = new DataInputStream(new BufferedInputStream(in));
        visitor.visitFile();
        SffCommonHeader commonHeader =DefaultSFFCommonHeaderDecoder.INSTANCE.decodeHeader(dataIn);
        CommonHeaderReturnCode commonHeaderRet = visitor.visitCommonHeader(commonHeader);
        if(commonHeaderRet==null){
        	throw new NullPointerException("can not return null for visitCommonHeader() callback");
        }
		if(commonHeaderRet==CommonHeaderReturnCode.PARSE_READS){        
            parseReads(visitor, dataIn, commonHeader);
        }
        visitor.visitEndOfFile();
        
    }
	private static void parseReads(SffFileVisitor visitor,
			DataInputStream dataIn, SffCommonHeader commonHeader)
			throws SffDecoderException {
		final long numberOfReads = commonHeader.getNumberOfReads();
		final int numberOfFlowsPerRead = commonHeader.getNumberOfFlowsPerRead();
		for(long i=0; i<numberOfReads; i++){
		    SffReadHeader readHeader = DefaultSffReadHeaderDecoder.INSTANCE.decodeReadHeader(dataIn);
		    ReadHeaderReturnCode readHeaderRet = visitor.visitReadHeader(readHeader);
		    if(readHeaderRet==null){
		    	throw new NullPointerException("can not return null for visitReadHeader() callback");
		    }
			if(readHeaderRet==ReadHeaderReturnCode.PARSE_READ_DATA){            
		        final int numberOfBases = readHeader.getNumberOfBases();
		        SffReadData readData = DefaultSffReadDataDecoder.INSTANCE.decode(dataIn,
		                        numberOfFlowsPerRead,
		                        numberOfBases);
		        ReadDataReturnCode readDataRet = visitor.visitReadData(readData);
		        if(readDataRet==null){
		        	throw new NullPointerException("can not return null for visitReadData() callback");
		        }
				if(readDataRet==ReadDataReturnCode.STOP_PARSING){
		            break;
		        }
		    }else if(readHeaderRet==ReadHeaderReturnCode.SKIP_CURRENT_READ){
		        //skip length of readData
		        int readDataLength = SffUtil.getReadDataLength(numberOfFlowsPerRead, readHeader.getNumberOfBases());
		        int padding =SffUtil.caclulatePaddedBytes(readDataLength);
		        try {
		            IOUtil.blockingSkip(dataIn, readDataLength+padding);
		        } catch (IOException e) {
		            throw new SffDecoderException("could not skip read data block",e);
		        }
		    }else{
		    	//stop
		    	break;
		    }
		    
		}
	}
	 /**
     * Parse the given {@link ByteBuffer} containing sff encoded
     * data and call the appropriate visitXXX methods on the given visitor.
     * <p/>
     * NOTE: The ByteBuffer's position will be advanced
     * as far as the buffer is read (which might reach the buffer limit).
     * 
     * @param buf {@link ByteBuffer} containing sff encoded
     * data where the current buffer position
     * is the start of the sff data.
     * @param visitor the visitor to visit.
     * @throws IOException if there is a problem parsing the sff data.
     * @throws NullPointerException if the buffer or visitor are null.
     * @throws BufferUnderflowException if the buffer limit is not large
     * enough to contain the entire sff file. 
     */
	 public static void parse(ByteBuffer buf, SffFileVisitor visitor) throws IOException{
        if(buf==null){
        	throw new NullPointerException("buffer can not be null");
        }
        if(visitor==null){
        	throw new NullPointerException("visitor can not be null");
        }
		visitor.visitFile();
        SffCommonHeader commonHeader =DefaultSFFCommonHeaderDecoder.INSTANCE.decodeHeader(buf);
        CommonHeaderReturnCode commonHeaderRet = visitor.visitCommonHeader(commonHeader);
        if(commonHeaderRet==null){
        	throw new NullPointerException("can not return null for visitCommonHeader() callback");
        }
		if(commonHeaderRet==CommonHeaderReturnCode.PARSE_READS){        
            parseReads(visitor, buf, commonHeader);
        }
        visitor.visitEndOfFile();
        
    }
	 
	 private static void parseReads(SffFileVisitor visitor,
				ByteBuffer dataIn, SffCommonHeader commonHeader)
				throws SffDecoderException {
			final long numberOfReads = commonHeader.getNumberOfReads();
			final int numberOfFlowsPerRead = commonHeader.getNumberOfFlowsPerRead();
			for(long i=0; i<numberOfReads; i++){
			    SffReadHeader readHeader = DefaultSffReadHeaderDecoder.INSTANCE.decodeReadHeader(dataIn);
			    ReadHeaderReturnCode readHeaderRet = visitor.visitReadHeader(readHeader);
			    if(readHeaderRet==null){
			    	throw new NullPointerException("can not return null for visitReadHeader() callback");
			    }
				if(readHeaderRet==ReadHeaderReturnCode.PARSE_READ_DATA){            
			        final int numberOfBases = readHeader.getNumberOfBases();
			        SffReadData readData = DefaultSffReadDataDecoder.INSTANCE.decode(dataIn,
			                        numberOfFlowsPerRead,
			                        numberOfBases);
			        ReadDataReturnCode readDataRet = visitor.visitReadData(readData);
			        if(readDataRet==null){
			        	throw new NullPointerException("can not return null for visitReadData() callback");
			        }
					if(readDataRet==ReadDataReturnCode.STOP_PARSING){
			            break;
			        }
			    }else if(readHeaderRet==ReadHeaderReturnCode.SKIP_CURRENT_READ){
			        //skip length of readData
			        int readDataLength = SffUtil.getReadDataLength(numberOfFlowsPerRead, readHeader.getNumberOfBases());
			        int padding =SffUtil.caclulatePaddedBytes(readDataLength);
			        
			        try {
			        	dataIn.position(dataIn.position()+ readDataLength+padding);
			        } catch (BufferUnderflowException e) {
			            throw new SffDecoderException("could not skip read data block",e);
			        }
			    }else{
			    	//stop
			    	break;
			    }
			    
			}
		}
}
