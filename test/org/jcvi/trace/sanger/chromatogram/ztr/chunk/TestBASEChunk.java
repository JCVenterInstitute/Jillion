/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.nio.ByteBuffer;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.BASEChunk;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestBASEChunk {
   
    private String decodedBases = "ACGTACGTNW-";
   BASEChunk sut =new BASEChunk();

    @Test
    public void valid() throws TraceDecoderException{
        ByteBuffer buf = ByteBuffer.allocate(decodedBases.length()+1);
        buf.put((byte)0 ); //padding
        buf.put(decodedBases.getBytes());
        ZTRChromatogramBuilder builder = new ZTRChromatogramBuilder();
        sut.parseData(buf.array(), builder);        
        assertEquals(decodedBases, builder.basecalls());
    }
}
