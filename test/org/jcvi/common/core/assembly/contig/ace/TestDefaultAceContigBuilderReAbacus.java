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

package org.jcvi.common.core.assembly.contig.ace;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultAceContigBuilderReAbacus {
    PhdInfo read1PhdInfo = createMock(PhdInfo.class);
    @Test
    public void abacus(){
        AceContigBuilder sut =  DefaultAceContig.createBuilder("id",
                          "ACGT-----ACGT")
        
        .addRead("read1",   "GT-T---ACG", 2, Direction.FORWARD, Range.buildRange(2,7), read1PhdInfo, 10)
        .addRead("read2", "ACGT--T--AC", 0, Direction.FORWARD, Range.buildRange(2,8), read1PhdInfo, 10)
        .addRead("read3",    "T---T-ACGT", 3, Direction.FORWARD, Range.buildRange(2,8), read1PhdInfo, 10);
        
        sut.getPlacedReadBuilder("read1").reAbacus(Range.buildRange(2,6), "T");
        sut.getPlacedReadBuilder("read2").reAbacus(Range.buildRange(4,8), "T");
        sut.getPlacedReadBuilder("read3").reAbacus(Range.buildRange(1,5), "T");
        sut.getConsensusBuilder().delete(Range.buildRange(4,8)).insert(4, "T");
           
        AceContig contig =sut.build();
        assertEquals("ACGTTACGT", contig.getConsensus().toString());
        AcePlacedRead read1 = contig.getPlacedReadById("read1");
        assertEquals("GTTACG", read1.getNucleotideSequence().toString());
        assertEquals(7, read1.getEnd());
        
        AcePlacedRead read2 = contig.getPlacedReadById("read2");
        assertEquals("ACGTTAC", read2.getNucleotideSequence().toString());
        assertEquals(6, read2.getEnd());
        
        AcePlacedRead read3 = contig.getPlacedReadById("read3");
        assertEquals("TTACGT", read3.getNucleotideSequence().toString());
        assertEquals(8, read3.getEnd());
    }
    
    @Test
    public void abacusAndShiftDownstreamReads(){
        AceContigBuilder sut =  DefaultAceContig.createBuilder("id",
                          "ACGT-----ACGT")
        
        .addRead("read1",   "GT-T---ACG", 2, Direction.FORWARD, Range.buildRange(2,7), read1PhdInfo, 10)
        .addRead("read2", "ACGT--T--AC", 0, Direction.FORWARD, Range.buildRange(2,8), read1PhdInfo, 10)
        .addRead("read3",    "T---T-ACGT", 3, Direction.FORWARD, Range.buildRange(2,8), read1PhdInfo, 10)
        .addRead("read4",           "ACGT", 9, Direction.FORWARD, Range.buildRange(2,4), read1PhdInfo, 10);
        
        sut.getPlacedReadBuilder("read1").reAbacus(Range.buildRange(2,6), "T");
        sut.getPlacedReadBuilder("read2").reAbacus(Range.buildRange(4,8), "T");
        sut.getPlacedReadBuilder("read3").reAbacus(Range.buildRange(1,5), "T");
        sut.getConsensusBuilder().delete(Range.buildRange(4,8)).insert(4, "T");
        sut.getPlacedReadBuilder("read4").shiftLeft(4);
           
        AceContig contig =sut.build();
        assertEquals("ACGTTACGT", contig.getConsensus().toString());
        AcePlacedRead read1 = contig.getPlacedReadById("read1");
        assertEquals("GTTACG", read1.getNucleotideSequence().toString());
        assertEquals(7, read1.getEnd());
        
        AcePlacedRead read2 = contig.getPlacedReadById("read2");
        assertEquals("ACGTTAC", read2.getNucleotideSequence().toString());
        assertEquals(6, read2.getEnd());
        
        AcePlacedRead read3 = contig.getPlacedReadById("read3");
        assertEquals("TTACGT", read3.getNucleotideSequence().toString());
        assertEquals(8, read3.getEnd());
        
        AcePlacedRead read4 = contig.getPlacedReadById("read4");
        assertEquals("ACGT", read4.getNucleotideSequence().toString());
        assertEquals(5, read4.getStart());
        assertEquals(8, read4.getEnd());
    }
}
