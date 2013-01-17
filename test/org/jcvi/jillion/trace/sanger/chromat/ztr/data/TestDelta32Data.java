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
package org.jcvi.jillion.trace.sanger.chromat.ztr.data;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import org.jcvi.jillion.internal.trace.sanger.chromat.ztr.data.DeltaEncodedData;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDelta32Data {
    private static byte[] uncompressedArray = new byte[]{16,32,49,16, 0,50,90,-80, 127,127,64,48};
    DeltaEncodedData sut = DeltaEncodedData.INTEGER;
    @Test
    public void level1(){
        IntBuffer uncompressed = ByteBuffer.wrap(uncompressedArray).asIntBuffer();
        ByteBuffer compressed = ByteBuffer.allocate(16);
        compressed.put((byte)66);
        compressed.put((byte)1);  //level
        compressed.putShort((short)0); //padding
        int delta=0;
        int prevValue=0;
        while(uncompressed.hasRemaining()){
            delta = prevValue;
            prevValue = uncompressed.get();
            compressed.putInt(prevValue -delta);
        }        
        compressed.flip();
       
        //test

        byte[] actual = sut.parseData(compressed.array());
        assertTrue(Arrays.equals(actual, uncompressedArray));
        
    }
    
    @Test
    public void level2(){
        IntBuffer uncompressed = ByteBuffer.wrap(uncompressedArray).asIntBuffer();
        ByteBuffer compressed = ByteBuffer.allocate(16);
        compressed.put((byte)66);
        compressed.put((byte)2);  //level
        compressed.putShort((short)0); //padding
        int delta=0;
        int prevValue=0;
        int prevPrevValue=0;
        while(uncompressed.hasRemaining()){
            delta = 2*prevValue -prevPrevValue;
            prevPrevValue= prevValue;
            prevValue = uncompressed.get();
            compressed.putInt(prevValue -delta);
        }        
        compressed.flip();
       
        //test
        byte[] actual = sut.parseData(compressed.array());
        assertTrue(Arrays.equals(actual, uncompressedArray));
        
    }
    
    @Test
    public void level3(){
        IntBuffer uncompressed = ByteBuffer.wrap(uncompressedArray).asIntBuffer();
        ByteBuffer compressed = ByteBuffer.allocate(16);
        compressed.put((byte)66);
        compressed.put((byte)3);  //level
        compressed.putShort((short)0); //padding
        int delta=0;
        int prevValue=0;
        int prevPrevValue=0;
        int prevPrevPrevValue =0;
        while(uncompressed.hasRemaining()){
            delta = 3*prevValue - 3*prevPrevValue + prevPrevPrevValue;
            prevPrevPrevValue= prevPrevValue;
            prevPrevValue= prevValue;
            prevValue = uncompressed.get();
            compressed.putInt(prevValue -delta);
        }        
        compressed.flip();
       
        //test
        byte[] actual = sut.parseData(compressed.array());
        assertTrue(Arrays.equals(actual, uncompressedArray));
        
    }
}
