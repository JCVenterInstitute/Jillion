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
 * Created on Dec 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import org.jcvi.trace.sanger.chromatogram.ztr.data.Delta16Data;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDelta16Data {
    private static byte[] uncompressedArray = new byte[]{16,32,49,16};
    Delta16Data sut = new Delta16Data();
    @Test
    public void level1(){
        ShortBuffer uncompressed = ByteBuffer.wrap(uncompressedArray).asShortBuffer();
        ByteBuffer compressed = ByteBuffer.allocate(6);
        compressed.put((byte)65);
        compressed.put((byte)1);  //level
        int delta=0;
        int prevValue=0;
        while(uncompressed.hasRemaining()){
            delta = prevValue;
            prevValue = uncompressed.get();
            compressed.putShort((short)(prevValue -delta));
        }        
        compressed.flip();
       
        //test
        byte[] actual = sut.parseData(compressed.array());
        assertTrue(Arrays.equals(actual, uncompressedArray));
        
    }
    
    @Test
    public void level2(){
        ShortBuffer uncompressed = ByteBuffer.wrap(uncompressedArray).asShortBuffer();
        ByteBuffer compressed = ByteBuffer.allocate(6);
        compressed.put((byte)65);
        compressed.put((byte)2);  //level
        int delta=0;
        int prevValue=0;
        int prevPrevValue=0;
        while(uncompressed.hasRemaining()){
            delta = 2*prevValue -prevPrevValue;
            prevPrevValue= prevValue;
            prevValue = uncompressed.get();
            compressed.putShort((short)(prevValue -delta));
        }        
        compressed.flip();
       
        //test
        byte[] actual = sut.parseData(compressed.array());
        assertTrue(Arrays.equals(actual, uncompressedArray));
        
    }
    
    @Test
    public void level3(){
        ShortBuffer uncompressed = ByteBuffer.wrap(uncompressedArray).asShortBuffer();
        ByteBuffer compressed = ByteBuffer.allocate(6);
        compressed.put((byte)65);
        compressed.put((byte)3);  //level
        int delta=0;
        int prevValue=0;
        int prevPrevValue=0;
        int prevPrevPrevValue =0;
        while(uncompressed.hasRemaining()){
            delta = 3*prevValue - 3*prevPrevValue + prevPrevPrevValue;
            prevPrevPrevValue= prevPrevValue;
            prevPrevValue= prevValue;
            prevValue = uncompressed.get();
            compressed.putShort((short)(prevValue -delta));
        }        
        compressed.flip();
        //test
        
        byte[] actual = sut.parseData(compressed.array());
        assertTrue(Arrays.equals(actual, uncompressedArray));
        
    }

}
