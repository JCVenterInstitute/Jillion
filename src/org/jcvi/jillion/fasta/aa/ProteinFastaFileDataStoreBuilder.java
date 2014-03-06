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
import java.io.InputStream;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.internal.fasta.AbstractFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.fasta.aa.DefaultProteinFastaDataStore;
import org.jcvi.jillion.internal.fasta.aa.IndexedProteinFastaFileDataStore;
import org.jcvi.jillion.internal.fasta.aa.LargeProteinFastaFileDataStore;


/**
 * {@code ProteinFastaFileDataStoreBuilder}
 * is a Builder that can create new instances
 * of {@link ProteinFastaDataStore}s
 * using data from a given input fasta file.
 * @author dkatzel
 *
 */
public final class ProteinFastaFileDataStoreBuilder extends AbstractFastaFileDataStoreBuilder<AminoAcid, ProteinSequence, ProteinFastaRecord, ProteinFastaDataStore> {
	/**
	 * Create a new {@link ProteinFastaFileDataStoreBuilder}
	 * instance that will use the given fasta file
	 * as input.
	 * @param fastaFile the fasta file to use;
	 * must exist and can not be null. 
	 * @throws IOException if the fasta file does not exist
	 * @throws NullPointerException if fastaFile is null.
	 */
	public ProteinFastaFileDataStoreBuilder(File fastaFile) throws IOException{
		super(fastaFile);
	}
	/**
	 * Create a new {@link ProteinFastaFileDataStoreBuilder}
	 * instance that will use the given fasta encoded inputStream
	 * as input.
	 * @param in the fasta encoded data to use can not be null. 
	 * @throws IOException if the fasta file does not exist
	 * @throws NullPointerException if fastaFile is null.
	 */
	public ProteinFastaFileDataStoreBuilder(InputStream in) throws IOException{
		super(in);
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
	protected ProteinFastaDataStore createNewInstance(FastaParser parser, DataStoreProviderHint hint, DataStoreFilter filter)
			throws IOException {
		if(!parser.canCreateMemento()){
			return DefaultProteinFastaDataStore.create(parser,filter);
		}
		switch(hint){
			case RANDOM_ACCESS_OPTIMIZE_SPEED: return DefaultProteinFastaDataStore.create(parser,filter);
			case RANDOM_ACCESS_OPTIMIZE_MEMORY: return IndexedProteinFastaFileDataStore.create(parser,filter);
			case ITERATION_ONLY: return LargeProteinFastaFileDataStore.create(parser,filter);
			default:
				throw new IllegalArgumentException("unknown provider hint :"+ hint);
		}
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ProteinFastaFileDataStoreBuilder filter(
			DataStoreFilter filter) {
		super.filter(filter);
		return this;
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ProteinFastaFileDataStoreBuilder hint(
			DataStoreProviderHint hint) {
		super.hint(hint);
		return this;
	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ProteinFastaDataStore build() throws IOException {
		return super.build();
	}
	
	
}
