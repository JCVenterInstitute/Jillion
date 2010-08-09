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

package org.jcvi.assembly.trim;

import static org.junit.Assert.assertEquals;

import org.jcvi.Range;
import org.jcvi.assembly.Contig;
import org.jcvi.assembly.DefaultContig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestElviraSangerContigEndTrimmer extends TestMinimumBidirectionalEndCoverageTrimmer {


    @Override
    protected PlacedReadTrimmer<PlacedRead, Contig<PlacedRead>> createSUT() {
        return new ElviraSangerContigEndTrimmer(2,3,4);
    }
    
   @Test
   public void _1xSangerShouldTrimOff(){
       Contig<PlacedRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
       .addRead("IVAAA04T26B11NA512F", 0, Range.buildRange(0, 7), "ACGTACGT", SequenceDirection.FORWARD)
       .addRead("read2", 2, Range.buildRange(0, 5), "GTACGT", SequenceDirection.REVERSE)
       .addRead("read3", 2, Range.buildRange(0, 5), "GTACGT", SequenceDirection.REVERSE)
       .build();
       
        sut.initializeContig(_1xContig, DefaultCoverageMap.buildCoverageMap(_1xContig.getPlacedReads()));
        PlacedRead readToTrim = _1xContig.getPlacedReadById("IVAAA04T26B11NA512F").getRealPlacedRead();
        PlacedRead readThatDoesntGetTrimmed = _1xContig.getPlacedReadById("read2").getRealPlacedRead();
        Range expectedTrimRange = Range.buildRange(2, 7);
        Range actualValidRange =sut.trimRead(readToTrim, readToTrim.getValidRange());
        assertEquals("new trimmed range",expectedTrimRange, actualValidRange);
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));

   }
   @Test
   public void _1xSangerCloneCoverageShouldTrimOff(){
       Contig<PlacedRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
       .addRead("IVAAA04T26B11NA512F", 0, Range.buildRange(0, 7), "ACGTACGT", SequenceDirection.FORWARD)
       .addRead("IVAAA04T26B11NA512FB", 0, Range.buildRange(0, 7), "ACGTACGT", SequenceDirection.FORWARD)
       .addRead("read2", 2, Range.buildRange(0, 5), "GTACGT", SequenceDirection.REVERSE)
       .addRead("read3", 2, Range.buildRange(0, 5), "GTACGT", SequenceDirection.REVERSE)
       .build();
       
        sut.initializeContig(_1xContig, DefaultCoverageMap.buildCoverageMap(_1xContig.getPlacedReads()));
        PlacedRead readToTrim = _1xContig.getPlacedReadById("IVAAA04T26B11NA512F").getRealPlacedRead();
        PlacedRead readThatDoesntGetTrimmed = _1xContig.getPlacedReadById("read2").getRealPlacedRead();
        Range expectedTrimRange = Range.buildRange(2, 7);
        Range actualValidRange =sut.trimRead(readToTrim, readToTrim.getValidRange());
        assertEquals("new trimmed range",expectedTrimRange, actualValidRange);
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));

   }
   @Test
   public void _2xSangerCloneCoverageShouldNotTrim(){
       Contig<PlacedRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
       .addRead("IVAAA04T26B11NA512F", 0, Range.buildRange(0, 7), "ACGTACGT", SequenceDirection.FORWARD)
       .addRead("IVAAB04T26B11NA512F", 0, Range.buildRange(0, 7), "ACGTACGT", SequenceDirection.FORWARD)
       .addRead("read2", 2, Range.buildRange(0, 5), "GTACGT", SequenceDirection.REVERSE)
       .addRead("read3", 2, Range.buildRange(0, 5), "GTACGT", SequenceDirection.REVERSE)
       .build();
       
        sut.initializeContig(_1xContig, DefaultCoverageMap.buildCoverageMap(_1xContig.getPlacedReads()));
        PlacedRead readThatDoesntGetTrimmed = _1xContig.getPlacedReadById("IVAAA04T26B11NA512F").getRealPlacedRead();
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));

   }
}
