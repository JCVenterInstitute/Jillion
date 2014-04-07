/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jan 11, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta.nt;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.fasta.AbstractLargeFastaFileDataStore;
/**
 * {@code LargeNucleotideSequenceFastaFileDataStore} is an implementation
 * of {@link NucleotideSequenceFastaDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fasta file
 * which can take some time.  It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.jillion.core.datastore.DataStore, int)}.
 * @author dkatzel
 */
final class LargeNucleotideSequenceFastaFileDataStore extends AbstractLargeFastaFileDataStore<Nucleotide, NucleotideSequence, NucleotideFastaRecord> implements NucleotideFastaDataStore{
    /**
     * Construct a {@link LargeNucleotideSequenceFastaFileDataStore}
     * for the given Fasta file.
     * @param parser the FastaParser to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static NucleotideFastaDataStore create(FastaParser parser){
		return create(parser, DataStoreFilters.alwaysAccept());
	}
	 /**
     * Construct a {@link LargeNucleotideSequenceFastaFileDataStore}
     * for the given Fasta file.
     * @param parser the FastaParser to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static NucleotideFastaDataStore create(FastaParser parser, DataStoreFilter filter){
		return new LargeNucleotideSequenceFastaFileDataStore(parser, filter);
	}
   
    
    public LargeNucleotideSequenceFastaFileDataStore(FastaParser parser,
			DataStoreFilter filter) {
		super(parser, filter);
	}
	
	@Override
	protected StreamingIterator<NucleotideFastaRecord> createNewIterator(
			FastaParser parser, DataStoreFilter filter) throws DataStoreException {
		 try {
			return DataStoreStreamingIterator.create(this,
			    		LargeNucleotideSequenceFastaIterator.createNewIteratorFor(parser,filter));
		} catch (IOException e) {
			throw new DataStoreException("error iterating over fasta file", e);
		}
	}
}
