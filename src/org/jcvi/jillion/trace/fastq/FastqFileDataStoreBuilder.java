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
package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.Builder;
/**
 * {@code FastqFileDataStoreBuilder}
 * is a {@link Builder} that can create new instances
 * of {@link FastqDataStore}s
 * using data from a given input fastq file.
 * @author dkatzel
 *
 */
public final class FastqFileDataStoreBuilder{
	private final File fastqFile;
	
	private DataStoreFilter filter = DataStoreFilters.alwaysAccept();
	//by default store everything in memory
	private DataStoreProviderHint hint = DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED;
	
	private FastqQualityCodec codec=null;
	
	/**
	 * Create a new instance of {@code FastqFileDataStoreBuilder}
	 * which will build a {@link FastqDataStore} for the given
	 * fastq file.
	 * @param fastqFile the fastq file make a {@link FastqDataStore} with. 
	 * @throws IOException if the fastq file does not exist, or can not be read.
	 * @throws NullPointerException if fastqFile is null.
	 */
	public FastqFileDataStoreBuilder(File fastqFile) throws IOException{
		if(fastqFile ==null){
			throw new NullPointerException("fastq file can not be null");
		}
		if(!fastqFile.exists()){
			throw new FileNotFoundException("fastq file must exist");
		}
		if(!fastqFile.canRead()){
			throw new IOException("fastq file is not readable");
		}
		this.fastqFile = fastqFile;
	}
	/**
	 * Explicitly specify the {@link FastqQualityCodec} that 
	 * is used to encode the quality values in the given fastq file.
	 * If the given {@link FastqQualityCodec} is not the one used to encode
	 * the quality data in the file, then incorrect
	 * quality values might silently decode 
	 * the wrong (possibly higher or lower) quality values or
	 * cause an {@link IllegalArgumentException}
	 * to be thrown during the {@link #build()} if the incorrectly decoded quality values are not
	 * valid phred scores.
	 * <p/>
	 * If a quality codec is not given to this builder,
	 * then during the actual datastore construction in {@link #build()},
	 * the codec will be automatically determined by parsing a portion of the 
	 * file an additional time.  This causes extra I/O and increases CPU and execution time
	 * to create a new {@link FastqDataStore} so it is recommended that the {@link FastqQualityCodec} 
	 * is given if it is known to avoid this performance penalty.
	 * @param codec the {@link FastqQualityCodec} to use to parse the file; can not be null.
	 * @return this.
	 * @throws NullPointerException if codec is null.
	 */
	public FastqFileDataStoreBuilder qualityCodec(FastqQualityCodec codec){
		if(codec==null){
			throw new NullPointerException("quality codec can not be null");
		}
		this.codec = codec;
		return this;
	}
	/**
	 * Only include the {@link FastqRecord}s which pass
	 * the given {@link DataStoreFilter}.  If a filter
	 * is not given to this builder, then all records
	 * in the fastq file will be included in the built
	 * {@link FastqDataStore}.
	 * @param filter a {@link DataStoreFilter} instance that can be
	 * used to filter out specified fastq records; can not be null. 
	 * @return this.
	 * @throws NullPointerException if filter is null.
	 */
	public FastqFileDataStoreBuilder filter(DataStoreFilter filter){
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
	 * try to store all the fastq records in memory
	 * which may cause an Exception if there isn't enough memory.
	 * The {@link DataStoreProviderHint}  is just a guideline 
	 * and may be ignored by this builder when determining
	 * which {@link FastqDataStore} implementation to chose
	 * to build in {@link #build()}.
	 * @param hint an instance of {@link DataStoreProviderHint};
	 * can not be null.
	 * @return this
	 * @throws NullPointerException if hint is null.
	 * @see DataStoreProviderHint
	 */
	public FastqFileDataStoreBuilder hint(DataStoreProviderHint hint){
		if(hint==null){
			throw new NullPointerException("hint can not be null");
		}
		this.hint = hint;
		return this;
	}
	
	
	/**
	 * Parse the given fastq file and return
	 * a new instance of a {@link FastqDataStore}
	 * using all the input parameters given so far.  
	 * If not all optional parameters are set then default
	 * values will be used:
	 * <ul>
	 * <li>
	 * If no {@link FastqQualityCodec} has been specified
	 * by {@link #qualityCodec(FastqQualityCodec)},
	 * then it will be auto-detected for a performance
	 * penalty.
	 * </li>
	 * <li>
	 * If no {@link DataStoreFilter} has been specified
	 * by {@link #filter(DataStoreFilter)},
	 * then all {@link FastqRecord}s will be included in this {@link FastqDataStore}.
	 * </li>
	 * <li>
	 * If no {@link DataStoreProviderHint} has been specified
	 * by {@link #hint(DataStoreProviderHint)},
	 * then this builder will try to store all the 
	 * {@link FastqRecord}s that meet the {@link DataStoreFilter}
	 * requirements in memory.  This may cause out of memory errors
	 * if there is not enough memory available.
	 * </li>
	 * </ul>
	 * @return a new {@link FastqDataStore} instance;
	 * never null.
	 * @throws IOException if there is a problem parsing the 
	 * fastq file.
	 * @throws IllegalArgumentException if the quality values
	 * are not valid for the specified {@link FastqQualityCodec}
	 * (can be thrown even if the quality codec is auto-detected).
	 * @see #qualityCodec(FastqQualityCodec)
	 * @see #hint(DataStoreProviderHint)
	 */
	public FastqDataStore build() throws IOException {
		if(codec ==null){
			codec = FastqUtil.guessQualityCodecUsed(fastqFile);
		}
		switch(hint){
			case RANDOM_ACCESS_OPTIMIZE_SPEED:
				return DefaultFastqFileDataStore.create(fastqFile,filter, codec);
			case RANDOM_ACCESS_OPTIMIZE_MEMORY:
				return IndexedFastqFileDataStore.create(fastqFile, codec, filter);
			case ITERATION_ONLY:
				return LargeFastqFileDataStore.create(fastqFile, filter, codec);
			default:
				//can not happen
				throw new IllegalArgumentException("unknown provider hint : "+ hint);
		}
	}


	
}
