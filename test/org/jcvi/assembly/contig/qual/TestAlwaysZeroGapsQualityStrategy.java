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

package org.jcvi.assembly.contig.qual;

import static org.junit.Assert.assertEquals;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.DefaultContig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.EncodedSequence;
import org.jcvi.glyph.Sequence;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.SequenceDirection;
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
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
        .addRead("readId", 0, "ACGT-")
        .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{11,12,13,14}));
        
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 4));
    }
    
    @Test
    public void readStartsEndsWithGapShouldReturnQualityValue0(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
        .addRead("readId", 0, "-ACGT")
        .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{11,12,13,14}));
        
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 0));
    }
    
    @Test
    public void oneGapShouldReturnQualityValue0(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT")
                                    .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{11,12,13,14,15,16,17}));
        
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 4));
    }
    @Test
    public void oneGapShouldReverseReturnQualityValue0(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGTACGT")
                                    .addRead("readId", 0, "ACGT-CGT", SequenceDirection.REVERSE)
                                    .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{11,12,13,14,15,16,17}));
        
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 4));
    }
    
    @Test
    public void multiGapShouldReturnQualityValue0(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGT-ACGT")
                                    .addRead("readId", 0, "ACGT--CGT")
                                    .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{11,12,13,14,15,16}));
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 4));
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 5));
    }
    @Test
    public void multiGapGapShouldReverseReturnQualityValue0(){
        Contig<PlacedRead> contig = new DefaultContig.Builder("1234", "ACGT-ACGT")
                                    .addRead("readId", 0, "ACGT--CGT", SequenceDirection.REVERSE)
                                    .build();
        PlacedRead read = contig.getPlacedReadById("readId");
        Sequence<PhredQuality> qualities = new EncodedSequence<PhredQuality>(RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE,
                PhredQuality.valueOf(new byte[]{11,12,13,14,15,16}));
        
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 4));
        assertEquals(ZERO,sut.getQualityFor(read, qualities, 5));
    }

}
