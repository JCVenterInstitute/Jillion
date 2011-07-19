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

package org.jcvi.common.core.assembly.contig;

import org.easymock.EasyMockSupport;
import org.jcvi.Range;
import org.jcvi.common.core.assembly.contig.GapQualityValueStrategies;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.seq.Sequence;
import org.jcvi.common.core.seq.nuc.NucleotideSequence;
import org.jcvi.common.core.seq.qual.PhredQuality;
import org.jcvi.common.core.seq.read.SequenceDirection;
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
    private Sequence<PhredQuality> fullQualities;
    private NucleotideSequence encodedGlyphs;
    PhredQuality expectedQuality = PhredQuality.valueOf(42);
    @Before
    public void setup(){
        sut= getGapQualityValueStrategies();
        placedRead= createMock(PlacedRead.class);
        encodedGlyphs = createMock(NucleotideSequence.class);
        fullQualities = createMock(Sequence.class);
    }
    
    protected abstract GapQualityValueStrategies getGapQualityValueStrategies();
    
    @Test(expected = NullPointerException.class)
    public void nullQualitiesShouldThrowNPE(){
        sut.getQualityFor(placedRead, null, 2);
    }
    @Test(expected = NullPointerException.class)
    public void nullPlacedReadShouldThrowNPE(){
        sut.getQualityFor(null, fullQualities, 2);
    }
    @Test
    public void getUngappedQualityFromForwardRead(){
        int gappedReadIndex = 12;
        expect(placedRead.getEncodedGlyphs()).andReturn(encodedGlyphs).times(2);
        expect(placedRead.getSequenceDirection()).andReturn(SequenceDirection.FORWARD);
        expect(encodedGlyphs.isGap(gappedReadIndex)).andReturn(false);
        Range validRange = Range.buildRange(10,100);
        expect(placedRead.getValidRange()).andReturn(validRange);
        expect(fullQualities.getLength()).andReturn(validRange.getEnd()+validRange.getStart());
        int fullIndex = 22;
        expect(encodedGlyphs.convertGappedValidRangeIndexToUngappedValidRangeIndex(gappedReadIndex)).andReturn(gappedReadIndex);
        expect(fullQualities.get(fullIndex)).andReturn(expectedQuality);
        replayAll();
        assertEquals(expectedQuality,
                sut.getQualityFor(placedRead, fullQualities, gappedReadIndex));
        verifyAll();
    }
    @Test
    public void getUngappedQualityFromReverseRead(){
        int gappedReadIndex = 12;
        Range validRange = Range.buildRange(10,100);
        int fullLength=110;
        expect(placedRead.getEncodedGlyphs()).andReturn(encodedGlyphs).times(2);
        expect(placedRead.getSequenceDirection()).andReturn(SequenceDirection.REVERSE);
        expect(encodedGlyphs.isGap(gappedReadIndex)).andReturn(false);
        
        expect(placedRead.getValidRange()).andReturn(validRange).times(3);
        expect(fullQualities.getLength()).andReturn((long)fullLength);
        int fullIndex = 22;
        expect(encodedGlyphs.convertGappedValidRangeIndexToUngappedValidRangeIndex(gappedReadIndex)).andReturn(gappedReadIndex);
        expect(fullQualities.get(fullLength-fullIndex)).andReturn(expectedQuality);
        replayAll();
        assertEquals(expectedQuality,
                sut.getQualityFor(placedRead, fullQualities, gappedReadIndex));
        verifyAll();
    }
}
