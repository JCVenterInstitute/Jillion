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
package org.jcvi.glyph.num;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class TestEncodedShortGlyph {
    private static final ShortGlyphFactory FACTORY = ShortGlyphFactory.getInstance();
    short[] bytes = new short[]{-20,1000,1138,123,Short.MAX_VALUE,0, Short.MIN_VALUE, 23243, 12312};
    List<ShortGlyph> glyphs = FACTORY.getGlyphsFor(bytes);
    
    EncodedShortGlyph sut = new EncodedShortGlyph(glyphs);
    
    @Test
    public void decode(){
        assertEquals(glyphs, sut.decode());
    }
    @Test
    public void length(){
        assertEquals(bytes.length, sut.getLength());
    }
    
    @Test
    public void getIndex(){
        assertEquals(glyphs.get(0), sut.get(0));
        assertEquals(glyphs.get(3), sut.get(3));
        assertEquals(glyphs.get(8), sut.get(8));
    }
}
