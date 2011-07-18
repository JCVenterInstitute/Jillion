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
 * Created on Dec 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.data;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.jcvi.common.core.seq.read.trace.TraceEncoderException;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ztr.data.DeltaEncodedData;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDelta8Data {

    private static byte[] uncompressed = new byte[]{10,20,10,(byte)200, (byte)190, 5};
    private static final byte[] COMPRESSED_LEVEL_1;
    private static final byte[] COMPRESSED_LEVEL_2;
    private static final byte[] COMPRESSED_LEVEL_3;
    
    DeltaEncodedData sut = DeltaEncodedData.BYTE;
    
    static{
    	COMPRESSED_LEVEL_1 = createCompressedLevel1();
    	COMPRESSED_LEVEL_2 = createCompressedLevel2();
    	COMPRESSED_LEVEL_3 = createCompressedLevel3();
    }
    
    private static byte[] createCompressedLevel1() {
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
         return convertToByteBuffer(compressed).array();
	}
    private static byte[] createCompressedLevel2() {
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
        return convertToByteBuffer(compressed).array();
    }
    
    private static byte[] createCompressedLevel3() {
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
         return convertToByteBuffer(compressed).array();
    }
    @Test
    public void level1(){
        byte[] actual = sut.parseData(COMPRESSED_LEVEL_1);
        assertArrayEquals(actual, uncompressed);
    }
    @Test
    public void compressedLevel1() throws TraceEncoderException{
    	byte[] actual = sut.encodeData(uncompressed, DeltaEncodedData.Level.DELTA_LEVEL_1);
    	assertArrayEquals(COMPRESSED_LEVEL_1, actual);
    }
    
	@Test
    public void level2(){
        byte[] actual = sut.parseData(COMPRESSED_LEVEL_2);
        assertArrayEquals(actual, uncompressed);
    }
	@Test
    public void compressedLevel2() throws TraceEncoderException{
    	byte[] actual = sut.encodeData(uncompressed, DeltaEncodedData.Level.DELTA_LEVEL_2);
    	assertArrayEquals(COMPRESSED_LEVEL_2, actual);
    }
    
    @Test
    public void level3(){
      byte[] actual = sut.parseData(COMPRESSED_LEVEL_3);
      assertArrayEquals(actual, uncompressed);
    }
    @Test
    public void compressedLevel3() throws TraceEncoderException{
    	byte[] actual = sut.encodeData(uncompressed, DeltaEncodedData.Level.DELTA_LEVEL_3);
    	assertArrayEquals(COMPRESSED_LEVEL_3, actual);
    }
    private static ByteBuffer convertToByteBuffer(ShortBuffer compressed) {
        ByteBuffer toBytes = ByteBuffer.allocate(8);
        while(compressed.hasRemaining()){
            toBytes.put((byte)compressed.get());
        }
        return toBytes;
    }
    private static int fixSign(int prevValue) {
        if(prevValue<0){
            prevValue +=256;
        }
        if(prevValue >255){
            prevValue -= 256;
        }
        return prevValue;
    }
}
