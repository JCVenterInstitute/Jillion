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
/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.ztr.chunk;

import java.nio.ByteBuffer;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.trace.chromat.ztr.chunk.Chunk;
import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.TraceEncoderException;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

public class TestClipChunk {

   Chunk sut = Chunk.CLIP;
   static final Range expectedClip = Range.of(12345678, 987654321);

    private static final byte[] encodedClip;
    static{    	
    	encodedClip = encode(expectedClip);
    }
	private static byte[] encode(Range clip) {
		ByteBuffer buf = ByteBuffer.allocate(9);
        buf.put((byte)0); // clip chunk
        buf.putInt((int)clip.getBegin());
        buf.putInt((int)clip.getEnd());
        byte[] temp = buf.array();
		return temp;
	}
    @Test
    public void validParse() throws TraceDecoderException{
       
        
        ZtrChromatogramBuilder mockStruct = new ZtrChromatogramBuilder("id");
        sut.parseData(encodedClip,mockStruct);
        assertEquals(expectedClip, mockStruct.clip());
    }
    
    @Test
    public void encode() throws TraceEncoderException{
    	ZtrChromatogram mockChromatogram = createMock(ZtrChromatogram.class);
    	expect(mockChromatogram.getClip()).andReturn(expectedClip);
    	replay(mockChromatogram);
    	byte[] actual =sut.encodeChunk(mockChromatogram);
    	assertArrayEquals(encodedClip, actual);
    	verify(mockChromatogram);
    }
    @Test
    public void encodeNullClipShouldEncodeZeroZero() throws TraceEncoderException{
    	ZtrChromatogram mockChromatogram = createMock(ZtrChromatogram.class);
    	expect(mockChromatogram.getClip()).andReturn(null);
    	replay(mockChromatogram);
    	byte[] actual =sut.encodeChunk(mockChromatogram);
    	assertArrayEquals(encode(Range.of(0,0)), actual);
    	verify(mockChromatogram);
    }
    @Test
    public void invalidLengthTooSmallShouldThrowTraceDecoderException(){
        try{
            sut.parseData(new byte[8], (ZtrChromatogramBuilder)null);
            fail("should throw exception if array length < 9");
        }catch(TraceDecoderException e){
            assertEquals("Invalid DefaultClip size, num of bytes = 8", e.getMessage());
        }
    }
    @Test
    public void invalidLengthTooBigShouldThrowTraceDecoderException(){
        try{
            sut.parseData(new byte[10], (ZtrChromatogramBuilder)null);
            fail("should throw exception if array length > 9");
        }catch(TraceDecoderException e){
            assertEquals("Invalid DefaultClip size, num of bytes = 10", e.getMessage());
        }
    }
}
