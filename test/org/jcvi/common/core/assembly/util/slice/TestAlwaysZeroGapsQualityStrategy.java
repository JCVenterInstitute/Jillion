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

import static org.junit.Assert.assertEquals;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.DefaultContig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.util.slice.GapQualityValueStrategies;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
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
    protected GapQualityValueStrategies getGapQualityValueStrategies() {
        return GapQualityValueStrategies.ALWAYS_ZERO;
    }
    
    @Test
    public void readEndsWithGapShouldReturnQualityValue0(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
        .addRead("readId", 0, "ACGT-")
        .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{11,12,13,14}).build();
        
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 4));
    }
    
    @Test
    public void readStartsEndsWithGapShouldReturnQualityValue0(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
        .addRead("readId", 0, "-ACGT")
        .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities = new QualitySequenceBuilder(new byte[]{11,12,13,14}).build();
        
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 0));
    }
    
    @Test
    public void oneGapShouldReturnQualityValue0(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT")
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{11,12,13,14,15,16,17}).build();
        
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 4));
    }
    @Test
    public void oneGapShouldReverseReturnQualityValue0(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT", Direction.REVERSE)
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{11,12,13,14,15,16,17}).build();
        
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 4));
    }
    
    @Test
    public void multiGapShouldReturnQualityValue0(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGT-ACGT")
                                    .addRead("readId", 0, "ACGT--CGT")
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{11,12,13,14,15,16}).build();
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 4));
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 5));
    }
    @Test
    public void multiGapGapShouldReverseReturnQualityValue0(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGT-ACGT")
                                    .addRead("readId", 0, "ACGT--CGT", Direction.REVERSE)
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{11,12,13,14,15,16,17,18}).build();
        
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 4));
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 5));
    }

}
