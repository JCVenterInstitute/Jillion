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

package org.jcvi.assembly.ace.consed;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.assembly.ace.DefaultAceContig;
import org.jcvi.assembly.ace.PhdInfo;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestConsedUtil_Split0x {

    private final String originalId="origId";
    private final String referenceConsensus = "AACGTACGTAAACGTACGTAA";
    
    @Test
    public void contigWithNo0xRegionsShouldNotTrim(){
        DefaultAceContig.Builder builder = new DefaultAceContig.Builder(originalId,referenceConsensus);
    
        builder.addRead("read1", referenceConsensus.substring(0, 11), 0, 
                SequenceDirection.FORWARD, 
                Range.buildRange(0, 10).convertRange(CoordinateSystem.RESIDUE_BASED), 
                createMock(PhdInfo.class));
        builder.addRead("read2", referenceConsensus.substring(10), 10, 
                SequenceDirection.FORWARD, 
                Range.buildRange(0, 11).convertRange(CoordinateSystem.RESIDUE_BASED), 
                createMock(PhdInfo.class));
        AceContig contig =builder.build();
        CoverageMap<CoverageRegion<AcePlacedRead>> coverageMap = DefaultCoverageMap.buildCoverageMap(contig.getPlacedReads());
    
        assertSame(contig, ConsedUtil.split0xContig(contig, coverageMap).get(0));
    }
    
    
    @Test
    public void one0xRegionShouldSplitContigIn2(){
        DefaultAceContig.Builder builder = new DefaultAceContig.Builder(originalId,referenceConsensus);
    
        final PhdInfo read1Phd = createMock(PhdInfo.class);
        final PhdInfo read2Phd = createMock(PhdInfo.class);
        builder.addRead("read1", referenceConsensus.substring(0, 11), 0, 
                SequenceDirection.FORWARD, 
                Range.buildRange(0, 10).convertRange(CoordinateSystem.RESIDUE_BASED), 
                read1Phd);
        builder.addRead("read2", referenceConsensus.substring(12), 12, 
                SequenceDirection.FORWARD, 
                Range.buildRange(0, 9).convertRange(CoordinateSystem.RESIDUE_BASED), 
                read2Phd);
        AceContig contig =builder.build();
        CoverageMap<CoverageRegion<AcePlacedRead>> coverageMap = DefaultCoverageMap.buildCoverageMap(contig.getPlacedReads());
    
        List<AceContig> splitContigs = ConsedUtil.split0xContig(contig, coverageMap);
        assertEquals("# of split contigs", 2, splitContigs.size());
        
        DefaultAceContig expectedFirstContig = new DefaultAceContig.Builder(
                String.format("%s_%d_%d",originalId,1,11),referenceConsensus.substring(0, 11))
                            .addRead("read1", referenceConsensus.substring(0, 11), 0, 
                                    SequenceDirection.FORWARD, 
                                    Range.buildRange(0, 10).convertRange(CoordinateSystem.RESIDUE_BASED), 
                                    read1Phd)
                                    .build();
        DefaultAceContig expectedSecondContig = new DefaultAceContig.Builder(
                String.format("%s_%d_%d",originalId,13,21),referenceConsensus.substring(12))
                        .addRead("read2", referenceConsensus.substring(12), 0, 
                                SequenceDirection.FORWARD, 
                                Range.buildRange(0, 9).convertRange(CoordinateSystem.RESIDUE_BASED), 
                                read2Phd)
                                    .build();
        assertContigsEqual(expectedFirstContig, splitContigs.get(0));
        assertContigsEqual(expectedSecondContig, splitContigs.get(1));
    }
    
    private void assertContigsEqual(AceContig expected, AceContig actual){
        assertEquals("id",expected.getId(),actual.getId());
        assertEquals("consensus", expected.getConsensus().decode(), actual.getConsensus().decode());
        assertEquals("numberOfReads", expected.getNumberOfReads(), actual.getNumberOfReads());
        for(AcePlacedRead expectedRead: expected.getPlacedReads()){
            final String id = expectedRead.getId();
            assertTrue("missing read " +id,actual.containsPlacedRead(id));
            assertAcePlacedReadsEqual(expectedRead, actual.getPlacedReadById(id).getRealPlacedRead());
        }
    }

    /**
     * @param expectedRead
     * @param placedReadById
     */
    private void assertAcePlacedReadsEqual(AcePlacedRead expected,
            AcePlacedRead actual) {
        assertEquals("id",expected.getId(),actual.getId());
        assertEquals("offset",expected.getStart(),actual.getStart());
        assertEquals("direction",expected.getSequenceDirection(),actual.getSequenceDirection());
        
        assertEquals("phdInfo",expected.getPhdInfo(),actual.getPhdInfo());
        assertEquals("basecalls",expected.getEncodedGlyphs().decode(),actual.getEncodedGlyphs().decode());
        assertEquals("validRange",expected.getValidRange(),actual.getValidRange());
    }
}
