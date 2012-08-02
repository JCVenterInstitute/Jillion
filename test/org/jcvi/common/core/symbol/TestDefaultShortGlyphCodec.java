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
 * Created on Jan 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestDefaultShortGlyphCodec {
    private static final ShortGlyphFactory FACTORY = ShortGlyphFactory.getInstance();
    
    private static final short[] decodedShorts = new short[]{12345,10,0,Short.MAX_VALUE, Short.MIN_VALUE,-255,256,5000};

    private static final List<ShortSymbol> decodedGlyphs = FACTORY.getGlyphsFor(decodedShorts);
    
    private static byte[] encodedShortsAsByteArray;
    
    @BeforeClass
    public static void createByteArray(){
        ByteBuffer buf = ByteBuffer.allocate(decodedShorts.length *2);
        for(int i=0; i<decodedShorts.length; i++){
            buf.putShort(decodedShorts[i]);
        }
        encodedShortsAsByteArray = buf.array();
    }
    
    DefaultShortGlyphCodec sut = DefaultShortGlyphCodec.getInstance();
    @Test
    public void decode(){
        List<ShortSymbol> actualGlyphs =sut.decode(encodedShortsAsByteArray);
        assertEquals(decodedGlyphs, actualGlyphs);
    }
    
    @Test
    public void encode(){
        byte[] actualEncodedBytes =sut.encode(decodedGlyphs);
        assertArrayEquals(encodedShortsAsByteArray, actualEncodedBytes);
    }
    
    @Test
    public void length(){
        assertEquals(decodedShorts.length, sut.decodedLengthOf(encodedShortsAsByteArray));
    }
    
    @Test
    public void indexedDecode(){
        for(int i=0; i<decodedShorts.length; i++){
            assertEquals( decodedGlyphs.get(i).getValue(), sut.decode(encodedShortsAsByteArray, i).getValue());
        }
    }
    
    
  
}
