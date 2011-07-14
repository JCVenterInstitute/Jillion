package org.jcvi.fastX.fasta.aa;

import org.jcvi.fastX.fasta.FastaRecord;
import org.jcvi.glyph.aa.AminoAcidSequence;

/**
 * {@code PeptideSequenceFastaRecord} is an interface for {@link FastaRecord} objects
 * using the AminoAcid encoded glyphs.

 * @author naxelrod
 *
 */
public interface AminoAcidSequenceFastaRecord extends FastaRecord<AminoAcidSequence> {

	AminoAcidSequence getValue();
	
}
