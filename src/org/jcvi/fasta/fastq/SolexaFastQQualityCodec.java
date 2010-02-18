/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;
/**
 * {@code SolexaFastQQualityCodec} supports Solexa 1.3+
 * FastQ format.
 * @author dkatzel
 *
 *
 */
public class SolexaFastQQualityCodec extends AbstractFastQQualityCodec{

    public SolexaFastQQualityCodec(GlyphCodec<PhredQuality> qualityCodec) {
        super(qualityCodec);
    }

    @Override
    protected PhredQuality decode(char encodedQuality) {
        return PhredQuality.valueOf(encodedQuality -64);
    }

    @Override
    protected char encode(PhredQuality quality) {
        return (char)(quality.getNumber().intValue()+64);
    }
    

}
