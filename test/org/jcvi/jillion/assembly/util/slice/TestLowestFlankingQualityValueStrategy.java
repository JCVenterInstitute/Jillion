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
/*
 * Created on Feb 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.slice;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.DefaultContig;
import org.jcvi.jillion.assembly.util.slice.GapQualityValueStrategies;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestLowestFlankingQualityValueStrategy extends AbstractGapQualityValueStrategies{


    GapQualityValueStrategies sut = GapQualityValueStrategies.LOWEST_FLANKING;

    @Test
    public void readEndsWithGapShouldReturnQualityValue1(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
        .addRead("readId", 0, "ACGT-")
        .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{11,12,13,14}).build();
        
        assertEquals(PhredQuality.valueOf(1),sut.getQualityFor(read, qualities, 4));
    }
    
    @Test
    public void readStartsEndsWithGapShouldReturnQualityValue1(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
        .addRead("readId", 0, "-ACGT")
        .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{11,12,13,14}).build();
        
        assertEquals(PhredQuality.valueOf(1),sut.getQualityFor(read, qualities, 0));
    }
   
    /**
    * {@inheritDoc}
    */
    @Override
    protected GapQualityValueStrategies getGapQualityValueStrategies() {
        return GapQualityValueStrategies.LOWEST_FLANKING;
    }
    
    @Test
    public void sameQualitiesFlankingOneGapShouldReturnFlankingQuality(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT")
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{0,0,0,16,16,0,0,0}).build();
        
        assertEquals(qualities.get(3),sut.getQualityFor(read, qualities, 4));
    }
    
    @Test
    public void leftFlankingGapIsLowerShouldReturnLeftFlankingQuality(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT")
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{0,0,0,14,16,0,0,0}).build();
        
        assertEquals(qualities.get(3),sut.getQualityFor(read, qualities, 4));
    }
    @Test
    public void reverseLeftFlankingGapIsLowerShouldReturnLeftFlankingQuality(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT",Direction.REVERSE)
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{0,0,0,0,14,16,0,0}).build();
        
        assertEquals(qualities.get(2),sut.getQualityFor(read, qualities, 4));
    }
    @Test
    public void rightFlankingGapIsLowerShouldReturnRightFlankingQuality(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT")
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{0,0,0,14,11,0,0,0}).build();
        
        assertEquals(qualities.get(4),sut.getQualityFor(read, qualities, 4));
    }
    @Test
    public void rightReverseFlankingGapIsLowerShouldReturnRightFlankingQuality(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT",Direction.REVERSE)
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{0,0,14,11,0,0,0}).build();
        
        assertEquals(qualities.get(3),sut.getQualityFor(read, qualities, 4));
    }
    @Test
    public void multiGapRightFlankingGapIsLowerShouldReturnRightFlankingQuality(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGT-ACGT")
                                    .addRead("readId", 0, "ACGT--CGT")
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{0,0,0,14,11,0,0,0}).build();
        
        assertEquals(qualities.get(4),sut.getQualityFor(read, qualities, 4));
        assertEquals(qualities.get(4),sut.getQualityFor(read, qualities, 5));
    }
    @Test
    public void multiGapLeftFlankingGapIsLowerShouldReturnLeftFlankingQuality(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGT-ACGT")
                                    .addRead("readId", 0, "ACGT--CGT")
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{0,0,0,14,16,0,0,0}).build();
        
        assertEquals(qualities.get(3),sut.getQualityFor(read, qualities, 4));
        assertEquals(qualities.get(3),sut.getQualityFor(read, qualities, 5));
    }
    
    
    @Test
    public void multiGapReverseRightFlankingGapIsLowerShouldReturnRightFlankingQuality(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGT-ACGT")
                                    .addRead("readId", 0, "ACGT--CGT", Direction.REVERSE)
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{0,0,14,11,0,0,0}).build();
        
        assertEquals(qualities.get(3),sut.getQualityFor(read, qualities, 4));
        assertEquals(qualities.get(3),sut.getQualityFor(read, qualities, 5));
    }
    @Test
    public void multiGapReverseLeftFlankingGapIsLowerShouldReturnLeftFlankingQuality(){
        Contig<AssembledRead> contig = new DefaultContig.Builder("1234", "ACGT-ACGT")
                                    .addRead("readId", 0, "ACGT--CGT",Direction.REVERSE)
                                    .build();
        AssembledRead read = contig.getRead("readId");
        QualitySequence qualities =  new QualitySequenceBuilder(new byte[]{0,0,14,16,0,0,0}).build();
        
        assertEquals(qualities.get(2),sut.getQualityFor(read, qualities, 4));
        assertEquals(qualities.get(2),sut.getQualityFor(read, qualities, 5));
    }
}
