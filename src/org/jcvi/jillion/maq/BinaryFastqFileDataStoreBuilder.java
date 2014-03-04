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
package org.jcvi.jillion.maq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteOrder;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
/**
 * {@code BinaryFastqFileDataStoreBuilder}
 * is a {@link Builder} that can create new instances
 * of {@link FastqDataStore}s
 * using data from a given input fastq file.
 * @author dkatzel
 *
 */
public final class BinaryFastqFileDataStoreBuilder{
	private final File fastqFile;
	/**
	 * Assume native Endain unless
	 * told otherwise.
	 */
	private ByteOrder endian = ByteOrder.nativeOrder();
	private DataStoreFilter filter = DataStoreFilters.alwaysAccept();
	//by default store everything in memory
	private DataStoreProviderHint hint = DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED;
	
	/**
	 * Create a new instance of {@code BinaryFastqFileDataStoreBuilder}
	 * which will build a {@link FastqDataStore} for the given
	 * fastq file.
	 * @param fastqFile the fastq file make a {@link FastqDataStore} with. 
	 * @throws IOException if the fastq file does not exist, or can not be read.
	 * @throws NullPointerException if fastqFile is null.
	 */
	public BinaryFastqFileDataStoreBuilder(File fastqFile) throws IOException{
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
	public BinaryFastqFileDataStoreBuilder filter(DataStoreFilter filter){
		if(filter==null){
			throw new NullPointerException("filter can not be null");
		}
		this.filter = filter;
		return this;
	}
	/**
	 * Set the endian that this Binary fastq file 
	 * was written in.  Maq has a "feature"
	 * that the binary file written out is in the
	 * native endian of the machine that ran the program.
	 * This means that MAQ can not read the its own
	 *  bfq files that were created on a machine with
	 *  a different endian.
	 * If this method is not called,
	 * then the system default endian is used
	 * just like MAQ does, however if you know that the
	 * endian on this machine is different than the
	 * endian than was used to create this binary fastq file
	 * then setting this method will allow Jillion
	 * to read the file.
	 * @param endian the {@link org.jcvi.jillion.core.io.IOUtil.Endian} 
	 * to use to parse this file; can not be null.
	 * @return this
	 * @throws NullPointerException if endian is null.
	 */
	public BinaryFastqFileDataStoreBuilder endian(ByteOrder endian){
		if(endian ==null){
			throw new NullPointerException("endian can not be null");
		}
		this.endian = endian;
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
	public BinaryFastqFileDataStoreBuilder hint(DataStoreProviderHint hint){
		if(hint==null){
			throw new NullPointerException("hint can not be null");
		}
		this.hint = hint;
		return this;
	}
	
	
	/**
	 * Parse the given binary fastq file and return
	 * a new instance of a {@link FastqDataStore}
	 * using all the input parameters given so far.  
	 * If not all optional parameters are set then default
	 * values will be used:
	 * <ul>
	 * <li>
	 * If no {@link org.jcvi.jillion.core.io.IOUtil.Endian} has been specified
	 * by {@link #endian(Endian)}
	 * then they native system endian is used.
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
	 * binary fastq file.
	 * @see #endian(Endian)
	 * @see #hint(DataStoreProviderHint)
	 */
	public FastqDataStore build() throws IOException {
		
		switch(hint){
			case RANDOM_ACCESS_OPTIMIZE_SPEED:
				return DefaultBinaryFastqFileDataStore.create(fastqFile,filter, endian);
			case RANDOM_ACCESS_OPTIMIZE_MEMORY:
				return IndexedBinaryFastqFileDataStore.create(fastqFile, filter, endian);
			case ITERATION_ONLY:
				return LargeBinaryFastqFileDataStore.create(fastqFile, filter, endian);
			default:
				//can not happen
				throw new IllegalArgumentException("unknown provider hint : "+ hint);
		}
	}


	
}
