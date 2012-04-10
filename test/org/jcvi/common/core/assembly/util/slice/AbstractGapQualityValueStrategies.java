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

package org.jcvi.common.core.assembly.util.slice;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMockSupport;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.util.slice.GapQualityValueStrategies;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.util.Builder;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractGapQualityValueStrategies extends EasyMockSupport{

    GapQualityValueStrategies sut;
    private PlacedRead placedRead;
    private NucleotideSequence sequence;
    PhredQuality expectedQuality = PhredQuality.valueOf(42);
    @Before
    public void setup(){
        sut= getGapQualityValueStrategies();
        placedRead= createMock(PlacedRead.class);
        sequence = createMock(NucleotideSequence.class);
    }
    
    protected abstract GapQualityValueStrategies getGapQualityValueStrategies();
    
    @Test(expected = NullPointerException.class)
    public void nullQualitiesShouldThrowNPE(){
        sut.getQualityFor(placedRead, null, 2);
    }
    @Test(expected = NullPointerException.class)
    public void nullPlacedReadShouldThrowNPE(){
        sut.getQualityFor(null, createMock(QualitySequence.class), 2);
    }
    @Test
    public void getUngappedQualityFromForwardRead(){
        int gappedReadIndex = 12;
        int fullIndex = 22;
        expect(placedRead.getNucleotideSequence()).andReturn(sequence).anyTimes();
        expect(sequence.isGap(gappedReadIndex)).andReturn(false);
        expect(placedRead.getDirection()).andStubReturn(Direction.FORWARD);
        Range validRange = Range.create(10,100);
        expect(placedRead.getValidRange()).andReturn(validRange);
        expect(sequence.getUngappedOffsetFor(gappedReadIndex)).andReturn(gappedReadIndex);
        long fullLength = validRange.getEnd()+validRange.getBegin();
        QualitySequence qualities =new MockQualitySequenceBuilder(fullLength)
                                .setQuality(fullIndex, expectedQuality)
                                .build();
        
        replayAll();
        assertEquals(expectedQuality,
                sut.getQualityFor(placedRead, qualities, gappedReadIndex));
        verifyAll();
    }
    @Test
    public void getUngappedQualityFromReverseRead(){
        int gappedReadIndex = 12;
        int ungappedReadOffset = gappedReadIndex-2;
        Range validRange = Range.create(10,100);
        long fullLength=110;
        expect(placedRead.getNucleotideSequence()).andReturn(sequence).anyTimes();
        expect(placedRead.getDirection()).andStubReturn(Direction.REVERSE);
        expect(sequence.isGap(gappedReadIndex)).andReturn(false);
        expect(sequence.getUngappedOffsetFor(gappedReadIndex)).andReturn(ungappedReadOffset);
        
        expect(placedRead.getValidRange()).andReturn(validRange).anyTimes();
        
        QualitySequence qualities =new MockQualitySequenceBuilder(fullLength)
                                .setQuality(fullLength-1-((fullLength-1-validRange.getEnd()) +ungappedReadOffset), expectedQuality)
                                .build();
        replayAll();
        assertEquals(expectedQuality,
                sut.getQualityFor(placedRead, qualities, gappedReadIndex));
        verifyAll();
    }
    
    private class MockQualitySequenceBuilder implements Builder<QualitySequence>{
        private QualitySequence fullQualities = createMock(QualitySequence.class);
        private PhredQuality[] quals;
        
        public MockQualitySequenceBuilder(long fullLength){
            quals = new PhredQuality[(int)fullLength];
            expect(fullQualities.getLength()).andStubReturn(fullLength);
        }
        
        
        public MockQualitySequenceBuilder setQuality(long offset, PhredQuality qual){
            quals[(int)offset]= qual;
            return this;
        }
        /**
        * {@inheritDoc}
        */
        @Override
        public QualitySequence build() {
            List<PhredQuality> list = Arrays.asList(quals);
            expect(fullQualities.asList()).andReturn(list);
            return fullQualities;
        }
        
    }
    
}
