/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.internal.assembly.DefaultContig;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestAlwaysZeroGapsQualityStrategy extends AbstractGapQualityValueStrategies{

    private static final PhredQuality ZERO = PhredQuality.valueOf(0);
    /**
    * {@inheritDoc}
    */
    @Override
    protected GapQualityValueStrategy getGapQualityValueStrategies() {
        return GapQualityValueStrategy.ALWAYS_ZERO;
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void fullLengthReadEndsWithGapShouldThrowException(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
        .addRead("readId", 0, "ACGT-")
        .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{11,12,13,14}).build();
        
        sut.getGappedValidRangeQualitySequenceFor(read, qualities);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void fullLengthReadStartsWithGapShouldThrowException(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
        .addRead("readId", 0, "-ACGT")
        .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities = new QualitySequenceBuilder(new byte[]{11,12,13,14}).build();
        
        sut.getGappedValidRangeQualitySequenceFor(read, qualities);
    }
    
    @Test
    public void readEndsWithGapButHasOtherBasesInTrimmedOffPortionShouldReturn0(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
        .addRead("readId", 0, Range.of(0,3), "ACGT-", Direction.FORWARD, 6)
        .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{11,12,13,14,5,6}).build();
        
        assertEquals(ZERO, sut.getGappedValidRangeQualitySequenceFor(read, qualities).get(4));
    }
    
    @Test
    public void fullLengthReadStartsEndsWithGapButHasOtherBasesInTrimmedOffPortionShouldReturn0(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
        .addRead("readId", 0, Range.of(2,5), "-ACGT", Direction.FORWARD, 6)
        .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities = new QualitySequenceBuilder(new byte[]{5,6,11,12,13,14,}).build();
        
        assertEquals(ZERO, sut.getGappedValidRangeQualitySequenceFor(read, qualities).get(0));
    }
    
    @Test
    public void oneGapShouldReturnQualityValue0(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT")
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{11,12,13,14,15,16,17}).build();
        
        assertEquals(ZERO,sut.getGappedValidRangeQualitySequenceFor(read, qualities).get(4));
    }
    @Test
    public void oneGapShouldReverseReturnQualityValue0(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT", Direction.REVERSE)
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{11,12,13,14,15,16,17}).build();
        
        assertEquals(ZERO,sut.getGappedValidRangeQualitySequenceFor(read, qualities).get(4));
    }
    
    @Test
    public void multiGapShouldReturnQualityValue0(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGT-ACGT")
                                    .addRead("readId", 0, "ACGT--CGT")
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{11,12,13,14,15,16,17}).build();
        QualitySequence gappedValidRangeQualitySequence = sut.getGappedValidRangeQualitySequenceFor(read, qualities);
		assertEquals(ZERO,gappedValidRangeQualitySequence.get(4));
        assertEquals(ZERO,gappedValidRangeQualitySequence.get(5));
    }
    @Test
    public void multiGapGapShouldReverseReturnQualityValue0(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGT-ACGT")
                                    .addRead("readId", 0, "ACGT--CGT", Direction.REVERSE)
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{11,12,13,14,15,16,17,18}).build();
        
        QualitySequence gappedValidRangeQualitySequence = sut.getGappedValidRangeQualitySequenceFor(read, qualities);
		assertEquals(ZERO,gappedValidRangeQualitySequence.get(4));
        assertEquals(ZERO,gappedValidRangeQualitySequence.get(5));
    }

}
