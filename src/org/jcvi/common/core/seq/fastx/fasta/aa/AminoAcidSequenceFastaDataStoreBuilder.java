package org.jcvi.common.core.seq.fastx.fasta.aa;

import org.jcvi.common.core.seq.fastx.fasta.FastaDataStoreBuilder;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;

public interface AminoAcidSequenceFastaDataStoreBuilder 
	extends FastaDataStoreBuilder<AminoAcid, AminoAcidSequence, AminoAcidSequenceFastaRecord, AminoAcidSequenceFastaDataStore>{

}
