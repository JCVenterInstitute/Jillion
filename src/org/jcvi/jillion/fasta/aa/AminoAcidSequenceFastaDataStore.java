package org.jcvi.jillion.fasta.aa;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.fasta.FastaDataStore;

/**
 * {@code AminoAcidSequenceFastaDataStore} is a 
 * marker interface for {@link FastaDataStore}s
 * of {@link AminoAcidSequenceFastaRecord}s.
 * @author dkatzel
 *
 */
public interface AminoAcidSequenceFastaDataStore extends FastaDataStore<AminoAcid, AminoAcidSequence, AminoAcidSequenceFastaRecord> {

}
