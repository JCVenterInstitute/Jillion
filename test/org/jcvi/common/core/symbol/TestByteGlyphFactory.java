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
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestByteGlyphFactory {

    ByteGlyphFactory<ByteGlyph>  sut = new ByteGlyphFactory<ByteGlyph>(){

        @Override
        protected ByteGlyph createNewGlyph(Byte b) {
            return new ByteGlyph(b);
        }
        
    };
    
    byte[] byteArray = new byte[]{10,20,30,40,27,66,127, -120};
    @Test
    public void getGlyphsForArray(){
        List<ByteGlyph> actual =sut.getGlyphsFor(byteArray);
        List<ByteGlyph> expected = buildExpectedGlyphList();
        assertEquals(expected, actual);
    }
    
    @Test
    public void getGlyphsForList(){
        List<ByteGlyph> actual =sut.getGlyphsFor(convertToArray(byteArray));
        List<ByteGlyph> expected = buildExpectedGlyphList();
        assertEquals(expected, actual);
    }

    private List<ByteGlyph> buildExpectedGlyphList() {
        List<ByteGlyph> expected = new ArrayList<ByteGlyph>(byteArray.length);
        for(int i=0; i<byteArray.length; i++){
            expected.add(sut.getGlyphFor(byteArray[i]));
        }
        return expected;
    }
    
    private static List<Byte> convertToArray(byte[] array){
        List<Byte> result = new ArrayList<Byte>(array.length);
        for(int i=0; i<array.length; i++){
            result.add(Byte.valueOf(array[i]));
        }
        return result;
    }
}
