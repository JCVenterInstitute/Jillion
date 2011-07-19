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

public class SffParser {
    private static final SFFCommonHeaderCodec COMMON_HEADER_CODEC = new DefaultSFFCommonHeaderCodec();
    private static final SFFReadHeaderCodec READ_HEADER_CODEC =new DefaultSFFReadHeaderCodec();
    private static final SFFReadDataCodec READ_DATA_CODEC =new DefaultSFFReadDataCodec();
    
    private SffParser(){}
    /**
     * Parse the given sffFile and call the appropriate visitXXX methods
     * on the given visitor.
     * @param sffFile the sff file to visit.
     * @param visitor the visitor to visit.
     * @throws SFFDecoderException if there is a problem parsing the sff data.
     * @throws NullPointerException if the sffFile or visitor are null.
     */
    public static void parseSFF(File sffFile, SffFileVisitor visitor) throws SFFDecoderException, FileNotFoundException{
        InputStream in = new FileInputStream(sffFile);
        try{
            parseSFF(in,visitor);
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
     * @throws SFFDecoderException if there is a problem parsing the sff data.
     * @throws NullPointerException if the inputstream or visitor are null.
     */
    public static void parseSFF(InputStream in, SffFileVisitor visitor) throws SFFDecoderException{
        DataInputStream dataIn = new DataInputStream(in);
        visitor.visitFile();
        SFFCommonHeader commonHeader =COMMON_HEADER_CODEC.decodeHeader(dataIn);
        if(visitor.visitCommonHeader(commonHeader)){
        
            final long numberOfReads = commonHeader.getNumberOfReads();
            final int numberOfFlowsPerRead = commonHeader.getNumberOfFlowsPerRead();
            for(long i=0; i<numberOfReads; i++){
                SFFReadHeader readHeader = READ_HEADER_CODEC.decodeReadHeader(dataIn);
                if(visitor.visitReadHeader(readHeader)){            
                    final int numberOfBases = readHeader.getNumberOfBases();
                    SFFReadData readData = READ_DATA_CODEC.decode(dataIn,
                                    numberOfFlowsPerRead,
                                    numberOfBases);
                    if(!visitor.visitReadData(readData)){
                        break;
                    }
                }else{
                    //skip length of readData
                    int readDataLength = SFFUtil.getReadDataLength(numberOfFlowsPerRead, readHeader.getNumberOfBases());
                    int padding =SFFUtil.caclulatePaddedBytes(readDataLength);
                    try {
                        IOUtil.blockingSkip(dataIn, readDataLength+padding);
                    } catch (IOException e) {
                        throw new SFFDecoderException("could not skip read data block");
                    }
                }
                
            }
        }
        visitor.visitEndOfFile();
        
    }
}
