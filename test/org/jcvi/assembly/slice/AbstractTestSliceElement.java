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

package org.jcvi.assembly.slice;

import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.SequenceDirection;
import org.jcvi.testUtil.TestUtil;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractTestSliceElement {

    String id = "id";
    NucleotideGlyph base = NucleotideGlyph.Adenine;
    PhredQuality quality = PhredQuality.valueOf(50);
    SequenceDirection dir = SequenceDirection.FORWARD;
    SliceElement sut;
    protected abstract SliceElement create(String id, NucleotideGlyph base, PhredQuality qual,SequenceDirection dir);
    
    @Before
    public void setup(){
        sut = create(id,base, quality,dir);
    }
    @Test
    public void constructor(){       
        assertEquals(id, sut.getId());
        assertEquals(base, sut.getBase());
        assertEquals(quality, sut.getQuality());
        assertEquals(dir, sut.getSequenceDirection());
    }
    
    @Test
    public void equalsSameRefShouldBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    
    @Test
    public void equalsSameValuesShouldBeEqual(){
        SliceElement sameValues = create(id,base, quality,dir);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentIdShouldNotBeEqual(){
        SliceElement differentValues = create("different"+id,base, quality,dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    
    @Test
    public void differentBaseShouldNotBeEqual(){
        SliceElement differentValues = create(id,NucleotideGlyph.Cytosine, quality,dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void differentQualityShouldNotBeEqual(){
        SliceElement differentValues = create(id,base, PhredQuality.valueOf(10),dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void differentDirectionShouldNotBeEqual(){
        SliceElement differentValues = create(id,base, quality,SequenceDirection.REVERSE);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    
}
