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

package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.io.IOUtil;

/**
 * {@code SffWriter} writes Sff formated data to an OutputStream.
 * @author dkatzel
 *
 *
 */
public class SffWriter {
   
    /**
     * Writes the given SffCommonHeader to the given outputStream.
     * @param header the header to write.
     * @param out the {@link OutputStream} to write to.
     * @return the number of bytes written
     * @throws IOException if there is a problem writing to the {@link OutputStream}.
     * @throws NullPointerException if either parameter is null.
     */
    public static int writeCommonHeader(SffCommonHeader header, OutputStream out) throws IOException{
        int keyLength = (int)header.getKeySequence().getLength();
        int size = 31+header.getNumberOfFlowsPerRead()+ keyLength;
        int padding =SffUtil.caclulatePaddedBytes(size);
        int headerLength = size+padding;
        out.write(SffUtil.SFF_MAGIC_NUMBER);
        out.write(IOUtil.convertUnsignedLongToByteArray(header.getIndexOffset()));
        out.write(IOUtil.convertUnsignedIntToByteArray(header.getIndexLength()));
        out.write(IOUtil.convertUnsignedIntToByteArray(header.getNumberOfReads()));
        out.write(IOUtil.convertUnsignedShortToByteArray(headerLength));
        out.write(IOUtil.convertUnsignedShortToByteArray(keyLength));
        out.write(IOUtil.convertUnsignedShortToByteArray(header.getNumberOfFlowsPerRead()));
        out.write(SffUtil.FORMAT_CODE);
        out.write(header.getFlowSequence().toString().getBytes(IOUtil.UTF_8));
        out.write(header.getKeySequence().toString().getBytes(IOUtil.UTF_8));
        out.write(new byte[padding]);
        out.flush();
        
        return headerLength;
    }
    /**
     * Writes the given {@link SffReadHeader} to the given {@link OutputStream}.
     * @param readHeader the readHeader to write.
     * @param out the {@link OutputStream} to write to.
     * @return the number of bytes written to output stream.
     * @throws IOException if there is a problem writing the data
     * to the {@link OutputStream}.
     * @throws NullPointerException if either parameter is null.
     */
    public static int writeReadHeader(SffReadHeader readHeader, OutputStream out) throws IOException{
        String name =readHeader.getId();
        final int nameLength = name.length();
        
        int unpaddedHeaderLength = 16+nameLength;
        int padding = SffUtil.caclulatePaddedBytes(unpaddedHeaderLength);
        int paddedHeaderLength = unpaddedHeaderLength+padding;
        out.write(IOUtil.convertUnsignedShortToByteArray(paddedHeaderLength));
       
        out.write(IOUtil.convertUnsignedShortToByteArray(nameLength));
        out.write(IOUtil.convertUnsignedIntToByteArray(readHeader.getNumberOfBases()));
        writeClip(readHeader.getQualityClip(),out);
        writeClip(readHeader.getAdapterClip(), out);
        out.write(name.getBytes(IOUtil.UTF_8));
        out.write(new byte[padding]);
        out.flush();
        return paddedHeaderLength;
    }
    
    public static int writeReadData(SffReadData readData, OutputStream out) throws IOException{
        final short[] flowgramValues = readData.getFlowgramValues();
        ByteBuffer flowValues= ByteBuffer.allocate(flowgramValues.length*2);
        for(int i=0; i<flowgramValues.length; i++){
            flowValues.putShort(flowgramValues[i]);
        }
        out.write(flowValues.array());
        out.write(readData.getFlowIndexPerBase());
        final String basecalls = readData.getBasecalls();
        out.write(basecalls.getBytes(IOUtil.UTF_8));
        out.write(readData.getQualities());
        int readDataLength = SffUtil.getReadDataLength(flowgramValues.length, basecalls.length());
        int padding =SffUtil.caclulatePaddedBytes(readDataLength);
        out.write(new byte[padding]);
        out.flush();
        return readDataLength+padding;
    }
    
    private static void writeClip(Range clip, OutputStream out) throws IOException{
        if(clip==null){
            out.write(SffUtil.EMPTY_CLIP_BYTES);
         }
         else{
        
            out.write(IOUtil.convertUnsignedShortToByteArray((int)clip.getBegin(CoordinateSystem.RESIDUE_BASED)));
            out.write(IOUtil.convertUnsignedShortToByteArray((int)clip.getEnd(CoordinateSystem.RESIDUE_BASED)));
        }
        
    }
}
