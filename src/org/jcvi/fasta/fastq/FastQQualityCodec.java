/*
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public interface FastQQualityCodec {

    EncodedGlyphs<PhredQuality> decode(String fastqQualities);
    String encode(EncodedGlyphs<PhredQuality> qualities);
}
