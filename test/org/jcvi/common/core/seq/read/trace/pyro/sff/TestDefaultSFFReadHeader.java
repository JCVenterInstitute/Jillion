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
 * Created on Oct 8, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.seq.read.trace.pyro.sff.DefaultSFFReadHeader;
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestDefaultSFFReadHeader {
    int numberOfBases=100;
    Range qualityClip = Range.create(10,90);
    Range adapterClip= Range.create(5,95);
    String name = "sequence name";

    DefaultSFFReadHeader sut = new DefaultSFFReadHeader( numberOfBases,
            qualityClip, adapterClip, name);

    @Test
    public void constructor(){
        assertEquals(numberOfBases, sut.getNumberOfBases());
        assertEquals(qualityClip, sut.getQualityClip());
        assertEquals(adapterClip, sut.getAdapterClip());
        assertEquals(name, sut.getName());
    }

    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsWrongClass(){
        assertFalse(sut.equals("not a DefaultSFFReadHeader"));
    }

    @Test
    public void equalsSameValues(){
        DefaultSFFReadHeader sameValues = new DefaultSFFReadHeader(
                numberOfBases,
                qualityClip,
                adapterClip,
                name);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
   
    @Test
    public void notEqualsDifferentNumberOfBases(){
        DefaultSFFReadHeader differentValues = new DefaultSFFReadHeader(
                numberOfBases+1,
                qualityClip,
                adapterClip,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsNullQualityClip(){
        DefaultSFFReadHeader differentValues = new DefaultSFFReadHeader(
                numberOfBases,
                null,
                adapterClip,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsDifferentQualityClip(){
        Range differentQualityClip = qualityClip.shiftRight(2);
        DefaultSFFReadHeader differentValues = new DefaultSFFReadHeader(
                numberOfBases,
                differentQualityClip,
                adapterClip,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsNullAdapterClip(){
        DefaultSFFReadHeader differentValues = new DefaultSFFReadHeader(
                numberOfBases,
                qualityClip,
                null,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsDifferentAdapterClip(){
        Range differentAdapterClip = adapterClip.shiftRight(2);
        DefaultSFFReadHeader differentValues = new DefaultSFFReadHeader(
                numberOfBases,
                qualityClip,
                differentAdapterClip,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsDifferentName(){
        DefaultSFFReadHeader differentValues = new DefaultSFFReadHeader(
                numberOfBases,
                qualityClip,
                adapterClip,
                "different"+name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsNullName(){
        DefaultSFFReadHeader differentValues = new DefaultSFFReadHeader(
                numberOfBases,
                qualityClip,
                adapterClip,
                null);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
}
