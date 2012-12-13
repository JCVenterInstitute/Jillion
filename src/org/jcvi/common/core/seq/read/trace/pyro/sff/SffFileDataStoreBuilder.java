package org.jcvi.common.core.seq.read.trace.pyro.sff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreFilters;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.seq.read.trace.pyro.Flowgram;
import org.jcvi.common.core.seq.read.trace.pyro.FlowgramDataStore;
import org.jcvi.common.core.seq.trace.fastq.FastqRecord;
import org.jcvi.common.core.util.Builder;
/**
 * {@code SffFileDataStoreBuilder}
 * is a {@link Builder} that can create new instances
 * of {@link FlowgramDataStore}s
 * using data from a given input sff file.
 * @author dkatzel
 *
 */
public class SffFileDataStoreBuilder {
	private final File fastqFile;
	
	private DataStoreFilter filter = DataStoreFilters.alwaysAccept();
	//by default store everything in memory
	private DataStoreProviderHint hint = DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_SPEED;
	
	/**
	 * Create a new instance of {@code SffFileDataStoreBuilder}
	 * which will build a {@link FlowgramDataStore} for the given
	 * sff file.
	 * @param sffFile the sff file make a {@link FlowgramDataStore} with. 
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
		this.fastqFile = sffFile;
	}
	/**
	 * Only include the {@link FastqRecord}s which pass
	 * the given {@link DataStoreFilter}.  If a filter
	 * is not given to this builder, then all records
	 * in the sff file will be included in the built
	 * {@link FlowgramDataStore}.
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
	 * which {@link FlowgramDataStore} implementation to chose
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
	 * a new instance of a {@link FlowgramDataStore}
	 * using all the input parameters given so far.  
	 * If not all optional parameters are set then default
	 * values will be used:
	 * <ul>
	 * <li>
	 * If no {@link DataStoreFilter} has been specified
	 * by {@link #filter(DataStoreFilter)},
	 * then all {@link Flowgram}s will be included in this {@link FlowgramDataStore}.
	 * </li>
	 * <li>
	 * If no {@link DataStoreProviderHint} has been specified
	 * by {@link #hint(DataStoreProviderHint)},
	 * then this builder will try to store all the 
	 * {@link Flowgram}s that meet the {@link DataStoreFilter}
	 * requirements in memory.  This may cause out of memory errors
	 * if there is not enough memory available.
	 * </li>
	 * </ul>
	 * @return a new {@link FlowgramDataStore} instance;
	 * never null.
	 * @throws IOException if there is a problem parsing the 
	 * sff file. 
	 * @see #hint(DataStoreProviderHint)
	 */
	public FlowgramDataStore build() throws IOException {
		switch(hint){
			case OPTIMIZE_RANDOM_ACCESS_SPEED:
				return DefaultSffFileDataStore.create(fastqFile,filter);
			case OPTIMIZE_RANDOM_ACCESS_MEMORY:
				return IndexedSffFileDataStore.create(fastqFile, filter);
			case OPTIMIZE_ITERATION:
				return LargeSffFileDataStore.create(fastqFile, filter);
			default:
				//can not happen
				throw new IllegalArgumentException("unknown provider hint : "+ hint);
		}
	}
}
