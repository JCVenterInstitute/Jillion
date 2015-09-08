/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.maq.bfq;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqFileDataStoreBuilder;
import org.jcvi.jillion.trace.fastq.FastqParser;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
/**
 * {@code BfqFileDataStoreBuilder}
 * is a Builder object that can
 * build a {@link FastqDataStore} instance
 * from a Maq Binary Fastq formatted file ({@literal .bfq} )
 * using the given configuration options.
 * @author dkatzel
 *
 */
public class BfqFileDataStoreBuilder {
	/**
	 * We need to set a {@link FastqQualityCodec}
	 * in the builder to avoid validation errors
	 * but our actual parser doesn't use it.
	 * This also lets us skip having to parse
	 * the file twice to guess a codec we don't need.
	 * (If it tried, it would crash anyway since there is no codec
	 * in the binary version).
	 */
	private static final FastqQualityCodec REQUIRED_DUMMY_VALUE = FastqQualityCodec.SANGER;
	/**
	 * Our delegate fastq builder that actually
	 * has the logic of how to build datastores
	 * from fastq files with filters and provider hints.
	 */
	private final FastqFileDataStoreBuilder delegate;
	
	/**
	 * Create a new builder instance that will use
	 * the given bfq encoded file as a datasource
	 * and parse it using the system native endian.
	 * Make sure the endian matches the endian of the 
	 * machine that Maq was run on 
	 * (or matches the ByteOrder  used by the BfqFileWriterBuilder )
	 *  that produced the file.
	 * @param bfqFile the bfq file to use; 
	 * can not be null and must exist and be readable
	 * @throws IOException if there is a problem trying to
	 * access the file.
	 * @throws NullPointerException if any parameter is null.
	 */
	public BfqFileDataStoreBuilder(File bfqFile) throws IOException{
		this(BfqFileParser.create(bfqFile));
	}
	/**
	 * Create a new builder instance that will use
	 * the given {@link InputStream} of bfq encoded data as a datasource
	 * and parse it using the system native endian.
	 * Make sure the endian matches the endian of the 
	 * machine that Maq was run on 
	 * (or matches the ByteOrder  used by the BfqFileWriterBuilder )
	 *  that produced the file.
	 * @param in the {@link InputStream} to use; 
	 * can not be null and must exist and be readable
	 * @throws IOException if there is a problem trying to
	 * access the file.
	 * @throws NullPointerException if any parameter is null.
	 */
	public BfqFileDataStoreBuilder(InputStream in) throws IOException{
		this(BfqFileParser.create(in));

	}
	/**
	 * Create a new builder instance that will use
	 * the given bfq encoded file as a datasource
	 * and parse it using the given {@link ByteOrder} endian.
	 * @param bfqFile the bfq file to use; 
	 * @param endian the ByteOrder to use to parse the file. 
	 * Make sure the endian matches the endian of the 
	 * machine that Maq was run on 
	 * (or matches the ByteOrder  used by the BfqFileWriterBuilder )
	 *  that produced the file.
	 * can not be null and must exist and be readable
	 * @throws IOException if there is a problem trying to
	 * access the file.
	 * @throws NullPointerException if any parameter is null.
	 */
	public BfqFileDataStoreBuilder(File bfqFile, ByteOrder endian) throws IOException{
		this(BfqFileParser.create(bfqFile, endian));
	}
	/**
	 * Create a new builder instance that will use
	 * the given {@link InputStream} of bfq encoded data as a datasource
	 * and parse it using the given {@link ByteOrder} endian.
	 * @param in the {@link InputStream} to use; 
	 * can not be null and must exist and be readable
	 * @param endian the ByteOrder to use to parse the file. 
	 * Make sure the endian matches the endian of the 
	 * machine that Maq was run on 
	 * (or matches the ByteOrder  used by the BfqFileWriterBuilder )
	 *  that produced the file.
	 * can not be null and must exist and be readable
	 * @throws IOException if there is a problem trying to
	 * access the file.
	 * @throws NullPointerException if any parameter is null.
	 */
	public BfqFileDataStoreBuilder(InputStream in, ByteOrder endian) throws IOException{
		this(BfqFileParser.create(in, endian));

	}
	private BfqFileDataStoreBuilder(FastqParser parser) throws IOException{
		delegate = new FastqFileDataStoreBuilder(parser);
		
		delegate.qualityCodec(REQUIRED_DUMMY_VALUE);
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
	public BfqFileDataStoreBuilder filter(DataStoreFilter filter){
		delegate.filter(filter);
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
	public BfqFileDataStoreBuilder hint(DataStoreProviderHint hint){
		delegate.hint(hint);
		return this;
	}
	/**
	 * Parse the given bfq file and return
	 * a new instance of a {@link FastqDataStore}
	 * using all the input parameters given so far.  
	 * If not all optional parameters are set then default
	 * values will be used:
	 * <ul>
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
	 * @see #hint(DataStoreProviderHint)
	 * @see #filter(DataStoreFilter)
	 */
	public FastqDataStore build() throws IOException{
		return delegate.build();
	}
}
