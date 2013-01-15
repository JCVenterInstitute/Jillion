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

package org.jcvi.jillion.assembly.util.slice;

import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMockSupport;
import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.assembly.util.slice.GapQualityValueStrategies;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
import org.jcvi.jillion.core.util.Builder;
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
    private AssembledRead placedRead;
    private ReferenceMappedNucleotideSequence sequence;
    PhredQuality expectedQuality = PhredQuality.valueOf(42);
    @Before
    public void setup(){
        sut= getGapQualityValueStrategies();
        placedRead= createMock(AssembledRead.class);
        sequence = createMock(ReferenceMappedNucleotideSequence.class);
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
        Range validRange = Range.of(10,100);
        int fullLength = (int)(validRange.getEnd()+validRange.getBegin());
        ReadInfo readInfo = new ReadInfo(validRange, fullLength);
        expect(placedRead.getReadInfo()).andStubReturn(readInfo);
        expect(sequence.getUngappedOffsetFor(gappedReadIndex)).andReturn(gappedReadIndex);
        
        QualitySequence qualities =new QualitySequenceBuilder(new byte[fullLength])
                                .replace(fullIndex, expectedQuality)
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
        Range validRange = Range.of(10,100);
        int fullLength=110;
        expect(placedRead.getNucleotideSequence()).andReturn(sequence).anyTimes();
        expect(placedRead.getDirection()).andStubReturn(Direction.REVERSE);
        expect(sequence.isGap(gappedReadIndex)).andReturn(false);
        expect(sequence.getUngappedOffsetFor(gappedReadIndex)).andReturn(ungappedReadOffset);
        ReadInfo readInfo = new ReadInfo(validRange, fullLength);
        expect(placedRead.getReadInfo()).andStubReturn(readInfo);
        
        QualitySequence qualities =new QualitySequenceBuilder(new byte[fullLength])
        						.replace((int)(fullLength-1-validRange.getEnd() +ungappedReadOffset), expectedQuality)
        						.reverse()
        						.build();
                                
        replayAll();
        assertEquals(expectedQuality,
                sut.getQualityFor(placedRead, qualities, gappedReadIndex));
        verifyAll();
    }
    
    
    
}
