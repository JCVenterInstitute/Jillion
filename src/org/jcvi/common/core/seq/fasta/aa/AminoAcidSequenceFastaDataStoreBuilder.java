package org.jcvi.common.core.seq.fasta.aa;

import org.jcvi.common.core.seq.fasta.FastaDataStoreBuilder;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;

public interface AminoAcidSequenceFastaDataStoreBuilder 
	extends FastaDataStoreBuilder<AminoAcid, AminoAcidSequence, AminoAcidSequenceFastaRecord, AminoAcidSequenceFastaDataStore>{

}
