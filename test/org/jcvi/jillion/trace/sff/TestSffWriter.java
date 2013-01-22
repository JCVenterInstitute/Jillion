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
package org.jcvi.jillion.trace.sff;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.trace.sff.DefaultSffCommonHeader;
import org.jcvi.jillion.trace.sff.DefaultSffReadData;
import org.jcvi.jillion.trace.sff.DefaultSffReadHeader;
import org.jcvi.jillion.trace.sff.SffCommonHeader;
import org.jcvi.jillion.trace.sff.SffReadData;
import org.jcvi.jillion.trace.sff.SffReadHeader;
import org.jcvi.jillion.trace.sff.SffUtil;
import org.jcvi.jillion.trace.sff.SffWriter;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestSffWriter {
    int numberOfBases=123;
    Range qualityClip = Range.of(10, 20);
    Range adapterClip = Range.of(4, 122);
    String name = "readName";
    @Test
    public void writeCommonHeaderWithNoIndex() throws IOException{
        NucleotideSequence keySequence = new NucleotideSequenceBuilder("TCAG").build();
        short flowLength= (short)800;
        short paddedHeaderLength = 840;
        SffCommonHeader header = new DefaultSffCommonHeader.Builder()
                                    .withNoIndex()
                                    .keySequence(keySequence)
                                    .numberOfReads(1234)
                                    .numberOfFlowsPerRead(flowLength)
                                    .build();
        
        StringBuilder flows = new StringBuilder();
        for(int i=0; i< flowLength; i+=4){
            flows.append(keySequence);
        }
        byte[] expected = ByteBuffer.allocate(paddedHeaderLength)
                                        .put(SffUtil.SFF_MAGIC_NUMBER)
                                        .putLong(0L)
                                        .putInt(0)
                                        .putInt((int)header.getNumberOfReads())
                                        .putShort(paddedHeaderLength)
                                        .putShort((short)4)
                                        .putShort(flowLength)
                                        .put(SffUtil.FORMAT_CODE)
                                        .put(flows.toString().getBytes(IOUtil.UTF_8))
                                        .put(keySequence.toString().getBytes(IOUtil.UTF_8))
                                        .put(new byte[3])
                                        .array();
        ByteArrayOutputStream actual = new ByteArrayOutputStream(paddedHeaderLength);
        SffWriter.writeCommonHeader(header, actual);
        assertArrayEquals(expected, actual.toByteArray());
    }
    
    private byte[] encodeReadHeader(SffReadHeader readHeader){
        //I wrap a newly allocated byte array
        //so that it is automatically filled with zeros
        //this allows me to not worry about padding.
        final int nameLength = readHeader.getId().length();
        int padding = SffUtil.caclulatePaddedBytes(16+nameLength);
        final int headerLength = padding+16+nameLength;
        ByteBuffer buf = ByteBuffer.wrap(new byte[headerLength]);
        buf.putShort((short)headerLength);
        
        buf.putShort((short)nameLength);
        buf.putInt(readHeader.getNumberOfBases());
        final Range qClip = readHeader.getQualityClip();
        if(qClip ==null){
            buf.put(SffUtil.EMPTY_CLIP_BYTES);
        }
        else{
            buf.putShort((short)qClip.getBegin(CoordinateSystem.RESIDUE_BASED));
            buf.putShort((short)qClip.getEnd(CoordinateSystem.RESIDUE_BASED));
        }
        final Range aClip = readHeader.getAdapterClip();
        if(aClip==null){
            buf.put(SffUtil.EMPTY_CLIP_BYTES);
        }
        else{
            buf.putShort((short)aClip.getBegin(CoordinateSystem.RESIDUE_BASED));
            buf.putShort((short)aClip.getEnd(CoordinateSystem.RESIDUE_BASED));
        }
        buf.put(readHeader.getId().getBytes());
        return buf.array();
    }

    @Test
    public void valid() throws IOException{
        
        SffReadHeader readHeader =new DefaultSffReadHeader(numberOfBases,
                qualityClip, adapterClip, name);
        byte[] expectedEncodedBytes = encodeReadHeader(readHeader);
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        SffWriter.writeReadHeader(readHeader, actual);
        assertArrayEquals(expectedEncodedBytes, actual.toByteArray());
    }
    @Test
    public void nullAdapterClipShouldEncodeWithZeros() throws IOException{
        DefaultSffReadHeader nullAdpaterClip = new DefaultSffReadHeader(numberOfBases,
                qualityClip, null, name);
        byte[] expectedEncodedBytes = encodeReadHeader(nullAdpaterClip);
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        SffWriter.writeReadHeader(nullAdpaterClip, actual);
        assertArrayEquals(expectedEncodedBytes, actual.toByteArray());

    }
    @Test
    public void nullQualityClip() throws IOException{
        DefaultSffReadHeader nullQualityClip = new DefaultSffReadHeader(numberOfBases,
                null, adapterClip, name);
        byte[] expectedEncodedBytes = encodeReadHeader(nullQualityClip);
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        SffWriter.writeReadHeader(nullQualityClip, actual);
        assertArrayEquals(expectedEncodedBytes, actual.toByteArray());

    }
    
    @Test
    public void encodeReadData() throws IOException{
        QualitySequence qualities = new QualitySequenceBuilder(new byte[]{20,30,40,35}).build();
        short[] values = new short[]{100,8,97,4,200};
        byte[] indexes = new byte[]{1,2,2,0};
        NucleotideSequence bases = new NucleotideSequenceBuilder("TATT").build();

        DefaultSffReadData readData = new DefaultSffReadData(bases, indexes,  values,
                                                qualities);
        byte[] expected = encodeExpectedReadData(readData);
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        SffWriter.writeReadData(readData, actual);
        assertArrayEquals(expected, actual.toByteArray());
        
    }
    private byte[] encodeExpectedReadData(SffReadData readData){
        int basesLength =(int)readData.getNucleotideSequence().getLength();
        int numberOfFlows = readData.getFlowgramValues().length;
        int readDataLength = numberOfFlows * 2 + 3*basesLength;
        int padding =SffUtil.caclulatePaddedBytes(readDataLength);
        ByteBuffer buf = ByteBuffer.wrap(new byte[readDataLength+padding]);
        IOUtil.putShortArray(buf, readData.getFlowgramValues());
        buf.put(readData.getFlowIndexPerBase());
        buf.put(readData.getNucleotideSequence().toString().getBytes(IOUtil.UTF_8));
        buf.put(PhredQuality.toArray(readData.getQualitySequence()));
        return buf.array();
    }
}
