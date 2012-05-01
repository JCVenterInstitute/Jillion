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

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.DefaultContig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;
import org.jcvi.common.core.assembly.util.trimmer.MinimumEndCoverageTrimmer;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestMinimumEndCoverageTrimmer {

    MinimumEndCoverageTrimmer<AssembledRead, Contig<AssembledRead>> sut;
    
    @Before
    public void setup(){
        sut = new MinimumEndCoverageTrimmer<AssembledRead, Contig<AssembledRead>>(2);
    }
    
    @Test
    public void allBelowMinCoverageReturnEmptyRange(){
        Contig<AssembledRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .build();
        sut.initializeContig(_1xContig, DefaultCoverageMap.buildCoverageMap(_1xContig));
        AssembledRead read = _1xContig.getRead("read1");
        Range actualValidRange =sut.trimRead(read, read.getValidRange());
        assertEquals(Range.createEmptyRange(), actualValidRange);
    }
    @Test
    public void leftEndBelowMinShouldTrimOff(){
        Contig<AssembledRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read2", 2, Range.create(0, 5), "GTACGT", Direction.FORWARD, 10)
                        .build();
        sut.initializeContig(_1xContig, DefaultCoverageMap.buildCoverageMap(_1xContig));
        AssembledRead readToTrim = _1xContig.getRead("read1");
        AssembledRead readThatDoesntGetTrimmed = _1xContig.getRead("read2");
        Range expectedTrimRange = Range.create(2, 7);
        Range actualValidRange =sut.trimRead(readToTrim, readToTrim.getValidRange());
        assertEquals("new trimmed range",expectedTrimRange, actualValidRange);
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));
    }
    
    @Test
    public void rightEndBelowMinShouldTrimOff(){
        Contig<AssembledRead> _1xContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read2", 0, Range.create(0, 5), "ACGTAC", Direction.FORWARD, 10)
                        .build();
        sut.initializeContig(_1xContig, DefaultCoverageMap.buildCoverageMap(_1xContig));
        AssembledRead readToTrim = _1xContig.getRead("read1");
        AssembledRead readThatDoesntGetTrimmed = _1xContig.getRead("read2");
        Range expectedTrimRange = Range.create(0, 5);
        Range actualValidRange =sut.trimRead(readToTrim, readToTrim.getValidRange());
        assertEquals("new trimmed range",expectedTrimRange, actualValidRange);
        final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getValidRange();
        assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));
    }
    
    @Test
    public void noneBelowMinShouldIgnore(){
        Contig<AssembledRead> _2xContig = new DefaultContig.Builder("id","ACGTACGT")
                        .addRead("read1", 0, Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .addRead("read2", 0,  Range.create(0, 7), "ACGTACGT", Direction.FORWARD, 10)
                        .build();
        sut.initializeContig(_2xContig, DefaultCoverageMap.buildCoverageMap(_2xContig));

        CloseableIterator<AssembledRead> iter = null;
        try{
        	iter = _2xContig.getReadIterator();
        	while(iter.hasNext()){
        		AssembledRead readThatDoesntGetTrimmed = iter.next();
        		final Range readThatDoesntGetTrimmedValidRange = readThatDoesntGetTrimmed.getValidRange();
                assertEquals("should not trim",readThatDoesntGetTrimmedValidRange, sut.trimRead(readThatDoesntGetTrimmed, readThatDoesntGetTrimmedValidRange));
           
        	}
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
    }
}
