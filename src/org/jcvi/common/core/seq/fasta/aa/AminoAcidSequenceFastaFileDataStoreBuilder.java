package org.jcvi.common.core.seq.fasta.aa;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.seq.fasta.FastaDataStore;
import org.jcvi.common.core.seq.fasta.aa.impl.DefaultAminoAcidSequenceFastaDataStore;
import org.jcvi.common.core.seq.fasta.aa.impl.IndexedAminoAcidSequenceFastaFileDataStore;
import org.jcvi.common.core.seq.fasta.aa.impl.LargeAminoAcidSequenceFastaFileDataStore;
import org.jcvi.common.core.seq.fasta.impl.AbstractFastaFileDataStoreBuilder;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;


/**
 * {@code AminoAcidSequenceFastaFileDataStoreBuilder}
 * is a Builder that can create new instances
 * of {@link AminoAcidSequenceFastaDataStore}s
 * using data from a given input fasta file.
 * @author dkatzel
 *
 */
public final class AminoAcidSequenceFastaFileDataStoreBuilder extends AbstractFastaFileDataStoreBuilder<AminoAcid, AminoAcidSequence, AminoAcidSequenceFastaRecord, AminoAcidSequenceFastaDataStore> {

	public AminoAcidSequenceFastaFileDataStoreBuilder(File fastaFile) throws IOException{
		super(fastaFile);
	}
	
	/**
	 * Create a new {@link FastaDataStore} instance.
	 * @param fastaFile the fasta file to make the datastore for;
	 * can not be null and should exist.
	 * @param hint a {@link DataStoreProviderHint}; will never be null.
	 * @param filter a {@link DataStoreFilter}; will never be null.
	 * @return a new {@link FastaDataStore} instance; should never be null.
	 * @throws IOException if there is a problem creating the datastore from the file.
	 */
	@Override
	protected AminoAcidSequenceFastaDataStore createNewInstance(File fastaFile, DataStoreProviderHint hint, DataStoreFilter filter)
			throws IOException {
		switch(hint){
			case OPTIMIZE_RANDOM_ACCESS_SPEED: return DefaultAminoAcidSequenceFastaDataStore.create(fastaFile,filter);
			case OPTIMIZE_RANDOM_ACCESS_MEMORY: return IndexedAminoAcidSequenceFastaFileDataStore.create(fastaFile,filter);
			case OPTIMIZE_ITERATION: return LargeAminoAcidSequenceFastaFileDataStore.create(fastaFile,filter);
			default:
				throw new IllegalArgumentException("unknown provider hint :"+ hint);
		}
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public AminoAcidSequenceFastaFileDataStoreBuilder filter(
			DataStoreFilter filter) {
		super.filter(filter);
		return this;
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public AminoAcidSequenceFastaFileDataStoreBuilder hint(
			DataStoreProviderHint hint) {
		super.hint(hint);
		return this;
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public AminoAcidSequenceFastaDataStore build() throws IOException {
		return super.build();
	}
	
	
}
