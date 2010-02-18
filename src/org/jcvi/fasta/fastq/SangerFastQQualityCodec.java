/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import org.jcvi.glyph.GlyphCodec;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class SangerFastQQualityCodec extends AbstractFastQQualityCodec{

    public SangerFastQQualityCodec(GlyphCodec<PhredQuality> qualityCodec) {
        super(qualityCodec);
    }

    @Override
    protected PhredQuality decode(char encodedQuality) {
        return PhredQuality.valueOf(encodedQuality -33);
    }

    @Override
    protected char encode(PhredQuality quality) {
        return (char)(quality.getNumber().intValue()+33);
    }

}
