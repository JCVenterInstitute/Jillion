/*
 * Created on Jan 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public interface QualityFastaRecord<T extends EncodedGlyphs<PhredQuality>> extends FastaRecord<T> {

}
