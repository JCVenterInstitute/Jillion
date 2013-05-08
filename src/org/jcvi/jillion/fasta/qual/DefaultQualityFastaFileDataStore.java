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
/*
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta.qual;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.internal.fasta.qual.DefaultQualityFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.fasta.qual.LargeQualityFastaFileDataStore;
/**
 * {@code DefaultQualityFastaFileDataStore} is the default implementation
 * of {@link QualitySequenceFastaDataStore} which stores
 * all fasta records in memory.  This is only recommended for small fasta
 * files that won't take up too much memory.
 * @author dkatzel
 * @see LargeQualityFastaFileDataStore
 *
 */
final class DefaultQualityFastaFileDataStore {
    
	private DefaultQualityFastaFileDataStore(){
		//can not instantiate
	}
    public static QualityFastaDataStore create(File fastaFile) throws IOException{
    	return create(fastaFile,DataStoreFilters.alwaysAccept());
    }
    
    public static QualityFastaDataStore create(File fastaFile, DataStoreFilter filter) throws IOException{
    	DefaultQualityFastaFileDataStoreBuilder builder = createBuilder(filter);
    	FastaFileParser.create(fastaFile).accept(builder);
    	return builder.build();
    }
    
    public static QualityFastaDataStore create(InputStream fastaStream) throws IOException{
    	return create(fastaStream,DataStoreFilters.alwaysAccept());
    }
    public static QualityFastaDataStore create(InputStream fastaStream, DataStoreFilter filter) throws IOException{
    	DefaultQualityFastaFileDataStoreBuilder builder = createBuilder(filter);
    	FastaFileParser.create(fastaStream).accept(builder);
    	return builder.build();
    }

    private static DefaultQualityFastaFileDataStoreBuilder createBuilder(DataStoreFilter filter){
    	return new DefaultQualityFastaFileDataStoreBuilder(filter);
    }

}
