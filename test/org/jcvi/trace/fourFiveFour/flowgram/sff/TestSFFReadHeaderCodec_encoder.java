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
 * Created on Oct 8, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.nio.ByteBuffer;

import org.jcvi.Range;
import org.jcvi.trace.fourFiveFour.flowgram.sff.DefaultSFFReadHeader;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFReadHeader;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSFFReadHeaderCodec_encoder extends AbstractTestSFFReadHeaderCodec{
    private byte[] EMPTY_CLIP = new byte[]{0,0,0,0};
    private byte[] encodeReadHeader(SFFReadHeader readHeader){
        //I wrap a newly allocated byte array
        //so that it is automatically filled with zeros
        //this allows me to not worry about padding.
        ByteBuffer buf = ByteBuffer.wrap(new byte[readHeader.getHeaderLength()]);
        buf.putShort(readHeader.getHeaderLength());
        buf.putShort((short)readHeader.getName().length());
        buf.putInt(readHeader.getNumberOfBases());
        final Range qClip = readHeader.getQualityClip();
        if(qClip ==null){
            buf.put(EMPTY_CLIP);
        }
        else{
            buf.putShort((short)qClip.getStart());
            buf.putShort((short)qClip.getEnd());
        }
        final Range aClip = readHeader.getAdapterClip();
        if(aClip==null){
            buf.put(EMPTY_CLIP);
        }
        else{
            buf.putShort((short)aClip.getStart());
            buf.putShort((short)aClip.getEnd());
        }
        buf.put(readHeader.getName().getBytes());
        return buf.array();
    }

    @Test
    public void valid(){
        byte[] expectedEncodedBytes = encodeReadHeader(expectedReadHeader);
        assertArrayEquals(sut.encodeReadHeader(expectedReadHeader),expectedEncodedBytes );
    }
    @Test
    public void nullAdapterClip(){
        DefaultSFFReadHeader nullAdpaterClip = new DefaultSFFReadHeader(headerLength, numberOfBases,
                qualityClip, null, name);
        byte[] expectedEncodedBytes = encodeReadHeader(nullAdpaterClip);
        assertArrayEquals(sut.encodeReadHeader(nullAdpaterClip),expectedEncodedBytes );

    }
    @Test
    public void nullQualityClip(){
        DefaultSFFReadHeader nullQualityClip = new DefaultSFFReadHeader(headerLength, numberOfBases,
                null, adapterClip, name);
        byte[] expectedEncodedBytes = encodeReadHeader(nullQualityClip);
        assertArrayEquals(sut.encodeReadHeader(nullQualityClip),expectedEncodedBytes );

    }
}
