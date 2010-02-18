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
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import org.jcvi.Range;
import org.jcvi.TestUtil;
import org.jcvi.trace.fourFiveFour.flowgram.sff.DefaultSFFReadHeader;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestDefaultSFFReadHeader {
    short headerLength=200;
    int numberOfBases=100;
    Range qualityClip = Range.buildRange(10,90);
    Range adapterClip= Range.buildRange(5,95);
    String name = "sequence name";

    DefaultSFFReadHeader sut = new DefaultSFFReadHeader(headerLength, numberOfBases,
            qualityClip, adapterClip, name);

    @Test
    public void constructor(){
        assertEquals(headerLength, sut.getHeaderLength());
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
        DefaultSFFReadHeader sameValues = new DefaultSFFReadHeader(headerLength,
                numberOfBases,
                qualityClip,
                adapterClip,
                name);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void notEqualsDifferentHeaderLength(){
        DefaultSFFReadHeader differentValues = new DefaultSFFReadHeader((short)(headerLength+1),
                numberOfBases,
                qualityClip,
                adapterClip,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsDifferentNumberOfBases(){
        DefaultSFFReadHeader differentValues = new DefaultSFFReadHeader(headerLength,
                numberOfBases+1,
                qualityClip,
                adapterClip,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsNullQualityClip(){
        DefaultSFFReadHeader differentValues = new DefaultSFFReadHeader(headerLength,
                numberOfBases,
                null,
                adapterClip,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsDifferentQualityClip(){
        Range differentQualityClip = qualityClip.shiftRight(2);
        DefaultSFFReadHeader differentValues = new DefaultSFFReadHeader(headerLength,
                numberOfBases,
                differentQualityClip,
                adapterClip,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsNullAdapterClip(){
        DefaultSFFReadHeader differentValues = new DefaultSFFReadHeader(headerLength,
                numberOfBases,
                qualityClip,
                null,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
    @Test
    public void notEqualsDifferentAdapterClip(){
        Range differentAdapterClip = adapterClip.shiftRight(2);
        DefaultSFFReadHeader differentValues = new DefaultSFFReadHeader(headerLength,
                numberOfBases,
                qualityClip,
                differentAdapterClip,
                name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsDifferentName(){
        DefaultSFFReadHeader differentValues = new DefaultSFFReadHeader(headerLength,
                numberOfBases,
                qualityClip,
                adapterClip,
                "different"+name);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }

    @Test
    public void notEqualsNullName(){
        DefaultSFFReadHeader differentValues = new DefaultSFFReadHeader(headerLength,
                numberOfBases,
                qualityClip,
                adapterClip,
                null);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentValues);
    }
}
