/*
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
/**
 * {@code NucleotideFastaRecordFactory} is an implementation 
 * of {@link FastaRecordFactory} that makes 
 * {@code FastaRecord<EncodedGlyphs<PhredQuality>>}s.
 * @author dkatzel
 *
 *
 */
public interface QualityFastaRecordFactory extends FastaRecordFactory<QualityFastaRecord<EncodedGlyphs<PhredQuality>>>{

}
