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
 * Created on Nov 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.trace.sff.Flowgram;
import org.jcvi.jillion.trace.sff.SffUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestSFFUtil_getTrimRange {

    private static final Range NOT_SET = Range.of(CoordinateSystem.RESIDUE_BASED,0,0);

    Flowgram flowgram;
    long numberOfBases = 100L;
    NucleotideSequence mockBasecalls;
    @Before
    public void setup(){
        flowgram = createMock(Flowgram.class);
        mockBasecalls = createMock(NucleotideSequence.class);
        expect(mockBasecalls.getLength()).andStubReturn(numberOfBases);
        expect(flowgram.getNucleotideSequence()).andReturn(mockBasecalls);
        replay(mockBasecalls);
    }
    
    @Test
    public void noClipsSetShouldReturn1_NumBases(){
        expect(flowgram.getQualityClip()).andReturn(NOT_SET);
        expect(flowgram.getAdapterClip()).andReturn(NOT_SET);
        replay(flowgram);
        Range expectedRange = Range.of(CoordinateSystem.RESIDUE_BASED, 1,numberOfBases);
        assertEquals(expectedRange, SffUtil.getTrimRangeFor(flowgram));
        verify(flowgram);
    }
    @Test
    public void onlyQualityClipSetShouldReturnQualityClip(){
        Range qualityClip = Range.of(CoordinateSystem.RESIDUE_BASED, 5,80);
        expect(flowgram.getQualityClip()).andReturn(qualityClip);
        expect(flowgram.getAdapterClip()).andReturn(NOT_SET);
        replay(flowgram);
       assertEquals(qualityClip, SffUtil.getTrimRangeFor(flowgram));
        verify(flowgram);
    }
    @Test
    public void onlyAdapterClipSetShouldReturnAdapterClip(){
        Range adapterClip = Range.of(CoordinateSystem.RESIDUE_BASED, 5,80);
        expect(flowgram.getQualityClip()).andReturn(NOT_SET);
        expect(flowgram.getAdapterClip()).andReturn(adapterClip);
        replay(flowgram);
       assertEquals(adapterClip, SffUtil.getTrimRangeFor(flowgram));
        verify(flowgram);
    }
    @Test
    public void bothClipsSetShouldReturnIntersection(){
        Range qualityClip = Range.of(CoordinateSystem.RESIDUE_BASED, 5,80);
        Range adapterClip = Range.of(CoordinateSystem.RESIDUE_BASED, 30,75);
        expect(flowgram.getQualityClip()).andReturn(qualityClip);
        expect(flowgram.getAdapterClip()).andReturn(adapterClip);
        replay(flowgram);
        Range expectedRange = qualityClip.intersection(adapterClip);
       assertEquals(expectedRange, SffUtil.getTrimRangeFor(flowgram));
        verify(flowgram);
    }
    @Test
    public void adapterOutsideOfQualityClip(){
        Range qualityClip = Range.of(CoordinateSystem.RESIDUE_BASED, 5,80);
        Range adapterClip = Range.of(CoordinateSystem.RESIDUE_BASED, 3,85);
        expect(flowgram.getQualityClip()).andReturn(qualityClip);
        expect(flowgram.getAdapterClip()).andReturn(adapterClip);
        replay(flowgram);
        Range expectedRange = qualityClip.intersection(adapterClip);
       assertEquals(expectedRange, SffUtil.getTrimRangeFor(flowgram));
        verify(flowgram);
    }
}
