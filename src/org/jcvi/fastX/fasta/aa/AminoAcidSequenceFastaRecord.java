package org.jcvi.fastX.fasta.aa;

import org.jcvi.fastX.fasta.FastaRecord;
import org.jcvi.glyph.aa.AminoAcidEncodedGlyphs;

/**
 * {@code PeptideSequenceFastaRecord} is an interface for {@link FastaRecord} objects
 * using the AminoAcid encoded glyphs.

 * @author naxelrod
 *
 */
public interface AminoAcidSequenceFastaRecord extends FastaRecord<AminoAcidEncodedGlyphs> {

	AminoAcidEncodedGlyphs getValue();
	
}
