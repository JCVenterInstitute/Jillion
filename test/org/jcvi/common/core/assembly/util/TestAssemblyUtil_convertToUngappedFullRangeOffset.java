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

package org.jcvi.common.core.assembly.util;

import org.easymock.EasyMockSupport;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.Builder;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestAssemblyUtil_convertToUngappedFullRangeOffset extends EasyMockSupport{

    Range validRange = Range.create(2,6);
    @Test
    public void forwardSequenceNoGapsValidLengthIsEntireSequenceShouldReturnSameOffset(){
        PlacedRead mockRead = new MockPlacedReadBuilder("ACGTACGT",8)
                                .validRange(Range.createOfLength(8))
                                .build();
        int offset = 4;
        replayAll();
        assertEquals(offset, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,offset));
    }
    
    @Test
    public void reverseSequenceNoGapsValidLengthIsEntireSequenceShouldReturnSameOffset(){
        PlacedRead mockRead = new MockPlacedReadBuilder("ACGTACGT",8)
                                .validRange(Range.createOfLength(8))
                                .direction(Direction.REVERSE)
                                .build();
        int offset = 4;
        replayAll();
        assertEquals(offset, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,offset));
    }
    
    @Test
    public void forwardSequenceNoGapsValidRangeIsSubsetOfFullLength(){
        PlacedRead mockRead = new MockPlacedReadBuilder("GTACG",8)
                                .validRange(validRange)
                                .build();
        replayAll();
        assertEquals(4+2, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,4));
    }
    
    @Test
    public void reverseSequenceNoGapsValidRangeIsSubsetOfFullLength(){
        PlacedRead mockRead = new MockPlacedReadBuilder("ACGTT",8)
                                .validRange(validRange)
                                .direction(Direction.REVERSE)
                                .build();
        replayAll();
        assertEquals(3, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,2));
    }
    
    @Test
    public void forwardSequenceOneGapAfterDesiredOffsetShouldReturnSameOffset(){
        
        PlacedRead mockRead = new MockPlacedReadBuilder("ACGT-G",8)
                                .validRange(validRange)
                                .build();
        replayAll();
        assertEquals(3+2, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,3));
    }
    
    @Test
    public void forwardSequenceOneGapBeforeDesiredOffsetShouldReturnOffsetMinusNumGaps(){
        PlacedRead mockRead = new MockPlacedReadBuilder("ACG-TC",8)
                                .validRange(validRange)
                                .build();
        replayAll();
        assertEquals(3+2-1, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,3));
    }
    @Test
    public void forwardSequencetwoGapBeforeDesiredOffsetShouldReturnOffsetMinusNumGaps(){
        PlacedRead mockRead = new MockPlacedReadBuilder("ACG--TC",8)
                                .validRange(validRange)
                                .build();
        replayAll();
        assertEquals(4+2-2, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,4));
    }
    
    @Test
    public void reverseSequenceOneGapBeforeDesiredOffsetShouldReturnOffsetMinusNumGaps(){
        PlacedRead mockRead = new MockPlacedReadBuilder("CGTA-C",8)
                                .validRange(validRange)
                                .direction(Direction.REVERSE)
                                .build();
        replayAll();
        assertEquals(3, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,2));
    }
    private class MockPlacedReadBuilder implements Builder<PlacedRead>{
        private final PlacedRead mock = createMock(PlacedRead.class);
        private Direction dir = Direction.FORWARD;
        private final NucleotideSequence seq;
        private Range validRange;
        public MockPlacedReadBuilder(String validRangeSeq, int fullLength){
            
            seq = new NucleotideSequenceBuilder(validRangeSeq).build();
            assertTrue(fullLength >= seq.getUngappedLength());
            expect(mock.getUngappedFullLength()).andStubReturn(fullLength);
            expect(mock.getNucleotideSequence()).andStubReturn(seq);
        }
        
        MockPlacedReadBuilder direction(Direction dir){
            this.dir = dir;
            return this;
        }
        MockPlacedReadBuilder validRange(Range r){
            this.validRange = r;
            expect(mock.getValidRange()).andStubReturn(validRange);
            return this;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public PlacedRead build() {
            assertEquals("ungapped valid sequence is wrong length",validRange.getLength(),seq.getUngappedLength());
           
            expect(mock.getDirection()).andStubReturn(dir);
            return mock;
        }
        
    }
}
