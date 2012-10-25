package org.jcvi.common.core.seq.fastx.fasta.nt;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.seq.fastx.fasta.AbstractFastaFileDataStoreBuilder;
import org.jcvi.common.core.seq.fastx.fasta.FastaDataStore;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * {@code NucleotideSequenceFastaFileDataStoreFactory}
 * is a factory class that can create new instances
 * of {@link NucleotideSequenceFastaDataStore}s
 * using data from a given input fasta file.
 * @author dkatzel
 *
 */
public final class NucleotideSequenceFastaFileDataStoreBuilder extends AbstractFastaFileDataStoreBuilder<Nucleotide, NucleotideSequence, NucleotideSequenceFastaRecord, NucleotideSequenceFastaDataStore>{

	/**
	 * Create a new Builder instance of 
	 * which will build a {@link FastaDataStore} for the given
	 * fasta file.
	 * @param fastaFile the fasta file make a {@link FastaDataStore} with. 
	 * @throws IOException if the fasta file does not exist, or can not be read.
	 * @throws NullPointerException if fastaFile is null.
	 */
	public NucleotideSequenceFastaFileDataStoreBuilder(File fastaFile)
			throws IOException {
		super(fastaFile);
	}
	
	
	@Override
	protected NucleotideSequenceFastaDataStore createNewInstance(
			File fastaFile, DataStoreProviderHint providerHint, DataStoreFilter filter)
			throws IOException {
		switch(providerHint){
			case OPTIMIZE_RANDOM_ACCESS_SPEED: return DefaultNucleotideSequenceFastaFileDataStore.create(fastaFile,filter);
			case OPTIMIZE_RANDOM_ACCESS_MEMORY: return IndexedNucleotideSequenceFastaFileDataStore.create(fastaFile,filter);
			case OPTIMIZE_ITERATION: return LargeNucleotideSequenceFastaFileDataStore.create(fastaFile,filter);
			default:
				throw new IllegalArgumentException("unknown provider hint : "+ providerHint);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public NucleotideSequenceFastaFileDataStoreBuilder filter(
			DataStoreFilter filter) {
		super.filter(filter);
		return this;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public NucleotideSequenceFastaFileDataStoreBuilder hint(
			DataStoreProviderHint hint) {
		super.hint(hint);
		return this;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public NucleotideSequenceFastaDataStore build() throws IOException {
		return super.build();
	}
	
	
}
