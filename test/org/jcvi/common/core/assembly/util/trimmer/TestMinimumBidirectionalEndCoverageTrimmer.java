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
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageMapFactory;
import org.jcvi.common.core.assembly.util.trimmer.MinimumBidirectionalEndCoverageTrimmer;
import org.jcvi.common.core.assembly.util.trimmer.PlacedReadTrimmer;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestMinimumBidirectionalEndCoverageTrimmer {
    PlacedReadTrimmer<AssembledRead, Contig<AssembledRead>> sut;
    
    @Before
    public void setup(){
        sut = createSUT();
    }
    
    protected PlacedReadTrimmer<AssembledRead, Contig<AssembledRead>> createSUT(){
        return new MinimumBidirectionalEndCoverageTrimmer<AssembledRead, Contig<AssembledRead>>(3,4);
    }
    
    @Test
    public void allBelowMinCoverageReturnEmptyRange(){
        Contig<AssembledRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .build();
        sut.initializeContig(_1xContig, CoverageMapFactory.createGappedCoverageMapFromContig(_1xContig));
        AssembledRead read = _1xContig.getRead("read1");
        Range actualValidRange =sut.trimRead(read, read.getReadInfo().getValidRange());
        assertEquals(Range.createEmptyRange(), actualValidRange);
    }
    
    @Test
    public void leftEndBelowMinShouldTrimOff(){
        Contig<AssembledRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read2", 2, Range.create(0, 5), "GTACGT", Direction.REVERSE, 10)
                        .addRead("read3", 2, Range.create(0, 5), "GTACGT", Direction.REVERSE, 10)
                        .build();
        sut.initializeContig(_1xContig, CoverageMapFactory.createGappedCoverageMapFromContig(_1xContig));
        AssembledRead readToTrim = _1xContig.getRead("read1");
        AssembledRead readThatDoesntGetTrimmed = _1xContig.getRead("read2");
        Range expectedTrimRange = Range.create(2, 7);
        Range actualValidRange =sut.trimRead(readToTrim, readToTrim.getReadInfo().getValidRange());
        assertEquals("new trimmed range",expectedTrimRange, actualValidRange);
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getReadInfo().getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));
    }
    
    @Test
    public void biDirectionalMinCoverageShouldIgnore(){
        Contig<AssembledRead> _3xBiDirectionalContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read2", 0,  Range.create(0, 7), "ACGTACGT", Direction.REVERSE, 10)
                        .addRead("read3", 0,  Range.create(0, 7), "ACGTACGT", Direction.REVERSE, 10)
                        .build();
        sut.initializeContig(_3xBiDirectionalContig, CoverageMapFactory.createGappedCoverageMapFromContig(_3xBiDirectionalContig));
        
        CloseableIterator<AssembledRead> iter = null;
        try{
        	iter = _3xBiDirectionalContig.getReadIterator();
        	while(iter.hasNext()){
        		AssembledRead readThatDoesntGetTrimmed = iter.next();
        		final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getReadInfo().getValidRange();
                assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));
           
        	}
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
    }
    @Test
    public void uniDirectionalLeftShouldTrimLeftEnd(){
        Contig<AssembledRead> _3xBiDirectionalContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read2", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read3", 2,  Range.create(0, 5), "GTACGT", Direction.REVERSE, 10)
                        .build();
        sut.initializeContig(_3xBiDirectionalContig, CoverageMapFactory.createGappedCoverageMapFromContig(_3xBiDirectionalContig));
        
        AssembledRead readToTrim = _3xBiDirectionalContig.getRead("read1");
        Range expectedTrimRange = Range.create(2, 7);
        Range actualValidRange =sut.trimRead(readToTrim, readToTrim.getReadInfo().getValidRange());
        assertEquals("new trimmed range",expectedTrimRange, actualValidRange);
        
        AssembledRead read2ToTrim = _3xBiDirectionalContig.getRead("read2");
        Range expectedTrimRange2 = Range.create(2, 7);
        Range actualValidRange2 =sut.trimRead(read2ToTrim, read2ToTrim.getReadInfo().getValidRange());
        assertEquals("new trimmed range",expectedTrimRange2, actualValidRange2);
        
        
        AssembledRead readThatDoesntGetTrimmed = _3xBiDirectionalContig.getRead("read3");
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getReadInfo().getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));
    }
    
    @Test
    public void uniDirectionalRightShouldTrimLeftEnd(){
        Contig<AssembledRead> _3xBiDirectionalContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read2", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read3", 0,  Range.create(0, 5), "ACGTAC", Direction.REVERSE, 10)
                        .build();
        sut.initializeContig(_3xBiDirectionalContig, CoverageMapFactory.createGappedCoverageMapFromContig(_3xBiDirectionalContig));
        
        AssembledRead readToTrim = _3xBiDirectionalContig.getRead("read1");
        Range expectedTrimRange = Range.create(0, 5);
        Range actualValidRange =sut.trimRead(readToTrim, readToTrim.getReadInfo().getValidRange());
        assertEquals("new trimmed range",expectedTrimRange, actualValidRange);
        
        AssembledRead read2ToTrim = _3xBiDirectionalContig.getRead("read2");
        Range expectedTrimRange2 = Range.create(0, 5);
        Range actualValidRange2 =sut.trimRead(read2ToTrim, read2ToTrim.getReadInfo().getValidRange());
        assertEquals("new trimmed range",expectedTrimRange2, actualValidRange2);
        
        
        AssembledRead readThatDoesntGetTrimmed = _3xBiDirectionalContig.getRead("read3");
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getReadInfo().getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));
    }
    
    @Test
    public void uniDirectionalOverMaxCoverageShouldIgnore(){
        Contig<AssembledRead> _2xBiDirectionalContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read2", 0,  Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read3", 0,  Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read4", 0,  Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read5", 0,  Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .build();
        sut.initializeContig(_2xBiDirectionalContig, CoverageMapFactory.createGappedCoverageMapFromContig(_2xBiDirectionalContig));
        CloseableIterator<AssembledRead> iter =null;
        try{
        	iter = _2xBiDirectionalContig.getReadIterator();
        	while(iter.hasNext()){
        		AssembledRead readThatDoesntGetTrimmed = iter.next();
        		  final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getReadInfo().getValidRange();
                  assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));
             
        	}
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
       
    }
}
