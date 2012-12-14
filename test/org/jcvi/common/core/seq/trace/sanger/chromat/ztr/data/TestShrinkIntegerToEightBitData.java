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
package org.jcvi.common.core.seq.trace.sanger.chromat.ztr.data;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.common.core.seq.trace.TraceDecoderException;
import org.jcvi.common.core.seq.trace.TraceEncoderException;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestShrinkIntegerToEightBitData {
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
        buf2.put((byte)71);
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
    public void decode() throws TraceDecoderException{
        Data sut = ShrinkToEightBitData.INTEGER_TO_BYTE;
        byte[] actual =sut.parseData(compressed);
        assertTrue(Arrays.equals(actual, uncompressed));
    }
    
    @Test
    public void encode() throws TraceEncoderException{
    	Data sut = ShrinkToEightBitData.INTEGER_TO_BYTE;
    	byte[] actual = sut.encodeData(uncompressed);
    	assertTrue(Arrays.equals(actual, compressed));
    }
    
}
