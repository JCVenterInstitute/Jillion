/*
 * Created on Dec 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.trace.sanger.chromatogram.ztr.data.ThirtyTwoToEightBitData;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestThirtyTowToEightBitData {
    static int[] intValues = new int[]{20,400,12345678, Short.MAX_VALUE,0, -4, -12345678};
    static byte[] uncompressed = new byte[intValues.length *4];
    static byte[] compressed = new byte[24];
    static byte guard = -128;
    static{
        ByteBuffer buf = ByteBuffer.wrap(uncompressed);
        for(int i =0 ; i<intValues.length; i++){
            buf.putInt(intValues[i]);
        }
        
        ByteBuffer buf2 = ByteBuffer.wrap(compressed);
        buf2.put((byte)47);
        buf2.put((byte)20);
        buf2.put(guard);
        buf2.putInt(400);
        buf2.put(guard);
        buf2.putInt(12345678);
        buf2.put(guard);
        buf2.putInt(Short.MAX_VALUE);
        buf2.put((byte)0);
        buf2.put((byte)-4);
        buf2.put(guard);
        buf2.putInt(-12345678);
    }
    
    @Test
    public void decode(){
        ThirtyTwoToEightBitData sut = new ThirtyTwoToEightBitData();
        byte[] actual =sut.parseData(compressed);
        assertTrue(Arrays.equals(actual, uncompressed));
    }
    
}
