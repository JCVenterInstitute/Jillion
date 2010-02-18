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
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.CLIPChunk;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestClipChunk {

   CLIPChunk sut = new CLIPChunk();
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
            sut.parseData(new byte[8], null);
            fail("should throw exception if array length < 9");
        }catch(TraceDecoderException e){
            assertEquals("Invalid DefaultClip size, num of bytes = 8", e.getMessage());
        }
    }
    @Test
    public void invalidLengthTooBigShouldThrowTraceDecoderException(){
        try{
            sut.parseData(new byte[10], null);
            fail("should throw exception if array length > 9");
        }catch(TraceDecoderException e){
            assertEquals("Invalid DefaultClip size, num of bytes = 10", e.getMessage());
        }
    }
}
