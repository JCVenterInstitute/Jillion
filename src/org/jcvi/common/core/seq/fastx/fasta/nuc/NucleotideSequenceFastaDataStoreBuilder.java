package org.jcvi.common.core.seq.fastx.fasta.nuc;

import org.jcvi.common.core.seq.fastx.fasta.FastaDataStoreBuilder;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
/**
 * {@code NucleotideSequenceFastaDataStoreBuilder} is a {@link FastaDataStoreBuilder}
 * that builds a {@link NucleotideSequenceFastaDataStore}.
 * @author dkatzel
 *
 */
public interface NucleotideSequenceFastaDataStoreBuilder extends FastaDataStoreBuilder<Nucleotide, NucleotideSequence, NucleotideSequenceFastaRecord, NucleotideSequenceFastaDataStore>{
	/**
	 * Adds the given {@link NucleotideSequenceFastaRecord} to this builder.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	public NucleotideSequenceFastaDataStoreBuilder addFastaRecord(NucleotideSequenceFastaRecord fastaRecord);
}
