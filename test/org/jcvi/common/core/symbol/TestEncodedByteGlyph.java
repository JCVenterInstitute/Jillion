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

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestEncodedByteGlyph {
   
    byte[] bytes = new byte[]{-20,40,Byte.MAX_VALUE,Byte.MIN_VALUE,21,86,99,0,4};
    List<ByteSymbol> glyphs = DefaultByteGlyphFactory.getInstance().getGlyphsFor(bytes);
    
    EncodedByteSquence sut = new EncodedByteSquence(glyphs);
    
    @Test
    public void decode(){
        assertEquals(glyphs, sut.asList());
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
