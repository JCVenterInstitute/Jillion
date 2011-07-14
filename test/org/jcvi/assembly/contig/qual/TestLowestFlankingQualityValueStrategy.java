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
/*
 * Created on Feb 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.qual;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.DefaultContig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.EncodedSequence;
import org.jcvi.glyph.Sequence;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestLowestFlankingQualityValueStrategy extends AbstractGapQualityValueStrategies{


    GapQualityValueStrategies sut = GapQualityValueStrategies.LOWEST_FLANKING;

    @Test
    public void readEndsWithGapShouldReturnQualityValue1(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
        .addRead("readId", 0, "ACGT-")
        .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{11,12,13,14}));
        
        assertEquals(PhredQuality.valueOf(1),sut.getQualityFor(read, qualities, 4));
    }
    
    @Test
    public void readStartsEndsWithGapShouldReturnQualityValue1(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
        .addRead("readId", 0, "-ACGT")
        .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{11,12,13,14}));
        
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
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT")
                                    .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{0,0,0,16,16,0,0,0}));
        
        assertEquals(qualities.get(3),sut.getQualityFor(read, qualities, 4));
    }
    
    @Test
    public void leftFlankingGapIsLowerShouldReturnLeftFlankingQuality(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT")
                                    .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{0,0,0,14,16,0,0,0}));
        
        assertEquals(qualities.get(3),sut.getQualityFor(read, qualities, 4));
    }
    @Test
    public void reverseLeftFlankingGapIsLowerShouldReturnLeftFlankingQuality(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT",SequenceDirection.REVERSE)
                                    .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{0,0,14,16,0,0,0}));
        
        assertEquals(qualities.get(2),sut.getQualityFor(read, qualities, 4));
    }
    @Test
    public void rightFlankingGapIsLowerShouldReturnRightFlankingQuality(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT")
                                    .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{0,0,0,14,11,0,0,0}));
        
        assertEquals(qualities.get(4),sut.getQualityFor(read, qualities, 4));
    }
    @Test
    public void rightReverseFlankingGapIsLowerShouldReturnRightFlankingQuality(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT",SequenceDirection.REVERSE)
                                    .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{0,0,14,11,0,0,0}));
        
        assertEquals(qualities.get(3),sut.getQualityFor(read, qualities, 4));
    }
    @Test
    public void multiGapRightFlankingGapIsLowerShouldReturnRightFlankingQuality(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGT-ACGT")
                                    .addRead("readId", 0, "ACGT--CGT")
                                    .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{0,0,0,14,11,0,0,0}));
        
        assertEquals(qualities.get(4),sut.getQualityFor(read, qualities, 4));
        assertEquals(qualities.get(4),sut.getQualityFor(read, qualities, 5));
    }
    @Test
    public void multiGapLeftFlankingGapIsLowerShouldReturnLeftFlankingQuality(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGT-ACGT")
                                    .addRead("readId", 0, "ACGT--CGT")
                                    .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{0,0,0,14,16,0,0,0}));
        
        assertEquals(qualities.get(3),sut.getQualityFor(read, qualities, 4));
        assertEquals(qualities.get(3),sut.getQualityFor(read, qualities, 5));
    }
    
    
    @Test
    public void multiGapReverseRightFlankingGapIsLowerShouldReturnRightFlankingQuality(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGT-ACGT")
                                    .addRead("readId", 0, "ACGT--CGT", SequenceDirection.REVERSE)
                                    .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{0,0,14,11,0,0,0}));
        
        assertEquals(qualities.get(3),sut.getQualityFor(read, qualities, 4));
        assertEquals(qualities.get(3),sut.getQualityFor(read, qualities, 5));
    }
    @Test
    public void multiGapReverseLeftFlankingGapIsLowerShouldReturnLeftFlankingQuality(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGT-ACGT")
                                    .addRead("readId", 0, "ACGT--CGT",SequenceDirection.REVERSE)
                                    .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{0,0,14,16,0,0,0}));
        
        assertEquals(qualities.get(2),sut.getQualityFor(read, qualities, 4));
        assertEquals(qualities.get(2),sut.getQualityFor(read, qualities, 5));
    }
}
