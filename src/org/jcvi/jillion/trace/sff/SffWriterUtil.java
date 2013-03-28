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
package org.jcvi.jillion.trace.sff;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

/**
 * {@code SffWriter} writes Sff formated data to an OutputStream.
 * @author dkatzel
 *
 *
 */
final class SffWriterUtil {
	
	private SffWriterUtil(){
		//can not instantaite
	}
   
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
        //write everything to an in memory byteBuffer
        //then perform a single write operation to the outputStream
        ByteBuffer temp = ByteBuffer.allocate(headerLength);
        
        temp.put(SffUtil.SFF_MAGIC_NUMBER);
        temp.put(IOUtil.convertUnsignedLongToByteArray(header.getIndexOffset()));
        temp.put(IOUtil.convertUnsignedIntToByteArray(header.getIndexLength()));
        temp.put(IOUtil.convertUnsignedIntToByteArray(header.getNumberOfReads()));
        temp.put(IOUtil.convertUnsignedShortToByteArray(headerLength));
        temp.put(IOUtil.convertUnsignedShortToByteArray(keyLength));
        temp.put(IOUtil.convertUnsignedShortToByteArray(header.getNumberOfFlowsPerRead()));
        temp.put(SffUtil.FORMAT_CODE);
        temp.put(header.getFlowSequence().toString().getBytes(IOUtil.UTF_8));
        temp.put(header.getKeySequence().toString().getBytes(IOUtil.UTF_8));
        temp.put(new byte[padding]);
        
        out.write(temp.array(), 0, headerLength);
        
        return headerLength;
    }
    
    /**
     * Get the number of bytes (plus padding) that a {@link SffCommonHeader}
     * will take up in an sff encoded file.
     * @param header the {@link SffCommonHeader} to inspect;
     * can not be null.
     * @return a positive int.
     * @throws NullPointerException if header is null or
     * {@link SffCommonHeader#getKeySequence()} returns null.
     */
    public static int getNumberOfBytesFor(SffCommonHeader header){
    	int keyLength = (int)header.getKeySequence().getLength();
        int size = 31+header.getNumberOfFlowsPerRead()+ keyLength;
        int padding =SffUtil.caclulatePaddedBytes(size);
        return size+padding;
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
        
        ByteBuffer temp = ByteBuffer.allocate(paddedHeaderLength);
        
        temp.put(IOUtil.convertUnsignedShortToByteArray(paddedHeaderLength));
       
        temp.put(IOUtil.convertUnsignedShortToByteArray(nameLength));
        temp.put(IOUtil.convertUnsignedIntToByteArray(readHeader.getNumberOfBases()));
        writeClip(readHeader.getQualityClip(),temp);
        writeClip(readHeader.getAdapterClip(), temp);
        temp.put(name.getBytes(IOUtil.UTF_8));
        temp.put(new byte[padding]);
        out.write(temp.array(),0,paddedHeaderLength);
        return paddedHeaderLength;
    }
    /**
     * Get the number of bytes (plus padding) that a {@link SffReadHeader}
     * will take up in an sff encoded file.
     * @param readHeader the {@link SffReadHeader} to inspect;
     * can not be null.
     * @return a positive int.
     * @throws NullPointerException if readHeader is null or
     * {@link SffReadHeader#getId()} returns null.
     */
    public static int getNumberOfBytesFor(SffReadHeader readHeader){
    	String id =readHeader.getId();        
        int unpaddedHeaderLength = 16+id.length();
        int padding = SffUtil.caclulatePaddedBytes(unpaddedHeaderLength);
        return  unpaddedHeaderLength+padding;
    }
    		
    public static int writeReadData(SffReadData readData, OutputStream out) throws IOException{
        final short[] flowgramValues = readData.getFlowgramValues();
        final NucleotideSequence basecalls = readData.getNucleotideSequence();
        int readDataLength = SffUtil.getReadDataLength(flowgramValues.length, (int)basecalls.getLength());
        int padding =SffUtil.caclulatePaddedBytes(readDataLength);
        
        int totalLength = readDataLength + padding;
        ByteBuffer temp= ByteBuffer.allocate(totalLength);
        for(int i=0; i<flowgramValues.length; i++){
        	temp.putShort(flowgramValues[i]);
        }
        
        
       
        temp.put(readData.getFlowIndexPerBase());
        
        temp.put(basecalls.toString().getBytes(IOUtil.UTF_8));
        temp.put(PhredQuality.toArray(readData.getQualitySequence()));
        
        temp.put(new byte[padding]);
        out.write(temp.array(), 0, totalLength);
        return totalLength;
    }
    
    public static int getNumberOfBytesFor(SffReadData readData){
    	 int readDataLength = SffUtil.getReadDataLength(readData.getFlowgramValues().length, (int)readData.getNucleotideSequence().getLength());
         int padding =SffUtil.caclulatePaddedBytes(readDataLength);
         return readDataLength+padding;
    }
    
    private static void writeClip(Range clip, ByteBuffer out) throws IOException{
        if(clip==null){
            out.put(SffUtil.EMPTY_CLIP_BYTES);
         }
         else{
        
            out.put(IOUtil.convertUnsignedShortToByteArray((int)clip.getBegin(CoordinateSystem.RESIDUE_BASED)));
            out.put(IOUtil.convertUnsignedShortToByteArray((int)clip.getEnd(CoordinateSystem.RESIDUE_BASED)));
        }
        
    }
}
