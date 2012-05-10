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

package org.jcvi.common.core.assembly.util.trimmer;

import static org.junit.Assert.assertEquals;

import org.jcvi.assembly.trim.ElviraSangerContigEndTrimmer;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.DefaultContig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageMapFactory;
import org.jcvi.common.core.assembly.util.trimmer.PlacedReadTrimmer;
import org.jcvi.common.core.assembly.util.trimmer.TestMinimumBidirectionalEndCoverageTrimmer;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestElviraSangerContigEndTrimmer extends TestMinimumBidirectionalEndCoverageTrimmer {


    @Override
    protected PlacedReadTrimmer<AssembledRead, Contig<AssembledRead>> createSUT() {
        return new ElviraSangerContigEndTrimmer(2,3,4);
    }
    
   @Test
   public void _1xSangerShouldTrimOff(){
       Contig<AssembledRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
       .addRead("IVAAA04T26B11NA512F", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
       .addRead("read2", 2, Range.create(0, 5), "GTACGT", Direction.REVERSE, 10)
       .addRead("read3", 2, Range.create(0, 5), "GTACGT", Direction.REVERSE, 10)
       .build();
       
        sut.initializeContig(_1xContig, CoverageMapFactory.createGappedCoverageMapFromContig(_1xContig));
        AssembledRead readToTrim = _1xContig.getRead("IVAAA04T26B11NA512F");
        AssembledRead readThatDoesntGetTrimmed = _1xContig.getRead("read2");
        Range expectedTrimRange = Range.create(2, 7);
        Range actualValidRange =sut.trimRead(readToTrim, readToTrim.getReadInfo().getValidRange());
        assertEquals("new trimmed range",expectedTrimRange, actualValidRange);
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getReadInfo().getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));

   }
   @Test
   public void _1xClosureSangerShouldTrimOff(){
       Contig<AssembledRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
       .addRead("JHVXC05T00NP01F", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
       .addRead("read2", 2, Range.create(0, 5), "GTACGT", Direction.REVERSE, 10)
       .addRead("read3", 2, Range.create(0, 5), "GTACGT", Direction.REVERSE, 10)
       .build();
       
        sut.initializeContig(_1xContig, CoverageMapFactory.createGappedCoverageMapFromContig(_1xContig));
        AssembledRead readToTrim = _1xContig.getRead("JHVXC05T00NP01F");
        AssembledRead readThatDoesntGetTrimmed = _1xContig.getRead("read2");
        Range expectedTrimRange = Range.create(2, 7);
        Range actualValidRange =sut.trimRead(readToTrim, readToTrim.getReadInfo().getValidRange());
        assertEquals("new trimmed range",expectedTrimRange, actualValidRange);
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getReadInfo().getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));

   }
   @Test
   public void _1xSangerCloneCoverageShouldTrimOff(){
       Contig<AssembledRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
       .addRead("IVAAA04T26B11NA512F", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
       .addRead("IVAAA04T26B11NA512FB", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
       .addRead("read2", 2, Range.create(0, 5), "GTACGT", Direction.REVERSE, 10)
       .addRead("read3", 2, Range.create(0, 5), "GTACGT", Direction.REVERSE, 10)
       .build();
       
        sut.initializeContig(_1xContig, CoverageMapFactory.createGappedCoverageMapFromContig(_1xContig));
        AssembledRead readToTrim = _1xContig.getRead("IVAAA04T26B11NA512F");
        AssembledRead readThatDoesntGetTrimmed = _1xContig.getRead("read2");
        Range expectedTrimRange = Range.create(2, 7);
        Range actualValidRange =sut.trimRead(readToTrim, readToTrim.getReadInfo().getValidRange());
        assertEquals("new trimmed range",expectedTrimRange, actualValidRange);
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getReadInfo().getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));

   }
   @Test
   public void _2xSangerCloneCoverageShouldNotTrim(){
       Contig<AssembledRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
       .addRead("IVAAA04T26B11NA512F", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
       .addRead("IVAAB04T26B11NA512F", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
       .addRead("read2", 2, Range.create(0, 5), "GTACGT", Direction.REVERSE, 10)
       .addRead("read3", 2, Range.create(0, 5), "GTACGT", Direction.REVERSE, 10)
       .build();
       
        sut.initializeContig(_1xContig, CoverageMapFactory.createGappedCoverageMapFromContig(_1xContig));
        AssembledRead readThatDoesntGetTrimmed = _1xContig.getRead("IVAAA04T26B11NA512F");
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getReadInfo().getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));

   }
  
   @Test
   public void _2xClosureAndNonClosureMixSangerCloneCoverageShouldNotTrim(){
       Contig<AssembledRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
       .addRead("IVAAB04T26B11NA512F", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
       .addRead("JHVXC05T00NP0334F", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
       .addRead("read2", 2, Range.create(0, 5), "GTACGT", Direction.REVERSE, 10)
       .addRead("read3", 2, Range.create(0, 5), "GTACGT", Direction.REVERSE, 10)
       .build();
       
        sut.initializeContig(_1xContig, CoverageMapFactory.createGappedCoverageMapFromContig(_1xContig));
        AssembledRead readThatDoesntGetTrimmed = _1xContig.getRead("JHVXC05T00NP0334F");
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getReadInfo().getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));

   }
}
