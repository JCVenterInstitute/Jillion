/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Nov 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffFileVisitor.CommonHeaderReturnCode;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffFileVisitor.ReadDataReturnCode;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffFileVisitor.ReadHeaderReturnCode;
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
     * on the given visitor.
     * @param sffFile the sff file to visit.
     * @param visitor the visitor to visit.
     * @throws SffDecoderException if there is a problem parsing the sff data.
     * @throws NullPointerException if the sffFile or visitor are null.
     */
    public static void parse(File sffFile, SffFileVisitor visitor) throws SffDecoderException, FileNotFoundException{
        InputStream in = new FileInputStream(sffFile);
        try{
            parse(in,visitor);
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
    public static void parse(InputStream in, SffFileVisitor visitor) throws SffDecoderException{
        DataInputStream dataIn = new DataInputStream(in);
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
}
