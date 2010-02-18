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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import java.util.List;

import org.jcvi.glyph.DefaultEncodedGlyphs;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSangerFastQQualityCodecActual {

    private static final RunLengthEncodedGlyphCodec QUALITY_CODEC = new RunLengthEncodedGlyphCodec(PhredQuality.MAX_VALUE);
    SangerFastQQualityCodec sut = new SangerFastQQualityCodec(
            QUALITY_CODEC);
    String encodedqualities = "I9IG9IC";
    List<PhredQuality> qualities = PhredQuality.valueOf(
            new byte[]{40,24,40,38,24,40,34});
    @Test
    public void decode(){       
        assertEquals(qualities, sut.decode(encodedqualities).decode());
    }
    @Test
    public void encode(){       
        assertEquals(encodedqualities, sut.encode(
                new DefaultEncodedGlyphs<PhredQuality>(QUALITY_CODEC, qualities)));
    }
}
