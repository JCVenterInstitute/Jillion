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

package org.jcvi.common.core.assembly.contig.ace.consed;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.List;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.assembly.contig.ace.AceContig;
import org.jcvi.common.core.assembly.contig.ace.AceContigTestUtil;
import org.jcvi.common.core.assembly.contig.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.contig.ace.DefaultAceContig;
import org.jcvi.common.core.assembly.contig.ace.PhdInfo;
import org.jcvi.common.core.assembly.contig.ace.consed.ConsedUtil;
import org.jcvi.common.core.assembly.coverage.CoverageMap;
import org.jcvi.common.core.assembly.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.coverage.DefaultCoverageMap;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestConsedUtil_Split0x {

    private final String originalId="origId";
    private final String referenceConsensus = "AACGTACGTAAACGTACGTAA";
    
    private static class TestAceBuilder extends DefaultAceContig.Builder{
    	
    	TestAceBuilder(String id, String consensus){
    		super(id,consensus);
    	}
    	
    	TestAceBuilder addRead(String readId, String gappedBasecalls,int offset, Direction dir, Range validRange, PhdInfo phdInfo){
    		super.addRead(readId, gappedBasecalls,offset,dir,validRange,phdInfo,offset+gappedBasecalls.length());
    		return this;
    	}
    	
    }
    @Test
    public void contigWithNo0xRegionsShouldNotTrim(){       
        TestAceBuilder contigBuilder =
        	new TestAceBuilder(originalId,referenceConsensus)
        .addRead("read1", referenceConsensus.substring(0, 11), 0, 
                Direction.FORWARD, 
                Range.buildRange(0, 10).convertRange(CoordinateSystem.RESIDUE_BASED), 
                createMock(PhdInfo.class))
        .addRead("read2", referenceConsensus.substring(10), 10, 
                Direction.FORWARD, 
                Range.buildRange(0, 11).convertRange(CoordinateSystem.RESIDUE_BASED), 
                createMock(PhdInfo.class));
        
        final List<AceContig> actualcontigs = ConsedUtil.split0xContig(contigBuilder, false);
        assertEquals(1,actualcontigs.size());
        AceContig expected = new TestAceBuilder(originalId,referenceConsensus)
                                .addRead("read1", referenceConsensus.substring(0, 11), 0, 
                                        Direction.FORWARD, 
                                        Range.buildRange(0, 10).convertRange(CoordinateSystem.RESIDUE_BASED), 
                                        createMock(PhdInfo.class))
                                .addRead("read2", referenceConsensus.substring(10), 10, 
                                        Direction.FORWARD, 
                                        Range.buildRange(0, 11).convertRange(CoordinateSystem.RESIDUE_BASED), 
                                        createMock(PhdInfo.class))
                                        .build();
        
        AceContigTestUtil.assertContigsEqual(expected, actualcontigs.get(0));
    }
    
    @Test
    public void one0xRegionShouldSplitContigIn2(){
        final PhdInfo read1Phd = createMock(PhdInfo.class);
        final PhdInfo read2Phd = createMock(PhdInfo.class);

        TestAceBuilder contig = new TestAceBuilder(originalId,referenceConsensus)
		        .addRead("read1", referenceConsensus.substring(0, 11), 0, 
		                Direction.FORWARD, 
		                Range.buildRange(0, 10).convertRange(CoordinateSystem.RESIDUE_BASED), 
		                read1Phd)
		        .addRead("read2", referenceConsensus.substring(12), 12, 
		                Direction.FORWARD, 
		                Range.buildRange(0, 9).convertRange(CoordinateSystem.RESIDUE_BASED), 
		                read2Phd);
		       
    
        List<AceContig> splitContigs = ConsedUtil.split0xContig(contig,  false);
        assertEquals("# of split contigs", 2, splitContigs.size());
        
        AceContig expectedFirstContig = new TestAceBuilder(
                String.format("%s_%d_%d",originalId,1,11),referenceConsensus.substring(0, 11))
                            .addRead("read1", referenceConsensus.substring(0, 11), 0, 
                                    Direction.FORWARD, 
                                    Range.buildRange(0, 10).convertRange(CoordinateSystem.RESIDUE_BASED), 
                                    read1Phd)
                                    .build();
        AceContig expectedSecondContig = new TestAceBuilder(
                String.format("%s_%d_%d",originalId,13,21),referenceConsensus.substring(12))
                        .addRead("read2", referenceConsensus.substring(12), 0, 
                                Direction.FORWARD, 
                                Range.buildRange(0, 9).convertRange(CoordinateSystem.RESIDUE_BASED), 
                                read2Phd)
                                    .build();
        assertContigsEqual(expectedFirstContig, splitContigs.get(0));
        assertContigsEqual(expectedSecondContig, splitContigs.get(1));
    }
    
    @Test
    public void contigIdAlreadyHasCoordinatesAtTheEnd_ShouldmodifyThoseCoordinates(){

        final PhdInfo read1Phd = createMock(PhdInfo.class);
        final PhdInfo read2Phd = createMock(PhdInfo.class);
        TestAceBuilder contig = new TestAceBuilder("id_1_12",referenceConsensus)
        
		        .addRead("read1", referenceConsensus.substring(0, 11), 0, 
		                Direction.FORWARD, 
		                Range.buildRange(0, 10).convertRange(CoordinateSystem.RESIDUE_BASED), 
		                read1Phd)
		        .addRead("read2", referenceConsensus.substring(12), 12, 
		                Direction.FORWARD, 
		                Range.buildRange(0, 9).convertRange(CoordinateSystem.RESIDUE_BASED), 
		                read2Phd);
        List<AceContig> splitContigs = ConsedUtil.split0xContig(contig, true);
        
        assertEquals("# of split contigs", 2, splitContigs.size());
        
        AceContig expectedFirstContig = new TestAceBuilder(
                String.format("id_%d_%d",1,11),referenceConsensus.substring(0, 11))
                            .addRead("read1", referenceConsensus.substring(0, 11), 0, 
                                    Direction.FORWARD, 
                                    Range.buildRange(0, 10).convertRange(CoordinateSystem.RESIDUE_BASED), 
                                    read1Phd)
                                    .build();
        AceContig expectedSecondContig = new TestAceBuilder(
                String.format("id_%d_%d",13,21),referenceConsensus.substring(12))
                        .addRead("read2", referenceConsensus.substring(12), 0, 
                                Direction.FORWARD, 
                                Range.buildRange(0, 9).convertRange(CoordinateSystem.RESIDUE_BASED), 
                                read2Phd)
                                    .build();
        assertContigsEqual(expectedFirstContig, splitContigs.get(0));
        assertContigsEqual(expectedSecondContig, splitContigs.get(1));
    }
    
    private void assertContigsEqual(AceContig expected, AceContig actual){
        assertEquals("id",expected.getId(),actual.getId());
        assertEquals("consensus", expected.getConsensus().asList(), actual.getConsensus().asList());
        assertEquals("numberOfReads", expected.getNumberOfReads(), actual.getNumberOfReads());
        for(AcePlacedRead expectedRead: expected.getPlacedReads()){
            final String id = expectedRead.getId();
            assertTrue("missing read " +id,actual.containsPlacedRead(id));
            assertAcePlacedReadsEqual(expectedRead, actual.getPlacedReadById(id));
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
        assertEquals("direction",expected.getDirection(),actual.getDirection());
        
        assertEquals("phdInfo",expected.getPhdInfo(),actual.getPhdInfo());
        assertEquals("basecalls",expected.getNucleotideSequence().asList(),actual.getNucleotideSequence().asList());
        assertEquals("validRange",expected.getValidRange(),actual.getValidRange());
    }
}
