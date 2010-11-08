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
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.nio.ByteBuffer;

import org.jcvi.Range;
import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestClipChunk {

   Chunk sut = Chunk.CLIP;
    Range expected = Range.buildRange(12345678, 987654321);

    @Test
    public void validParse() throws TraceDecoderException{
        ByteBuffer buf = ByteBuffer.allocate(9);
        buf.put((byte)0); // clip chunk
        buf.putInt((int)expected.getStart());
        buf.putInt((int)expected.getEnd());
        
        
        ZTRChromatogramBuilder mockStruct = new ZTRChromatogramBuilder();
        sut.parseData(buf.array(),mockStruct);
        assertEquals(expected, mockStruct.clip());
    }
    
    @Test
    public void invalidLengthTooSmallShouldThrowTraceDecoderException(){
        try{
            sut.parseData(new byte[8], (ZTRChromatogramBuilder)null);
            fail("should throw exception if array length < 9");
        }catch(TraceDecoderException e){
            assertEquals("Invalid DefaultClip size, num of bytes = 8", e.getMessage());
        }
    }
    @Test
    public void invalidLengthTooBigShouldThrowTraceDecoderException(){
        try{
            sut.parseData(new byte[10], (ZTRChromatogramBuilder)null);
            fail("should throw exception if array length > 9");
        }catch(TraceDecoderException e){
            assertEquals("Invalid DefaultClip size, num of bytes = 10", e.getMessage());
        }
    }
}
