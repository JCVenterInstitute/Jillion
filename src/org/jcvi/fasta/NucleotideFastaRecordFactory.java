/*
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
/**
 * {@code NucleotideFastaRecordFactory} is an implementation 
 * of {@link FastaRecordFactory} that makes 
 * {@link NucleotideSequenceFastaRecord}s.
 * @author dkatzel
 *
 *
 */
public interface NucleotideFastaRecordFactory extends FastaRecordFactory<NucleotideSequenceFastaRecord<NucleotideEncodedGlyphs>>{

}
