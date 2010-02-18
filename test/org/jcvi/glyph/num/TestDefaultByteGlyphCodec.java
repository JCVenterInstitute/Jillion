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
package org.jcvi.glyph.num;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class TestDefaultByteGlyphCodec {
 private static final ByteGlyphFactory FACTORY = new ByteGlyphFactory<ByteGlyph>(){

     @Override
     protected ByteGlyph createNewGlyph(Byte b) {
         return new ByteGlyph(b);
     }
     
 };
    
    private static final byte[] decodedByteArray = new byte[]{123,10,0,Byte.MAX_VALUE, Byte.MIN_VALUE,-125,65,99};

    private static final List<ByteGlyph> decodedGlyphs = FACTORY.getGlyphsFor(decodedByteArray);

    
    DefaultByteGlyphCodec sut = new DefaultByteGlyphCodec(FACTORY);
    @Test
    public void decode(){
        List<ByteGlyph> actualGlyphs =sut.decode(decodedByteArray);
        assertEquals(decodedGlyphs, actualGlyphs);
    }
    
    @Test
    public void encode(){
        byte[] actualEncodedBytes =sut.encode(decodedGlyphs);
        assertArrayEquals(decodedByteArray, actualEncodedBytes);
    }
    
    @Test
    public void length(){
        assertEquals(decodedByteArray.length, sut.decodedLengthOf(decodedByteArray));
    }
    
    @Test
    public void indexedDecode(){
        for(int i=0; i<decodedByteArray.length; i++){
            assertEquals( decodedGlyphs.get(i).getNumber(), sut.decode(decodedByteArray, i).getNumber());
        }
        
    }
}
