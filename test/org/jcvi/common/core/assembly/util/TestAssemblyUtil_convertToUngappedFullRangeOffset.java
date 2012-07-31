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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMockSupport;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.ReadInfo;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.ReferenceMappedNucleotideSequence;
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
        AssembledRead mockRead = new MockPlacedReadBuilder("ACGTACGT",8)
                                .validRange(Range.createOfLength(8))
                                .build();
        int offset = 4;
        replayAll();
        assertEquals(offset, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,offset));
    }
    
    @Test
    public void reverseSequenceNoGapsValidLengthIsEntireSequenceShouldReturnSameOffset(){
        AssembledRead mockRead = new MockPlacedReadBuilder("ACGTACGT",8)
                                .validRange(Range.createOfLength(8))
                                .direction(Direction.REVERSE)
                                .build();
        int offset = 4;
        replayAll();
        assertEquals(offset, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,offset));
    }
    
    @Test
    public void forwardSequenceNoGapsValidRangeIsSubsetOfFullLength(){
        AssembledRead mockRead = new MockPlacedReadBuilder("GTACG",8)
                                .validRange(validRange)
                                .build();
        replayAll();
        assertEquals(4+2, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,4));
    }
    
    @Test
    public void reverseSequenceNoGapsValidRangeIsSubsetOfFullLength(){
        AssembledRead mockRead = new MockPlacedReadBuilder("ACGTT",8)
                                .validRange(validRange)
                                .direction(Direction.REVERSE)
                                .build();
        replayAll();
        assertEquals(3, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,2));
    }
    
    @Test
    public void forwardSequenceOneGapAfterDesiredOffsetShouldReturnSameOffset(){
        
        AssembledRead mockRead = new MockPlacedReadBuilder("ACGT-G",8)
                                .validRange(validRange)
                                .build();
        replayAll();
        assertEquals(3+2, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,3));
    }
    
    @Test
    public void forwardSequenceOneGapBeforeDesiredOffsetShouldReturnOffsetMinusNumGaps(){
        AssembledRead mockRead = new MockPlacedReadBuilder("ACG-TC",8)
                                .validRange(validRange)
                                .build();
        replayAll();
        assertEquals(3+2-1, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,3));
    }
    @Test
    public void forwardSequencetwoGapBeforeDesiredOffsetShouldReturnOffsetMinusNumGaps(){
        AssembledRead mockRead = new MockPlacedReadBuilder("ACG--TC",8)
                                .validRange(validRange)
                                .build();
        replayAll();
        assertEquals(4+2-2, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,4));
    }
    
    @Test
    public void reverseSequenceOneGapBeforeDesiredOffsetShouldReturnOffsetMinusNumGaps(){
        AssembledRead mockRead = new MockPlacedReadBuilder("CGTA-C",8)
                                .validRange(validRange)
                                .direction(Direction.REVERSE)
                                .build();
        replayAll();
        assertEquals(3, AssemblyUtil.convertToUngappedFullRangeOffset(mockRead,2));
    }
    
    private static class NucleotideSequenceReferenceEncodedAdapter implements ReferenceMappedNucleotideSequence{
    	private final NucleotideSequence delegate;
    	
		public NucleotideSequenceReferenceEncodedAdapter(NucleotideSequence delegate) {
			this.delegate = delegate;
		}

		@Override
		public List<Integer> getGapOffsets() {
			return delegate.getGapOffsets();
		}

		@Override
		public int getNumberOfGaps() {
			return delegate.getNumberOfGaps();
		}

		@Override
		public boolean isGap(int gappedOffset) {
			return delegate.isGap(gappedOffset);
		}

		@Override
		public long getUngappedLength() {
			return delegate.getUngappedLength();
		}

		

		@Override
		public int getNumberOfGapsUntil(int gappedOffset) {
			return delegate.getNumberOfGapsUntil(gappedOffset);
		}

		@Override
		public int getUngappedOffsetFor(int gappedOffset) {
			return delegate.getUngappedOffsetFor(gappedOffset);
		}

		@Override
		public int getGappedOffsetFor(int ungappedOffset) {
			return delegate.getGappedOffsetFor(ungappedOffset);
		}

		@Override
		public List<Nucleotide> asList() {
			return delegate.asList();
		}

		@Override
		public Nucleotide get(int index) {
			return delegate.get(index);
		}

		@Override
		public long getLength() {
			return delegate.getLength();
		}


		@Override
		public Iterator<Nucleotide> iterator() {
			return delegate.iterator();
		}


		@Override
		public Iterator<Nucleotide> iterator(Range range) {
			return delegate.iterator(range);
		}

		@Override
		public Map<Integer, Nucleotide> getDifferenceMap() {
			throw new UnsupportedOperationException("invalid for adapted sequence");
		}

		@Override
		public NucleotideSequence getReferenceSequence() {
			throw new UnsupportedOperationException("invalid for adapted sequence");
			
		}
    	
    }
    private class MockPlacedReadBuilder implements Builder<AssembledRead>{
        private final AssembledRead mock = createMock(AssembledRead.class);
        private Direction dir = Direction.FORWARD;
        private final ReferenceMappedNucleotideSequence seq;
        private Range validRange;
        private final int ungappedLength;
        private final int fullLength;
        public MockPlacedReadBuilder(String validRangeSeq, int fullLength){
        	this.ungappedLength = validRangeSeq.replaceAll("-", "").length();
           
            seq = new NucleotideSequenceReferenceEncodedAdapter(
            		new NucleotideSequenceBuilder(validRangeSeq).build());
            		
            assertTrue(fullLength >= ungappedLength);
            this.fullLength = fullLength;
            expect(mock.getNucleotideSequence()).andStubReturn(seq);
        }
        
        MockPlacedReadBuilder direction(Direction dir){
            this.dir = dir;
            return this;
        }
        MockPlacedReadBuilder validRange(Range r){
            this.validRange = r;
            return this;
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        public AssembledRead build() {
            assertEquals("ungapped valid sequence is wrong length",validRange.getLength(),
            		seq.getUngappedLength());
            ReadInfo readInfo = new ReadInfo(validRange, fullLength);
            expect(mock.getReadInfo()).andStubReturn(readInfo);
            expect(mock.getDirection()).andStubReturn(dir);
            return mock;
        }
        
    }
}
