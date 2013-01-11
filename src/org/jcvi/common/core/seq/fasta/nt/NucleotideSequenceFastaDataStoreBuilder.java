package org.jcvi.common.core.seq.fasta.nt;

import org.jcvi.common.core.seq.fasta.FastaDataStoreBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code NucleotideSequenceFastaDataStoreBuilder} is a {@link FastaDataStoreBuilder}
 * that builds a {@link NucleotideSequenceFastaDataStore}.
 * @author dkatzel
 *
 */
interface NucleotideSequenceFastaDataStoreBuilder extends FastaDataStoreBuilder<Nucleotide, NucleotideSequence, NucleotideSequenceFastaRecord, NucleotideSequenceFastaDataStore>{
	/**
	 * Adds the given {@link NucleotideSequenceFastaRecord} to this builder.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	NucleotideSequenceFastaDataStoreBuilder addFastaRecord(NucleotideSequenceFastaRecord fastaRecord);
}
