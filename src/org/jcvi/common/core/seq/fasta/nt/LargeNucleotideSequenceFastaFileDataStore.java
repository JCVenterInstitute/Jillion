/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fasta.nt;

import java.io.File;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreFilters;
import org.jcvi.common.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.internal.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.core.internal.seq.fasta.AbstractLargeFastaFileDataStore;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * {@code LargeNucleotideSequenceFastaFileDataStore} is an implementation
 * of {@link NucleotideSequenceFastaDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fasta file
 * which can take some time.  It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.common.core.datastore.DataStore, int)}.
 * @author dkatzel
 */
final class LargeNucleotideSequenceFastaFileDataStore extends AbstractLargeFastaFileDataStore<Nucleotide, NucleotideSequence, NucleotideSequenceFastaRecord> implements NucleotideSequenceFastaDataStore{
    /**
     * Construct a {@link LargeNucleotideSequenceFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @param fastaRecordFactory the NucleotideFastaRecordFactory implementation to use.
     * @throws NullPointerException if fastaFile is null.
     */
	public static NucleotideSequenceFastaDataStore create(File fastaFile){
		return create(fastaFile, DataStoreFilters.alwaysAccept());
	}
	 /**
     * Construct a {@link LargeNucleotideSequenceFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @param fastaRecordFactory the NucleotideFastaRecordFactory implementation to use.
     * @throws NullPointerException if fastaFile is null.
     */
	public static NucleotideSequenceFastaDataStore create(File fastaFile, DataStoreFilter filter){
		return new LargeNucleotideSequenceFastaFileDataStore(fastaFile, filter);
	}
   
    
    public LargeNucleotideSequenceFastaFileDataStore(File fastaFile,
			DataStoreFilter filter) {
		super(fastaFile, filter);
	}
	
	@Override
	protected StreamingIterator<NucleotideSequenceFastaRecord> createNewIterator(
			File fastaFile) {
		 return DataStoreStreamingIterator.create(this,
	        		LargeNucleotideSequenceFastaIterator.createNewIteratorFor(fastaFile,getFilter()));
	}
}
