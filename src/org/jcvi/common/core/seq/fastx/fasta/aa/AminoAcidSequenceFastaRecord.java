package org.jcvi.common.core.seq.fastx.fasta.aa;

import org.jcvi.common.core.seq.fastx.fasta.FastaRecord;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;

/**
 * {@code AminoAcidSequenceFastaRecord} is an interface for {@link FastaRecord} objects
 * using the {@link AminoAcidSequence}.

 * @author naxelrod
 * @author dkatzel
 */
public interface AminoAcidSequenceFastaRecord extends FastaRecord<AminoAcid,AminoAcidSequence> {

	AminoAcidSequence getSequence();
	
}
