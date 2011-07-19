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

import org.jcvi.Range;
import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.DefaultContig;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.coverage.DefaultCoverageMap;
import org.jcvi.common.core.seq.read.SequenceDirection;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestMinimumEndCoverageTrimmer {

    MinimumEndCoverageTrimmer<PlacedRead, Contig<PlacedRead>> sut;
    
    @Before
    public void setup(){
        sut = new MinimumEndCoverageTrimmer<PlacedRead, Contig<PlacedRead>>(2);
    }
    
    @Test
    public void allBelowMinCoverageReturnEmptyRange(){
        Contig<PlacedRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.buildRange(0, 7), "ACGTACGT", SequenceDirection.FORWARD)
                        .build();
        sut.initializeContig(_1xContig, DefaultCoverageMap.buildCoverageMap(_1xContig));
        PlacedRead read = _1xContig.getPlacedReadById("read1");
        Range actualValidRange =sut.trimRead(read, read.getValidRange());
        assertEquals(Range.buildEmptyRange(), actualValidRange);
    }
    @Test
    public void leftEndBelowMinShouldTrimOff(){
        Contig<PlacedRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.buildRange(0, 7), "ACGTACGT", SequenceDirection.FORWARD)
                        .addRead("read2", 2, Range.buildRange(0, 5), "GTACGT", SequenceDirection.FORWARD)
                        .build();
        sut.initializeContig(_1xContig, DefaultCoverageMap.buildCoverageMap(_1xContig));
        PlacedRead readToTrim = _1xContig.getPlacedReadById("read1");
        PlacedRead readThatDoesntGetTrimmed = _1xContig.getPlacedReadById("read2");
        Range expectedTrimRange = Range.buildRange(2, 7);
        Range actualValidRange =sut.trimRead(readToTrim, readToTrim.getValidRange());
        assertEquals("new trimmed range",expectedTrimRange, actualValidRange);
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));
    }
    
    @Test
    public void rightEndBelowMinShouldTrimOff(){
        Contig<PlacedRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.buildRange(0, 7), "ACGTACGT", SequenceDirection.FORWARD)
                        .addRead("read2", 0, Range.buildRange(0, 5), "ACGTAC", SequenceDirection.FORWARD)
                        .build();
        sut.initializeContig(_1xContig, DefaultCoverageMap.buildCoverageMap(_1xContig));
        PlacedRead readToTrim = _1xContig.getPlacedReadById("read1");
        PlacedRead readThatDoesntGetTrimmed = _1xContig.getPlacedReadById("read2");
        Range expectedTrimRange = Range.buildRange(0, 5);
        Range actualValidRange =sut.trimRead(readToTrim, readToTrim.getValidRange());
        assertEquals("new trimmed range",expectedTrimRange, actualValidRange);
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));
    }
    
    @Test
    public void noneBelowMinShouldIgnore(){
        Contig<PlacedRead> _2xContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.buildRange(0, 7), "ACGTACGT", SequenceDirection.FORWARD)
                        .addRead("read2", 0,  Range.buildRange(0, 7), "ACGTACGT", SequenceDirection.FORWARD)
                        .build();
        sut.initializeContig(_2xContig, DefaultCoverageMap.buildCoverageMap(_2xContig));
        for(PlacedRead readThatDoesntGetTrimmed : _2xContig.getPlacedReads()){
            final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getValidRange();
            assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));
       
        }
       
    }
}
