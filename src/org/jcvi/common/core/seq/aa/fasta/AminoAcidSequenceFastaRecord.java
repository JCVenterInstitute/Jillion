package org.jcvi.common.core.seq.aa.fasta;

import org.jcvi.common.core.seq.aa.AminoAcidSequence;
import org.jcvi.common.core.seq.fastx.fasta.FastaRecord;

/**
 * {@code PeptideSequenceFastaRecord} is an interface for {@link FastaRecord} objects
 * using the AminoAcid encoded glyphs.

 * @author naxelrod
 *
 */
public interface AminoAcidSequenceFastaRecord extends FastaRecord<AminoAcidSequence> {

	AminoAcidSequence getValue();
	
}
