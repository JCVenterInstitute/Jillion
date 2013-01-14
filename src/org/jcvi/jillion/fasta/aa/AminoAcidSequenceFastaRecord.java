package org.jcvi.jillion.fasta.aa;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.fasta.FastaRecord;

/**
 * {@code AminoAcidSequenceFastaRecord} is an interface for {@link FastaRecord} objects
 * using the {@link AminoAcidSequence}.

 * @author naxelrod
 * @author dkatzel
 */
public interface AminoAcidSequenceFastaRecord extends FastaRecord<AminoAcid,AminoAcidSequence> {

	AminoAcidSequence getSequence();
	
}
