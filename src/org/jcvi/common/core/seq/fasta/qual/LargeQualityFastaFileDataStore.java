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
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fasta.qual;

import java.io.File;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreFilters;
import org.jcvi.common.core.datastore.DataStoreUtil;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.internal.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.core.internal.seq.fasta.AbstractLargeFastaFileDataStore;
/**
 * {@code LargeQualityFastaFileDataStore} is an implementation
 * of {@link QualitySequenceFastaDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fastq file
 * which can take some time.  It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.common.core.datastore.DataStore, int)}.
 * @author dkatzel
 *
 *
 */
final class LargeQualityFastaFileDataStore extends AbstractLargeFastaFileDataStore<PhredQuality, QualitySequence, QualitySequenceFastaRecord> implements QualitySequenceFastaDataStore{

    
    public static QualitySequenceFastaDataStore create(File fastaFile){
    	return create(fastaFile, DataStoreFilters.alwaysAccept());
    }
    public static QualitySequenceFastaDataStore create(File fastaFile, DataStoreFilter filter){
    	return new LargeQualityFastaFileDataStore(fastaFile,filter);
    }
	protected LargeQualityFastaFileDataStore(File fastaFile, DataStoreFilter filter) {
		super(fastaFile, filter);
	}

	@Override
	protected StreamingIterator<QualitySequenceFastaRecord> createNewIterator(
			File fastaFile) {
		QualitySequenceFastaDataStoreIteratorImpl iter = new QualitySequenceFastaDataStoreIteratorImpl(fastaFile);
        iter.start();
        
        return DataStoreStreamingIterator.create(this,iter);
	}
   
	
}
