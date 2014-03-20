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
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.aa.ProteinFastaDataStore;
import org.jcvi.jillion.fasta.aa.ProteinFastaRecord;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.fasta.AbstractLargeFastaFileDataStore;

/**
 * {@code LargeProteinFastaFileDataStore} is an implementation
 * of {@link ProteinFastaDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fasta file
 * which can take some time. It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.jillion.core.datastore.DataStore, int)}.
 * @author dkatzel
 */
public final class LargeProteinFastaFileDataStore extends AbstractLargeFastaFileDataStore<AminoAcid, ProteinSequence, ProteinFastaRecord> implements ProteinFastaDataStore{
	
	
	
    /**
     * Construct a {@link LargeProteinFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static ProteinFastaDataStore create(File fastaFile) throws IOException{
		return create(fastaFile, DataStoreFilters.alwaysAccept());
	}
	/**
     * Construct a {@link LargeProteinFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static ProteinFastaDataStore create(File fastaFile, DataStoreFilter filter) throws IOException{
		FastaParser parser = FastaFileParser.create(fastaFile);
		return new LargeProteinFastaFileDataStore(parser,filter);
	}
	/**
     * Construct a {@link LargeProteinFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static ProteinFastaDataStore create(FastaParser parser){
		return create(parser, DataStoreFilters.alwaysAccept());
	}
	/**
     * Construct a {@link LargeProteinFastaFileDataStore}
     * for the given Fasta file.
     * @param fastaFile the Fasta File to use, can not be null.
     * @throws NullPointerException if fastaFile is null.
     */
	public static ProteinFastaDataStore create(FastaParser parser, DataStoreFilter filter){
		return new LargeProteinFastaFileDataStore(parser,filter);
	}
   
    protected LargeProteinFastaFileDataStore(FastaParser parser,
			DataStoreFilter filter) {
		super(parser, filter);
	}


	@Override
	protected StreamingIterator<ProteinFastaRecord> createNewIterator(
			FastaParser parser, DataStoreFilter filter) {
		return DataStoreStreamingIterator.create(this,LargeProteinFastaIterator.createNewIteratorFor(parser, filter));
	       
	}
   
   
}

