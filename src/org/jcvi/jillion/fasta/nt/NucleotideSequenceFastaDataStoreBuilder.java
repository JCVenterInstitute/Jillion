package org.jcvi.jillion.fasta.nt;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.fasta.FastaDataStoreBuilder;
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
