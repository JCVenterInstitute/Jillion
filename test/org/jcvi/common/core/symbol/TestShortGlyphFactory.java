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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestShortGlyphFactory {
    ShortGlyphFactory sut = ShortGlyphFactory.getInstance();
    
    short[] shortArray = new short[]{10,20,30,40,27,66,127, -120};
    @Test
    public void getGlyphsForArray(){
        List<ShortGlyph> actual =sut.getGlyphsFor(shortArray);
        List<ShortGlyph> expected = buildExpectedGlyphList();
        assertEquals(expected, actual);
    }
    
    @Test
    public void getGlyphsForList(){
        List<ShortGlyph> actual =sut.getGlyphsFor(convertToArray(shortArray));
        List<ShortGlyph> expected = buildExpectedGlyphList();
        assertEquals(expected, actual);
    }

    private List<ShortGlyph> buildExpectedGlyphList() {
        List<ShortGlyph> expected = new ArrayList<ShortGlyph>(shortArray.length);
        for(int i=0; i<shortArray.length; i++){
            expected.add(sut.getGlyphFor(shortArray[i]));
        }
        return expected;
    }
    
    private static List<Short> convertToArray(short[] array){
        List<Short> result = new ArrayList<Short>(array.length);
        for(int i=0; i<array.length; i++){
            result.add(Short.valueOf(array[i]));
        }
        return result;
    }
}
