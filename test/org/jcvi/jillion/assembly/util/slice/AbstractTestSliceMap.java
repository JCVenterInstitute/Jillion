/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util.slice;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.util.slice.GapQualityValueStrategies;
import org.jcvi.jillion.assembly.util.slice.QualityValueStrategy;
import org.jcvi.jillion.assembly.util.slice.SliceMap;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.internal.assembly.DefaultContig;
import org.junit.Before;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractTestSliceMap {

    protected abstract SliceMap createSliceMapFor(Contig<AssembledRead> contig, QualitySequenceDataStore qualityDatastore, QualityValueStrategy qualityValueStrategy);
    private QualitySequenceDataStore qualityDataStore;
    @Before
    public void setup(){
        Map<String, QualitySequence> qualities = new HashMap<String, QualitySequence>();
        qualities.put("read_0", new QualitySequenceBuilder(new byte[]{10,12,14,16,18,20,22,24}).build());
        qualities.put("read_1", new QualitySequenceBuilder(new byte[]{1,2,3,4,5,6,7,8}).build());
        qualities.put("read_2", new QualitySequenceBuilder(new byte[]{15,16,17,18}).build());
        qualityDataStore = DataStoreUtil.adapt(QualitySequenceDataStore.class, qualities);
    }
    @Test
    public void allSlicesSameDepth(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
                                    .addRead("read_0", 0, "ACGTACGT")
                                    .addRead("read_1", 0, "ACGTACGT")
                                    .build();
        SliceMap sut = createSliceMapFor(contig, qualityDataStore,GapQualityValueStrategies.LOWEST_FLANKING);
        assertEquals(8L, sut.getSize());
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AA", 10,1),
                    sut.getSlice(0));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("CC", 12,2),
                sut.getSlice(1));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GG", 14,3),
                sut.getSlice(2));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TT", 16,4),
                sut.getSlice(3));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AA", 18,5),
                sut.getSlice(4));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("CC", 20,6),
                sut.getSlice(5));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GG", 22,7),
                sut.getSlice(6));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TT", 24,8),
                sut.getSlice(7));
    }
    @Test
    public void multipleDepthSlices(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
                                    .addRead("read_0", 0, "ACGTACGT")
                                    .addRead("read_1", 0, "ACGTACGT")
                                    .addRead("read_2", 2,   "GTAC")
                                    .build();
        SliceMap sut = createSliceMapFor(contig, qualityDataStore,GapQualityValueStrategies.LOWEST_FLANKING);
        assertEquals(8L, sut.getSize());
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AA", 10,1),
                    sut.getSlice(0));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("CC", 12,2),
                sut.getSlice(1));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GGG", 14,3,15),
                sut.getSlice(2));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TTT", 16,4,16),
                sut.getSlice(3));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AAA", 18,5,17),
                sut.getSlice(4));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("CCC", 20,6,18),
                sut.getSlice(5));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GG", 22,7),
                sut.getSlice(6));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TT", 24,8),
                sut.getSlice(7));
    }
    
    @Test
    public void multipleBasecallsPerSlice(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
                                    .addRead("read_0", 0, "ACGTACGT")
                                    .addRead("read_1", 0, "RCGTACGT")
                                    .addRead("read_2", 2,   "GWAC")
                                    .build();
        SliceMap sut = createSliceMapFor(contig, qualityDataStore,GapQualityValueStrategies.LOWEST_FLANKING);
        assertEquals(8L, sut.getSize());
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AR", 10,1),
                    sut.getSlice(0));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("CC", 12,2),
                sut.getSlice(1));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GGG", 14,3,15),
                sut.getSlice(2));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TTW", 16,4,16),
                sut.getSlice(3));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AAA", 18,5,17),
                sut.getSlice(4));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("CCC", 20,6,18),
                sut.getSlice(5));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GG", 22,7),
                sut.getSlice(6));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TT", 24,8),
                sut.getSlice(7));
    }
    
    @Test
    public void gapsInSliceShouldUseLowestFlankingQualityValues(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("contigId", "ACGTACGT")
                                    .addRead("read_0", 0, "ACGTACGT")
                                    .addRead("read_1", 0, "RCGTA-GT")
                                    .addRead("read_2", 2,   "G-AC")
                                    .build();
        SliceMap sut = createSliceMapFor(contig, qualityDataStore,GapQualityValueStrategies.LOWEST_FLANKING);
        assertEquals(8L, sut.getSize());
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AR", 10,1),
                    sut.getSlice(0));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("CC", 12,2),
                sut.getSlice(1));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GGG", 14,3,15),
                sut.getSlice(2));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TT-", 16,4,15),
                sut.getSlice(3));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("AAA", 18,5,16),
                sut.getSlice(4));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("C-C", 20,5,17),
                sut.getSlice(5));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("GG", 22,6),
                sut.getSlice(6));
        assertEquals(TestSliceUtil.createIsolatedSliceFrom("TT", 24,7),
                sut.getSlice(7));
    }
    
}
