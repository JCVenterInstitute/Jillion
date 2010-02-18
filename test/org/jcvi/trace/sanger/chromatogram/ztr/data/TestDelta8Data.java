/*
 * Created on Dec 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import org.jcvi.trace.sanger.chromatogram.ztr.data.Delta8Data;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDelta8Data {

    private static byte[] uncompressed = new byte[]{10,20,10,(byte)200, (byte)190, 5};
    Delta8Data sut = new Delta8Data();
    @Test
    public void level1(){
        ShortBuffer compressed = ShortBuffer.allocate(8);
        compressed.put((byte)64);
        compressed.put((short)1);  //level
        int delta=0;
        int prevValue=0;
        for(int i=0; i< uncompressed.length; i++){
            delta = prevValue;
            prevValue = fixSign(uncompressed[i]);
            compressed.put((short)fixSign(prevValue -delta));
        }        
        compressed.flip();
        
        //test
        ByteBuffer toBytes = convertToByteBuffer(compressed);

        byte[] actual = sut.parseData(toBytes.array());
        assertTrue(Arrays.equals(actual, uncompressed));
    }
    @Test
    public void level2(){
        ShortBuffer compressed = ShortBuffer.allocate(8);
        compressed.put((byte)64);
        compressed.put((short)2);  //level
        int delta=0;
        int prevValue=0;
        int prevPrevValue=0;
        for(int i=0; i< uncompressed.length; i++){
            delta = 2*prevValue -prevPrevValue;
            prevPrevValue= prevValue;
            prevValue = fixSign(uncompressed[i]);
            compressed.put((short)fixSign(prevValue -delta));
        }        
        compressed.flip();
        //test
        ByteBuffer toBytes = convertToByteBuffer(compressed);
        byte[] actual = sut.parseData(toBytes.array());
        assertTrue(Arrays.equals(actual, uncompressed));
    }
    
    @Test
    public void level3(){
        ShortBuffer compressed = ShortBuffer.allocate(8);
        compressed.put((byte)64);
        compressed.put((short)3);  //level
        int delta=0;
        int prevValue=0;
        int prevPrevValue=0;
        int prevPrevPrevValue =0;
        for(int i=0; i< uncompressed.length; i++){
            delta = 3*prevValue - 3*prevPrevValue + prevPrevPrevValue;
            prevPrevPrevValue= prevPrevValue;
            prevPrevValue= prevValue;
            prevValue = fixSign(uncompressed[i]);
            compressed.put((short)fixSign(prevValue -delta));
        }        
        compressed.flip();
        //test
        ByteBuffer toBytes = convertToByteBuffer(compressed);
        byte[] actual = sut.parseData(toBytes.array());
        assertTrue(Arrays.equals(actual, uncompressed));
    }
    private ByteBuffer convertToByteBuffer(ShortBuffer compressed) {
        ByteBuffer toBytes = ByteBuffer.allocate(8);
        while(compressed.hasRemaining()){
            toBytes.put((byte)compressed.get());
        }
        return toBytes;
    }
    private int fixSign(int prevValue) {
        if(prevValue<0){
            prevValue +=256;
        }
        if(prevValue >255){
            prevValue -= 256;
        }
        return prevValue;
    }
}
