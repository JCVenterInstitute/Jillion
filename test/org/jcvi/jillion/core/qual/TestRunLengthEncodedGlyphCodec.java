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
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.qual;

import java.nio.ByteBuffer;
import java.util.List;

import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.RunLengthEncodedQualityCodec;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestRunLengthEncodedGlyphCodec {

    static private byte guard = Byte.valueOf((byte)70);
    
    RunLengthEncodedQualityCodec sut = new RunLengthEncodedQualityCodec(guard);
    
    static List<PhredQuality> decodedValues = PhredQuality.valueOf(
            new byte[]{10,20,30,40,40,40,40,40,40,50,6,guard,12,15,guard,guard,30});
    static byte[] expected;
    
    @BeforeClass
    public static void setup(){
        int numberOfGuards =3;
        int numberOfRepeatedValues = 1;
        int numberOfNonRepeatedValues = 8;
        final int expectedSize = 4+1+ numberOfNonRepeatedValues + 
                                (numberOfGuards *3) +  (numberOfRepeatedValues *4);
        ByteBuffer buf = ByteBuffer.allocate(expectedSize);
        buf.putInt(decodedValues.size());
        buf.put(guard);
        buf.put((byte)10);
        buf.put((byte)20);
        buf.put((byte)30);
        buf.put(guard);
        buf.putShort((short)6);
        buf.put((byte)40);
        buf.put((byte)50);
        buf.put((byte)6);
        buf.put(guard);
        buf.putShort((short)0);
        buf.put((byte)12);
        buf.put((byte)15);
        buf.put(guard);
        buf.putShort((short)0);
        buf.put(guard);
        buf.putShort((short)0);
        buf.put((byte)30);
        
        expected = buf.array();
    }
    
    @Test
    public void encode(){
        byte[] actual =sut.encode(decodedValues);
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void decodeLengthOf(){
        assertEquals(decodedValues.size(), sut.decodedLengthOf(expected));
    }
    
    @Test
    public void decodeIndex(){
        for(int i=0; i< decodedValues.size(); i++){
            assertEquals(Integer.toString(i),decodedValues.get(i), sut.decode(expected, i));
        }
    }
}
