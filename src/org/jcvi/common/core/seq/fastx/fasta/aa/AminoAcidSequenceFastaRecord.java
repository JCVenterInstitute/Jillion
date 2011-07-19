package org.jcvi.common.core.seq.fastx.fasta.aa;

import org.jcvi.common.core.seq.fastx.fasta.FastaRecord;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;

/**
 * {@code PeptideSequenceFastaRecord} is an interface for {@link FastaRecord} objects
 * using the AminoAcid encoded glyphs.

 * @author naxelrod
 *
 */
public interface AminoAcidSequenceFastaRecord extends FastaRecord<AminoAcidSequence> {

	AminoAcidSequence getValue();
	
}
