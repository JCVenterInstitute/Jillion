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

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.DefaultContig;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;
import org.jcvi.common.core.assembly.util.trimmer.MinimumBidirectionalEndCoverageTrimmer;
import org.jcvi.common.core.assembly.util.trimmer.PlacedReadTrimmer;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestMinimumBidirectionalEndCoverageTrimmer {
    PlacedReadTrimmer<PlacedRead, Contig<PlacedRead>> sut;
    
    @Before
    public void setup(){
        sut = createSUT();
    }
    
    protected PlacedReadTrimmer<PlacedRead, Contig<PlacedRead>> createSUT(){
        return new MinimumBidirectionalEndCoverageTrimmer<PlacedRead, Contig<PlacedRead>>(3,4);
    }
    
    @Test
    public void allBelowMinCoverageReturnEmptyRange(){
        Contig<PlacedRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.buildRange(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .build();
        sut.initializeContig(_1xContig, DefaultCoverageMap.buildCoverageMap(_1xContig));
        PlacedRead read = _1xContig.getPlacedReadById("read1");
        Range actualValidRange =sut.trimRead(read, read.getValidRange());
        assertEquals(Range.buildEmptyRange(), actualValidRange);
    }
    
    @Test
    public void leftEndBelowMinShouldTrimOff(){
        Contig<PlacedRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.buildRange(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read2", 2, Range.buildRange(0, 5), "GTACGT", Direction.REVERSE, 10)
                        .addRead("read3", 2, Range.buildRange(0, 5), "GTACGT", Direction.REVERSE, 10)
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
    public void biDirectionalMinCoverageShouldIgnore(){
        Contig<PlacedRead> _3xBiDirectionalContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.buildRange(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read2", 0,  Range.buildRange(0, 7), "ACGTACGT", Direction.REVERSE, 10)
                        .addRead("read3", 0,  Range.buildRange(0, 7), "ACGTACGT", Direction.REVERSE, 10)
                        .build();
        sut.initializeContig(_3xBiDirectionalContig, DefaultCoverageMap.buildCoverageMap(_3xBiDirectionalContig));
        for(PlacedRead readThatDoesntGetTrimmed : _3xBiDirectionalContig.getPlacedReads()){
            final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getValidRange();
            assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));
       
        }
    }
    @Test
    public void uniDirectionalLeftShouldTrimLeftEnd(){
        Contig<PlacedRead> _3xBiDirectionalContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.buildRange(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read2", 0, Range.buildRange(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read3", 2,  Range.buildRange(0, 5), "GTACGT", Direction.REVERSE, 10)
                        .build();
        sut.initializeContig(_3xBiDirectionalContig, DefaultCoverageMap.buildCoverageMap(_3xBiDirectionalContig));
        
        PlacedRead readToTrim = _3xBiDirectionalContig.getPlacedReadById("read1");
        Range expectedTrimRange = Range.buildRange(2, 7);
        Range actualValidRange =sut.trimRead(readToTrim, readToTrim.getValidRange());
        assertEquals("new trimmed range",expectedTrimRange, actualValidRange);
        
        PlacedRead read2ToTrim = _3xBiDirectionalContig.getPlacedReadById("read2");
        Range expectedTrimRange2 = Range.buildRange(2, 7);
        Range actualValidRange2 =sut.trimRead(read2ToTrim, read2ToTrim.getValidRange());
        assertEquals("new trimmed range",expectedTrimRange2, actualValidRange2);
        
        
        PlacedRead readThatDoesntGetTrimmed = _3xBiDirectionalContig.getPlacedReadById("read3");
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));
    }
    
    @Test
    public void uniDirectionalRightShouldTrimLeftEnd(){
        Contig<PlacedRead> _3xBiDirectionalContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.buildRange(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read2", 0, Range.buildRange(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read3", 0,  Range.buildRange(0, 5), "ACGTAC", Direction.REVERSE, 10)
                        .build();
        sut.initializeContig(_3xBiDirectionalContig, DefaultCoverageMap.buildCoverageMap(_3xBiDirectionalContig));
        
        PlacedRead readToTrim = _3xBiDirectionalContig.getPlacedReadById("read1");
        Range expectedTrimRange = Range.buildRange(0, 5);
        Range actualValidRange =sut.trimRead(readToTrim, readToTrim.getValidRange());
        assertEquals("new trimmed range",expectedTrimRange, actualValidRange);
        
        PlacedRead read2ToTrim = _3xBiDirectionalContig.getPlacedReadById("read2");
        Range expectedTrimRange2 = Range.buildRange(0, 5);
        Range actualValidRange2 =sut.trimRead(read2ToTrim, read2ToTrim.getValidRange());
        assertEquals("new trimmed range",expectedTrimRange2, actualValidRange2);
        
        
        PlacedRead readThatDoesntGetTrimmed = _3xBiDirectionalContig.getPlacedReadById("read3");
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));
    }
    
    @Test
    public void uniDirectionalOverMaxCoverageShouldIgnore(){
        Contig<PlacedRead> _2xBiDirectionalContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.buildRange(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read2", 0,  Range.buildRange(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read3", 0,  Range.buildRange(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read4", 0,  Range.buildRange(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read5", 0,  Range.buildRange(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .build();
        sut.initializeContig(_2xBiDirectionalContig, DefaultCoverageMap.buildCoverageMap(_2xBiDirectionalContig));
        for(PlacedRead readThatDoesntGetTrimmed : _2xBiDirectionalContig.getPlacedReads()){
            final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getValidRange();
            assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));
       
        }
       
    }
}
