/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Before;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractTestSliceElement {

    String id = "id";
    Nucleotide base = Nucleotide.Adenine;
    PhredQuality quality = PhredQuality.valueOf(50);
    Direction dir = Direction.FORWARD;
    SliceElement sut;
    protected abstract SliceElement create(String id, Nucleotide base, PhredQuality qual,Direction dir);
    
    @Before
    public void setup(){
        sut = create(id,base, quality,dir);
    }
    @Test
    public void constructor(){       
        assertEquals(id, sut.getId());
        assertEquals(base, sut.getBase());
        assertEquals(quality, sut.getQuality());
        assertEquals(dir, sut.getDirection());
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
        SliceElement differentValues = create(id,Nucleotide.Cytosine, quality,dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void differentQualityShouldNotBeEqual(){
        SliceElement differentValues = create(id,base, PhredQuality.valueOf(10),dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void differentDirectionShouldNotBeEqual(){
        SliceElement differentValues = create(id,base, quality,Direction.REVERSE);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    
}
