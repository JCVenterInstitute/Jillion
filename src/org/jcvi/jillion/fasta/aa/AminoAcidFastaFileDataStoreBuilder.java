/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.fasta.aa;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.fasta.FastaDataStore;
import org.jcvi.jillion.internal.fasta.AbstractFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.fasta.aa.DefaultAminoAcidSequenceFastaDataStore;
import org.jcvi.jillion.internal.fasta.aa.IndexedAminoAcidSequenceFastaFileDataStore;
import org.jcvi.jillion.internal.fasta.aa.LargeAminoAcidSequenceFastaFileDataStore;


/**
 * {@code AminoAcidFastaFileDataStoreBuilder}
 * is a Builder that can create new instances
 * of {@link AminoAcidSequenceFastaDataStore}s
 * using data from a given input fasta file.
 * @author dkatzel
 *
 */
public final class AminoAcidFastaFileDataStoreBuilder extends AbstractFastaFileDataStoreBuilder<AminoAcid, AminoAcidSequence, AminoAcidFastaRecord, AminoAcidFastaDataStore> {

	public AminoAcidFastaFileDataStoreBuilder(File fastaFile) throws IOException{
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
	protected AminoAcidFastaDataStore createNewInstance(File fastaFile, DataStoreProviderHint hint, DataStoreFilter filter)
			throws IOException {
		switch(hint){
			case RANDOM_ACCESS_OPTIMIZE_SPEED: return DefaultAminoAcidSequenceFastaDataStore.create(fastaFile,filter);
			case RANDOM_ACCESS_OPTIMIZE_MEMORY: return IndexedAminoAcidSequenceFastaFileDataStore.create(fastaFile,filter);
			case ITERATION_ONLY: return LargeAminoAcidSequenceFastaFileDataStore.create(fastaFile,filter);
			default:
				throw new IllegalArgumentException("unknown provider hint :"+ hint);
		}
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public AminoAcidFastaFileDataStoreBuilder filter(
			DataStoreFilter filter) {
		super.filter(filter);
		return this;
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public AminoAcidFastaFileDataStoreBuilder hint(
			DataStoreProviderHint hint) {
		super.hint(hint);
		return this;
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public AminoAcidFastaDataStore build() throws IOException {
		return super.build();
	}
	
	
}
