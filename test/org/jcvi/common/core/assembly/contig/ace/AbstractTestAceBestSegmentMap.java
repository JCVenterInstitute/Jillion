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
 * Created on Nov 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.ace;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.contig.ace.AceBestSegment;
import org.jcvi.common.core.assembly.contig.ace.AceBestSegmentMap;
import org.jcvi.common.core.assembly.contig.ace.DefaultAceBestSegment;
import org.jcvi.common.core.assembly.contig.ace.DefaultAceBestSegmentMap;
import org.jcvi.common.core.assembly.contig.slice.DefaultSliceMap;
import org.jcvi.common.core.assembly.contig.slice.Slice;
import org.jcvi.common.core.assembly.contig.slice.SliceMap;
import org.jcvi.common.core.assembly.coverage.slice.TestSliceUtil;
import org.jcvi.common.core.seq.read.SequenceDirection;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.junit.Test;

public abstract class AbstractTestAceBestSegmentMap {
    private final NucleotideSequence consensus = new DefaultNucleotideSequence("ACGT");
    AceBestSegmentMap sut;
    SliceMap sliceMap;

    protected abstract AceBestSegmentMap createSut(SliceMap sliceMap, NucleotideSequence consensus);
    @Test(expected = NullPointerException.class)
    public void nullSliceMapShouldThrowNPE(){
        new DefaultAceBestSegmentMap(null, consensus);
    }
    @Test(expected = NullPointerException.class)
    public void nullConsensusShouldThrowNPE(){
        new DefaultAceBestSegmentMap(createMock(SliceMap.class), null);
    }
    
    @Test
    public void oneReadProvidesEntireBestSegment(){
        List<Slice> slices =TestSliceUtil.createSlicesFrom(
                Arrays.asList("ACGT"), 
                new byte[][]{new byte[]{30,30,30,30}},            
                Arrays.asList(SequenceDirection.FORWARD));
     
        SliceMap sliceMap = new DefaultSliceMap(slices);
        sut = createSut(sliceMap, consensus);
        assertEquals(1, sut.getNumberOfBestSegments());
        DefaultAceBestSegment expectedSegment = 
            new DefaultAceBestSegment("read_0", Range.buildRange(0, 3));
        assertEquals(expectedSegment, sut.getBestSegmentFor(0));
        assertEquals(expectedSegment, sut.getBestSegmentFor(1));
        assertEquals(expectedSegment, sut.getBestSegmentFor(2));
        assertEquals(expectedSegment, sut.getBestSegmentFor(3));
        Iterator<AceBestSegment> actualIter = sut.iterator();
        assertEquals(expectedSegment, actualIter.next());
        assertFalse(actualIter.hasNext());
    }
    @Test
    public void twoBestSegments(){
        List<Slice> slices =TestSliceUtil.createSlicesFrom(
                Arrays.asList("ACGN", "NNNT"), 
                new byte[][]{new byte[]{30,30,30,30},new byte[]{30,30,30,30}},            
                Arrays.asList(SequenceDirection.FORWARD,SequenceDirection.FORWARD));
     
        SliceMap sliceMap = new DefaultSliceMap(slices);
        sut = createSut(sliceMap, consensus);
        assertEquals(2, sut.getNumberOfBestSegments());
        DefaultAceBestSegment expectedSegment1 = 
            new DefaultAceBestSegment("read_0", Range.buildRange(0, 2));
        DefaultAceBestSegment expectedSegment2 = 
            new DefaultAceBestSegment("read_1", Range.buildRange(3, 3));
        assertEquals(expectedSegment1, sut.getBestSegmentFor(0));
        assertEquals(expectedSegment1, sut.getBestSegmentFor(1));
        assertEquals(expectedSegment1, sut.getBestSegmentFor(2));
        assertEquals(expectedSegment2, sut.getBestSegmentFor(3));
        
        Iterator<AceBestSegment> actualIter = sut.iterator();
        assertEquals(expectedSegment1, actualIter.next());
        assertEquals(expectedSegment2, actualIter.next());
        assertFalse(actualIter.hasNext());
    }
    @Test
    public void threeBestSegments(){
        List<Slice> slices =TestSliceUtil.createSlicesFrom(
                Arrays.asList("ACNT", "NNGN"), 
                new byte[][]{new byte[]{30,30,30,30},new byte[]{30,30,30,30}},            
                Arrays.asList(SequenceDirection.FORWARD,SequenceDirection.FORWARD));
     
        SliceMap sliceMap = new DefaultSliceMap(slices);
        sut = createSut(sliceMap, consensus);
        assertEquals(3, sut.getNumberOfBestSegments());
        DefaultAceBestSegment expectedSegment1 = 
            new DefaultAceBestSegment("read_0", Range.buildRange(0, 1));
        DefaultAceBestSegment expectedSegment2 = 
            new DefaultAceBestSegment("read_1", Range.buildRange(2, 2));
        DefaultAceBestSegment expectedSegment3 = 
            new DefaultAceBestSegment("read_0", Range.buildRange(3, 3));
        
        assertEquals(expectedSegment1, sut.getBestSegmentFor(0));
        assertEquals(expectedSegment1, sut.getBestSegmentFor(1));
        assertEquals(expectedSegment2, sut.getBestSegmentFor(2));
        assertEquals(expectedSegment3, sut.getBestSegmentFor(3));
        
        Iterator<AceBestSegment> actualIter = sut.iterator();
        assertEquals(expectedSegment1, actualIter.next());
        assertEquals(expectedSegment2, actualIter.next());
        assertEquals(expectedSegment3, actualIter.next());
        assertFalse(actualIter.hasNext());
    }
    @Test
    public void keepExtendingBestSegments(){
        List<Slice> slices =TestSliceUtil.createSlicesFrom(
                Arrays.asList("ACNT", "NNGT"), 
                new byte[][]{new byte[]{30,30,30,30},new byte[]{30,30,30,30}},            
                Arrays.asList(SequenceDirection.FORWARD,SequenceDirection.FORWARD));
     
        SliceMap sliceMap = new DefaultSliceMap(slices);
        sut = createSut(sliceMap, consensus);
        assertEquals(2, sut.getNumberOfBestSegments());
        DefaultAceBestSegment expectedSegment1 = 
            new DefaultAceBestSegment("read_0", Range.buildRange(0, 1));
        DefaultAceBestSegment expectedSegment2 = 
            new DefaultAceBestSegment("read_1", Range.buildRange(2, 3));

        
        assertEquals(expectedSegment1, sut.getBestSegmentFor(0));
        assertEquals(expectedSegment1, sut.getBestSegmentFor(1));
        assertEquals(expectedSegment2, sut.getBestSegmentFor(2));
        assertEquals(expectedSegment2, sut.getBestSegmentFor(3));
        
        Iterator<AceBestSegment> actualIter = sut.iterator();
        assertEquals(expectedSegment1, actualIter.next());
        assertEquals(expectedSegment2, actualIter.next());
        assertFalse(actualIter.hasNext());
    }
}
