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
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.fasta.qual;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.fasta.FastaParser;
import org.jcvi.jillion.fasta.qual.QualityFastaDataStore;
import org.jcvi.jillion.fasta.qual.QualityFastaRecord;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.fasta.AbstractLargeFastaFileDataStore;
/**
 * {@code LargeQualityFastaFileDataStore} is an implementation
 * of {@link QualitySequenceFastaDataStore} which does not
 * store any Fasta record data 
 * in memory except it's size (which is lazy loaded).
 * This means that each get() or contain() requires re-parsing the fastq file
 * which can take some time.  It is recommended that instances are wrapped
 * in  a cached datastore using
 * {@link DataStoreUtil#createNewCachedDataStore(Class, org.jcvi.jillion.core.datastore.DataStore, int)}.
 * @author dkatzel
 *
 *
 */
public final class LargeQualityFastaFileDataStore extends AbstractLargeFastaFileDataStore<PhredQuality, QualitySequence, QualityFastaRecord> implements QualityFastaDataStore{

    
    public static QualityFastaDataStore create(File fastaFile) throws IOException{
    	return create(fastaFile, DataStoreFilters.alwaysAccept(), null);
    }
    public static QualityFastaDataStore create(File fastaFile, Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter) throws IOException{
    	FastaParser parser = FastaFileParser.create(fastaFile);
    	return new LargeQualityFastaFileDataStore(parser,filter, recordFilter);
    }
    
    public static QualityFastaDataStore create(FastaParser parser){
    	return create(parser, DataStoreFilters.alwaysAccept(), null);
    }
    public static QualityFastaDataStore create(FastaParser parser, Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter){
    	return new LargeQualityFastaFileDataStore(parser,filter, recordFilter);
    }
	protected LargeQualityFastaFileDataStore(FastaParser parser, Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter) {
		super(parser, filter, recordFilter);
	}

	@Override
	protected StreamingIterator<QualityFastaRecord> createNewIterator(
			FastaParser parser, Predicate<String> filter, Predicate<QualityFastaRecord> recordFilter) {
		StreamingIterator<QualityFastaRecord> iter = QualitySequenceFastaDataStoreIteratorImpl.createIteratorFor(parser, filter, recordFilter);
        
        return DataStoreStreamingIterator.create(this,iter);
	}
   
	
}
