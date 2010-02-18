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
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.io.IOUtil;

public class SffParser {
    private static final SFFCommonHeaderCodec commonHeaderCodec = new DefaultSFFCommonHeaderCodec();
    private static final SFFReadHeaderCodec readHeaderCodec =new DefaultSFFReadHeaderCodec();
    private static final SFFReadDataCodec readDataCodec =new DefaultSFFReadDataCodec();
    
    private SffParser(){}
    public static void parseSFF(File sffFile, SffFileVisitor visitor) throws SFFDecoderException, FileNotFoundException{
        InputStream in = new FileInputStream(sffFile);
        try{
            parseSFF(in,visitor);
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    public static void parseSFF(InputStream in, SffFileVisitor visitor) throws SFFDecoderException{
        DataInputStream dataIn = new DataInputStream(in);
        visitor.visitFile();
        SFFCommonHeader commonHeader =commonHeaderCodec.decodeHeader(dataIn);
        visitor.visitCommonHeader(commonHeader);
        
        final long numberOfReads = commonHeader.getNumberOfReads();
        final int numberOfFlowsPerRead = commonHeader.getNumberOfFlowsPerRead();
        for(long i=0; i<numberOfReads; i++){
            SFFReadHeader readHeader = readHeaderCodec.decodeReadHeader(dataIn);
            if(visitor.visitReadHeader(readHeader)){            
                final int numberOfBases = readHeader.getNumberOfBases();
                SFFReadData readData = readDataCodec.decode(dataIn,
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
        visitor.visitEndOfFile();
        
    }
}
