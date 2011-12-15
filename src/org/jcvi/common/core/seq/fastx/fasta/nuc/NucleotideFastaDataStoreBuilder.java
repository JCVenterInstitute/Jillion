package org.jcvi.common.core.seq.fastx.fasta.nuc;

import org.jcvi.common.core.seq.fastx.fasta.FastaDataStoreBuilder;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
/**
 * {@code NucleotideFastaDataStoreBuilder} is a {@link FastaDataStoreBuilder}
 * that builds a {@link NucleotideFastaDataStore}.
 * @author dkatzel
 *
 */
public interface NucleotideFastaDataStoreBuilder extends FastaDataStoreBuilder<Nucleotide, NucleotideSequence, NucleotideSequenceFastaRecord, NucleotideFastaDataStore>{
	/**
	 * Adds the given {@link NucleotideSequenceFastaRecord} to this builder.
	 * <p/>
	 * {@inheritDoc}
	 */
	@Override
	public NucleotideFastaDataStoreBuilder addFastaRecord(NucleotideSequenceFastaRecord fastaRecord);
}
