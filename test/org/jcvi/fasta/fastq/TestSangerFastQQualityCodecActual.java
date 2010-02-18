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
