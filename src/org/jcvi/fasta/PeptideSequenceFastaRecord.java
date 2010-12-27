package org.jcvi.fasta;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.aa.AminoAcid;
import org.jcvi.glyph.aa.AminoAcidEncodedGlyphs;

/**
 * {@code PeptideSequenceFastaRecord} is an interface for {@link FastaRecord} objects
 * using the AminoAcid encoded glyphs.

 * @author naxelrod
 *
 */
public interface PeptideSequenceFastaRecord extends FastaRecord<EncodedGlyphs<AminoAcid>> {

	AminoAcidEncodedGlyphs getValues();
	
}
