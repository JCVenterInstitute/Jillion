package org.jcvi.common.core.seq.fasta.aa;

import org.jcvi.common.core.seq.fasta.FastaRecord;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;

/**
 * {@code AminoAcidSequenceFastaRecord} is an interface for {@link FastaRecord} objects
 * using the {@link AminoAcidSequence}.

 * @author naxelrod
 * @author dkatzel
 */
public interface AminoAcidSequenceFastaRecord extends FastaRecord<AminoAcid,AminoAcidSequence> {

	AminoAcidSequence getSequence();
	
}
