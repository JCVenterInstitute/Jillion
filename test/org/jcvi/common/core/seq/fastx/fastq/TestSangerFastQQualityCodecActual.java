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
package org.jcvi.common.core.seq.fastx.fastq;

import java.util.List;

import org.jcvi.common.core.seq.fastx.fastq.FastqQualityCodec;
import org.jcvi.common.core.symbol.EncodedSequence;
import org.jcvi.common.core.symbol.RunLengthEncodedGlyphCodec;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSangerFastQQualityCodecActual {

    private static final RunLengthEncodedGlyphCodec QUALITY_CODEC = new RunLengthEncodedGlyphCodec(PhredQuality.MAX_VALUE);
    FastqQualityCodec sut = FastqQualityCodec.SANGER;
    String encodedqualities = "I9IG9IC";
    List<PhredQuality> qualities = PhredQuality.valueOf(
            new byte[]{40,24,40,38,24,40,34});
    @Test
    public void decode(){       
        assertEquals(qualities, sut.decode(encodedqualities).asList());
    }
    @Test
    public void encode(){       
        assertEquals(encodedqualities, sut.encode(
                new EncodedSequence<PhredQuality>(QUALITY_CODEC, qualities)));
    }
}
