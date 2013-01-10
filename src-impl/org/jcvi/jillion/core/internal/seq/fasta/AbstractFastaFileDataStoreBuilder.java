package org.jcvi.jillion.core.internal.seq.fasta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreFilters;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.seq.fasta.FastaDataStore;
import org.jcvi.common.core.seq.fasta.FastaRecord;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.Symbol;

public abstract class AbstractFastaFileDataStoreBuilder<T extends Symbol, S extends Sequence<T>, F extends FastaRecord<T,S>, D extends FastaDataStore<T,S, F>> {

	protected final File fastaFile;
	private DataStoreFilter filter = DataStoreFilters.alwaysAccept();
	private DataStoreProviderHint hint = DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_SPEED;
	/**
	 * Create a new Builder instance of 
	 * which will build a {@link FastaDataStore} for the given
	 * fasta file.
	 * @param fastaFile the fasta file make a {@link FastaDataStore} with. 
	 * @throws IOException if the fasta file does not exist, or can not be read.
	 * @throws NullPointerException if fastaFile is null.
	 */
	protected AbstractFastaFileDataStoreBuilder(File fastaFile) throws IOException{
		if(fastaFile ==null){
			throw new NullPointerException("fasta file can not be null");
		}
		if(!fastaFile.exists()){
			throw new FileNotFoundException("fasta file must exist");
		}
		if(!fastaFile.canRead()){
			throw new IOException("fasta file is not readable");
		}
		this.fastaFile = fastaFile;
	}
	
	/**
	 * Only include the {@link FastaRecord}s which pass
	 * the given {@link DataStoreFilter}.  If a filter
	 * is not given to this builder, then all records
	 * in the fastq file will be included in the built
	 * {@link FastaDataStore}.
	 * @param filter a {@link DataStoreFilter} instance that can be
	 * used to filter out specified fasta records; can not be null. 
	 * @return this.
	 * @throws NullPointerException if filter is null.
	 */
	protected AbstractFastaFileDataStoreBuilder<T, S, F, D> filter(DataStoreFilter filter) {
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
	 * try to store all the fasta records in memory
	 * which may cause an Exception if there isn't enough memory.
	 * The {@link DataStoreProviderHint}  is just a guideline 
	 * and may be ignored by this builder when determining
	 * which {@link FastaDataStore} implementation to chose
	 * to build in {@link #build()}.
	 * @param hint an instance of {@link DataStoreProviderHint};
	 * can not be null.
	 * @return this
	 * @throws NullPointerException if hint is null.
	 * @see DataStoreProviderHint
	 */
	protected AbstractFastaFileDataStoreBuilder<T, S, F, D> hint(DataStoreProviderHint hint) {
		if(hint==null){
			throw new NullPointerException("hint can not be null");
		}
		this.hint = hint;
		return this;
	}

	/**
	 * Parse the given fasta file and return
	 * a new instance of a {@link FastaDataStore}
	 * using all the input parameters given so far.  
	 * If not all optional parameters are set then default
	 * values will be used:
	 * <ul>
	 * <li>
	 * If no {@link DataStoreFilter} has been specified
	 * by {@link #filter(DataStoreFilter)},
	 * then all {@link FastaRecord}s will be included in this {@link FastaDataStore}.
	 * </li>
	 * <li>
	 * If no {@link DataStoreProviderHint} has been specified
	 * by {@link #hint(DataStoreProviderHint)},
	 * then this builder will try to store all the 
	 * {@link FastaRecord}s that meet the {@link DataStoreFilter}
	 * requirements in memory.  This may cause out of memory errors
	 * if there is not enough memory available.
	 * </li>
	 * </ul>
	 * @return a new {@link FastaDataStore} instance;
	 * never null.
	 * @throws IOException if there is a problem parsing the 
	 * fasta file.
	 * @see #hint(DataStoreProviderHint)
	 */
	protected D build() throws IOException {
		return createNewInstance(fastaFile, hint, filter);
	}

	/**
	 * Create a new {@link FastaDataStore} instance.
	 * @param fastaFile the fasta file to make the datastore for;
	 * can not be null and should exist.
	 * @param hint a {@link DataStoreProviderHint}; will never be null.
	 * @param filter a {@link DataStoreFilter}; will never be null.
	 * @return a new {@link FastaDataStore} instance; should never be null.
	 * @throws IOException if there is a problem creating the datastore from the file.
	 */
	protected abstract D createNewInstance(File fastaFile, DataStoreProviderHint hint, DataStoreFilter filter) throws IOException;
			



}