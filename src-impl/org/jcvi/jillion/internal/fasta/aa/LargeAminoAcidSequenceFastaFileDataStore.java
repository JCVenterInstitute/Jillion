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
package org.jcvi.jillion.internal.fasta.aa;


import java.io.File;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.aa.AminoAcidFastaDataStore;
import org.jcvi.jillion.fasta.aa.AminoAcidFastaRecord;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.fasta.AbstractLargeFastaFileDataStore;

/**
 * {@code LargeAminoAcidSequenceFastaFileDataStore} is an implementation
 * of {@link AminoAcidSequenceFastaDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fasta file
 * which can take some time. It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.jillion.core.datastore.DataStore, int)}.
 * @author dkatzel
 */
public final class LargeAminoAcidSequenceFastaFileDataStore extends AbstractLargeFastaFileDataStore<AminoAcid, AminoAcidSequence, AminoAcidFastaRecord> implements AminoAcidFastaDataStore{
	
	
	
    /**
     * Construct a {@link LargeAminoAcidSequenceFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static AminoAcidFastaDataStore create(File fastaFile){
		return create(fastaFile, DataStoreFilters.alwaysAccept());
	}
	/**
     * Construct a {@link LargeAminoAcidSequenceFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static AminoAcidFastaDataStore create(File fastaFile, DataStoreFilter filter){
		return new LargeAminoAcidSequenceFastaFileDataStore(fastaFile,filter);
	}
   
    protected LargeAminoAcidSequenceFastaFileDataStore(File fastaFile,
			DataStoreFilter filter) {
		super(fastaFile, filter);
	}


	@Override
	protected StreamingIterator<AminoAcidFastaRecord> createNewIterator(
			File fastaFile, DataStoreFilter filter) {
		return DataStoreStreamingIterator.create(this,LargeAminoAcidSequenceFastaIterator.createNewIteratorFor(fastaFile, filter));
	       
	}
   
   
}

