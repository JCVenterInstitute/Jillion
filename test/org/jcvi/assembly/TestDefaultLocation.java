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
 * Created on Jan 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.sequence.Read;
import org.jcvi.testUtil.TestUtil;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestDefaultLocation {

    Read<NucleotideEncodedGlyphs> aRead = createMock(Read.class);
    int index = 1234;
    
    DefaultLocation<Read<NucleotideEncodedGlyphs>> sut = new DefaultLocation<Read<NucleotideEncodedGlyphs>>(aRead, index);
    
    @Test
    public void constructor(){
        assertEquals(aRead ,sut.getSource());
        assertEquals(index, sut.getIndex());
    }
    
    @Test
    public void nullSourceShouldThrowIllegalArgumentExcetion(){
        try{
            new DefaultLocation<Read<NucleotideEncodedGlyphs>>(null, index);
            fail("should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e){
            assertEquals("source can not be null", e.getMessage());
        }
    }
    @Test
    public void nullNotEquals(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void differentClassNotEquals(){
        assertFalse(sut.equals("not a defaultLocation"));
    }
    
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void equalsSameValues(){
        DefaultLocation<Read<NucleotideEncodedGlyphs>> sameValues = new DefaultLocation<Read<NucleotideEncodedGlyphs>>(aRead, index);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    
    @Test
    public void differentIndexShouldNotBeEqual(){
        DefaultLocation<Read<NucleotideEncodedGlyphs>> differentIndex = new DefaultLocation<Read<NucleotideEncodedGlyphs>>(aRead, index+1);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentIndex);
    }
    @Test
    public void differentSourceShouldNotBeEqual(){
        DefaultLocation<Read<NucleotideEncodedGlyphs>> differentSource = new DefaultLocation<Read<NucleotideEncodedGlyphs>>(
                createMock(Read.class), index);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentSource);
    }
}
