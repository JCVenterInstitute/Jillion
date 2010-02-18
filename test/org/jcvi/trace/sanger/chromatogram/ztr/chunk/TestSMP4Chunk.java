/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.chunk;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.trace.TraceDecoderException;
import org.jcvi.trace.sanger.chromatogram.ztr.ZTRChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.ztr.chunk.SMP4Chunk;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSMP4Chunk {

    private static short[] aTraces = new short[]{0,0,2,4,5,3,2,0,0,0,1};
    private static short[] cTraces = new short[]{7,5,2,0,1,0,2,1,1,0,1};
    private static short[] gTraces = new short[]{1,0,0,2,1,0,3,8,4,2,0};
    private static short[] tTraces = new short[]{0,0,2,4,2,3,2,0,5,8,25};
   
    SMP4Chunk sut= new SMP4Chunk();

    
    @Test
    public void valid() throws TraceDecoderException{
        ByteBuffer buf = ByteBuffer.allocate(aTraces.length *8 + 2);
        buf.putShort((short)0);
        for(int i=0; i<aTraces.length; i++){
            buf.putShort(aTraces[i]);
        }
        for(int i=0; i<aTraces.length; i++){
            buf.putShort(cTraces[i]);
        }
        for(int i=0; i<aTraces.length; i++){
            buf.putShort(gTraces[i]);
        }
        for(int i=0; i<aTraces.length; i++){
            buf.putShort(tTraces[i]);
        }
        ZTRChromatogramBuilder struct = new ZTRChromatogramBuilder();

        sut.parseData(buf.array(), struct);
        assertTrue(Arrays.equals(struct.aPositions(),aTraces));
        assertTrue(Arrays.equals(struct.cPositions(),cTraces));
        assertTrue(Arrays.equals(struct.gPositions(),gTraces));
        assertTrue(Arrays.equals(struct.tPositions(),tTraces));
    }
    
}
