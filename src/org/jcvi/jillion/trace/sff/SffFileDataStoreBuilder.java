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
package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.trace.fastq.FastqRecord;
/**
 * {@code SffFileDataStoreBuilder}
 * is a {@link Builder} that can create new instances
 * of {@link SffFileDataStore}s
 * using data from a given input sff file.
 * @author dkatzel
 *
 */
public class SffFileDataStoreBuilder {
	private final File sffFile;
	
	private DataStoreFilter filter = DataStoreFilters.alwaysAccept();
	//by default store everything in memory
	private DataStoreProviderHint hint = DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED;
	
	/**
	 * Create a new instance of {@code SffFileDataStoreBuilder}
	 * which will build a {@link SffFileDataStore} for the given
	 * sff file.
	 * @param sffFile the sff file make a {@link SffFileDataStore} with. 
	 * @throws IOException if the sff file does not exist, or can not be read.
	 * @throws NullPointerException if sff is null.
	 */
	public SffFileDataStoreBuilder(File sffFile) throws IOException{
		if(sffFile ==null){
			throw new NullPointerException("sff file can not be null");
		}
		if(!sffFile.exists()){
			throw new FileNotFoundException("sff file must exist");
		}
		if(!sffFile.canRead()){
			throw new IOException("sff file is not readable");
		}
		this.sffFile = sffFile;
	}
	/**
	 * Only include the {@link FastqRecord}s which pass
	 * the given {@link DataStoreFilter}.  If a filter
	 * is not given to this builder, then all records
	 * in the sff file will be included in the built
	 * {@link SffFileDataStore}.
	 * @param filter a {@link DataStoreFilter} instance that can be
	 * used to filter out specified flowgram records; can not be null. 
	 * @return this.
	 * @throws NullPointerException if filter is null.
	 */
	public SffFileDataStoreBuilder filter(DataStoreFilter filter){
		if(filter==null){
			throw new NullPointerException("filter can not be null");
		}
		this.filter = filter;
		return this;
	}
	/**
	 * Provide a {@link DataStoreProviderHint} to this builder
	 * to let it know the implementation preferences of the client.
	 * If no hint is given, then this builder will
	 * try to store all the flowgram records in memory
	 * which may cause an Exception if there isn't enough memory.
	 * The {@link DataStoreProviderHint}  is just a guideline 
	 * and may be ignored by this builder when determining
	 * which {@link SffFileDataStore} implementation to chose
	 * to build in {@link #build()}.
	 * @param hint an instance of {@link DataStoreProviderHint};
	 * can not be null.
	 * @return this
	 * @throws NullPointerException if hint is null.
	 * @see DataStoreProviderHint
	 */
	public SffFileDataStoreBuilder hint(DataStoreProviderHint hint){
		if(hint==null){
			throw new NullPointerException("hint can not be null");
		}
		this.hint = hint;
		return this;
	}
	
	
	/**
	 * Parse the given sff file and return
	 * a new instance of a {@link SffFileDataStore}
	 * using all the input parameters given so far.  
	 * If not all optional parameters are set then default
	 * values will be used:
	 * <ul>
	 * <li>
	 * If no {@link DataStoreFilter} has been specified
	 * by {@link #filter(DataStoreFilter)},
	 * then all {@link SffFlowgram}s will be included in this {@link SffFileDataStore}.
	 * </li>
	 * <li>
	 * If no {@link DataStoreProviderHint} has been specified
	 * by {@link #hint(DataStoreProviderHint)},
	 * then this builder will try to store all the 
	 * {@link SffFlowgram}s that meet the {@link DataStoreFilter}
	 * requirements in memory.  This may cause out of memory errors
	 * if there is not enough memory available.
	 * </li>
	 * </ul>
	 * @return a new {@link SffFileDataStore} instance;
	 * never null.
	 * @throws IOException if there is a problem parsing the 
	 * sff file. 
	 * @see #hint(DataStoreProviderHint)
	 */
	public SffFileDataStore build() throws IOException {
		switch(hint){
			case RANDOM_ACCESS_OPTIMIZE_SPEED:
				return DefaultSffFileDataStore.create(sffFile,filter);
			case RANDOM_ACCESS_OPTIMIZE_MEMORY:
				return handleIndexedSffFileDataStore();
			case ITERATION_ONLY:
				return LargeSffFileDataStore.create(sffFile, filter);
			default:
				//can not happen
				throw new IllegalArgumentException("unknown provider hint : "+ hint);
		}
	}
	
	private SffFileDataStore handleIndexedSffFileDataStore() throws IOException{
		SffFileDataStore manifestDataStore = ManifestIndexed454SffFileDataStore.create(sffFile, filter);
		if(manifestDataStore!=null){
			return manifestDataStore;
		}
		//no manifest
		return CompletelyParsedIndexedSffFileDataStore.create(sffFile, filter);
	}
}
