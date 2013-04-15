/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util.slice;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMockSupport;
import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.ReadInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
import org.junit.Before;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractGapQualityValueStrategies extends EasyMockSupport{

    GapQualityValueStrategy sut;
    private AssembledRead placedRead;
    private ReferenceMappedNucleotideSequence sequence;
    PhredQuality expectedQuality = PhredQuality.valueOf(42);
    @Before
    public void setup(){
        sut= getGapQualityValueStrategies();
        placedRead= createMock(AssembledRead.class);
        sequence = createMock(ReferenceMappedNucleotideSequence.class);
    }
    
    protected abstract GapQualityValueStrategy getGapQualityValueStrategies();
    
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
        expect(sequence.getLength()).andReturn(validRange.getLength());
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
        expect(sequence.getLength()).andReturn(validRange.getLength());
        expect(placedRead.getReadInfo()).andStubReturn(readInfo);
        
        QualitySequence qualities =new QualitySequenceBuilder(new byte[fullLength])
        						.reverse()
        						.replace((int)(ungappedReadOffset + validRange.getBegin()), expectedQuality)
        						.reverse()
        						.build();
                                
        replayAll();
        assertEquals(expectedQuality,
                sut.getQualityFor(placedRead, qualities, gappedReadIndex));
        verifyAll();
    }
    
    @Test
    public void testGappedValidRangeQualitySequence(){
    	QualitySequenceBuilder fullLengthQualities = new QualitySequenceBuilder();
    	for(int i =0; i<15; i++){
    		fullLengthQualities.append(i+1);
    	}
    	NucleotideSequence seq = new NucleotideSequenceBuilder("AA-A-AAAA--AA-A")
    									.build();
    	
    	ReferenceMappedNucleotideSequence readSeq = new NucleotideSequenceBuilder(seq)
    													.setReferenceHint(seq, 0)
    													.buildReferenceEncodedNucleotideSequence();
    	
    	expect(placedRead.getNucleotideSequence()).andStubReturn(readSeq);
    	expect(placedRead.getReadInfo()).andStubReturn(new ReadInfo(Range.of(2,11), 15));
    	expect(placedRead.getDirection()).andStubReturn(Direction.FORWARD);
    	expect(placedRead.getGappedLength()).andStubReturn(seq.getLength());
    	replayAll();
    	QualitySequence fullLengthUngappedQualities = fullLengthQualities.build();
    	
    	QualitySequence fullLengthGappedQualities = sut.getGappedValidRangeQualitySequenceFor(placedRead, fullLengthUngappedQualities);
    
    	for(int i=0; i<seq.getLength(); i++){
    		assertEquals(""+i, fullLengthGappedQualities.get(i), 
    				sut.getQualityFor(placedRead, fullLengthUngappedQualities, i));
    	}
    }
    
    @Test
    public void testGappedValidRangeQualityReverseSequence(){
    	QualitySequenceBuilder fullLengthQualities = new QualitySequenceBuilder();
    	for(int i =0; i<15; i++){
    		fullLengthQualities.append(i+1);
    	}
    	NucleotideSequence seq = new NucleotideSequenceBuilder("AA-A-AAAA--AA-A")
    									.build();
    	
    	ReferenceMappedNucleotideSequence readSeq = new NucleotideSequenceBuilder(seq)
    													.setReferenceHint(seq, 0)
    													.buildReferenceEncodedNucleotideSequence();
    	
    	expect(placedRead.getNucleotideSequence()).andStubReturn(readSeq);
    	expect(placedRead.getReadInfo()).andStubReturn(new ReadInfo(Range.of(2,11), 15));
    	expect(placedRead.getDirection()).andStubReturn(Direction.REVERSE);
    	expect(placedRead.getGappedLength()).andStubReturn(seq.getLength());
    	replayAll();
    	QualitySequence fullLengthUngappedQualities = fullLengthQualities.build();
    	
    	QualitySequence fullLengthGappedQualities = sut.getGappedValidRangeQualitySequenceFor(placedRead, fullLengthUngappedQualities);
    
    	
    	for(int i=0; i<seq.getLength(); i++){
    		assertEquals(""+i, fullLengthGappedQualities.get(i), 
    				sut.getQualityFor(placedRead, fullLengthUngappedQualities, i));
    	}
    }
    
}
