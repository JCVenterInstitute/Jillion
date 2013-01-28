package org.jcvi.jillion.assembly.ctg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.ace.AceContig;
import org.jcvi.jillion.assembly.ace.AceFileContigDataStore;
import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideSequenceFastaRecord;

public class TigrContigFileDataStoreBuilder {
	private final File contigFile;
	
	private DataStoreFilter filter = DataStoreFilters.alwaysAccept();
	//by default store everything in memory
	private DataStoreProviderHint hint = DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_SPEED;
	
	private final DataStore<Long> fullSeqLengthDataStore;
	
	/**
	 * Create a new instance of {@code TigrContigFileDataStoreBuilder}
	 * which will build a {@link TigrContigDataStore} for the given
	 * contig file.
	 * @param contigFile the ace file make a {@link TigrContigDataStore} with.
	 * @param  fullLengthSequenceDataStore a datastore containing the full length sequences
	 * of all the input reads that are assembled into the contig file.  This datastore
	 * is used to extract the full ungapped length for each read to correctly populate the 
	 * {@link AssembledRead#getReadInfo()} instance since not all of that
	 * information is stored directly in the contig file. Each read id in the contigs 
	 * must have a corresponding record in this sequence datastore. This sequence datastore
	 * may contain additional records that did not make it into the contig file.
	 * @throws IOException if the ace file does not exist, or can not be read.
	 * @throws NullPointerException if either contigFile or fullLengthSequenceDataStore are null.
	 */
	public TigrContigFileDataStoreBuilder(File contigFile, NucleotideSequenceFastaDataStore fullLengthSequenceDataStore) throws IOException{
		if(contigFile ==null){
			throw new NullPointerException("contig file can not be null");
		}
		if(!contigFile.exists()){
			throw new FileNotFoundException("contig file must exist");
		}
		if(!contigFile.canRead()){
			throw new IOException("contig file is not readable");
		}
		if(fullLengthSequenceDataStore ==null){
			throw new NullPointerException("sequence datastore can not be null");
		}
		this.contigFile = contigFile;
		this.fullSeqLengthDataStore = adapt(fullLengthSequenceDataStore);
	}
	/**
	 * Create a new instance of {@code TigrContigFileDataStoreBuilder}
	 * which will build a {@link TigrContigDataStore} for the given
	 * contig file.
	 * @param contigFile the ace file make a {@link TigrContigDataStore} with.
	 * @param  fullLengthSequenceDataStore a datastore containing the full length sequences
	 * of all the input reads that are assembled into the contig file.  This datastore
	 * is used to extract the full ungapped length for each read to correctly populate the 
	 * {@link AssembledRead#getReadInfo()} instance since not all of that
	 * information is stored directly in the contig file. Each read id in the contigs 
	 * must have a corresponding record in this sequence datastore. This sequence datastore
	 * may contain additional records that did not make it into the contig file.
	 * @throws IOException if the ace file does not exist, or can not be read.
	 * @throws NullPointerException if either contigFile or fullLengthSequenceDataStore are null.
	 */
	public TigrContigFileDataStoreBuilder(File contigFile, NucleotideSequenceDataStore fullLengthSequenceDataStore) throws IOException{
		if(contigFile ==null){
			throw new NullPointerException("contig file can not be null");
		}
		if(!contigFile.exists()){
			throw new FileNotFoundException("contig file must exist");
		}
		if(!contigFile.canRead()){
			throw new IOException("contig file is not readable");
		}
		if(fullLengthSequenceDataStore ==null){
			throw new NullPointerException("sequence datastore can not be null");
		}
		this.contigFile = contigFile;
		this.fullSeqLengthDataStore = adapt(fullLengthSequenceDataStore);
	}

    @SuppressWarnings("unchecked")
	private DataStore<Long> adapt(NucleotideSequenceFastaDataStore fullLengthSequenceDataStore){
    	return (DataStore<Long>)DataStoreUtil.adapt(DataStore.class, fullLengthSequenceDataStore, 
    			new DataStoreUtil.AdapterCallback<NucleotideSequenceFastaRecord, Long>() {

					@Override
					public Long get(NucleotideSequenceFastaRecord from) {
						return from.getSequence().getUngappedLength();
					}
    		
		});
    }
    
    @SuppressWarnings("unchecked")
	private DataStore<Long> adapt(NucleotideSequenceDataStore fullLengthSequenceDataStore){
    	return (DataStore<Long>)DataStoreUtil.adapt(DataStore.class, fullLengthSequenceDataStore, 
    			new DataStoreUtil.AdapterCallback<NucleotideSequence, Long>() {

					@Override
					public Long get(NucleotideSequence from) {
						return from.getUngappedLength();
					}
    		
		});
    }
	
	/**
	 * Only include the {@link AceContig}s which pass
	 * the given {@link DataStoreFilter}.  If a filter
	 * is not given to this builder, then all records
	 * in the ace file will be included in the built
	 * {@link AceFileContigDataStore}.
	 * @param filter a {@link DataStoreFilter} instance that can be
	 * used to filter out specified {@link AceContig}s; can not be null. 
	 * @return this.
	 * @throws NullPointerException if filter is null.
	 */
	public TigrContigFileDataStoreBuilder filter(DataStoreFilter filter){
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
	 * try to store all the {@link AceContig} records in memory
	 * which may cause an Exception if there isn't enough memory.
	 * The {@link DataStoreProviderHint}  is just a guideline 
	 * and may be ignored by this builder when determining
	 * which {@link AceFileContigDataStore} implementation to chose
	 * to build in {@link #build()}.
	 * @param hint an instance of {@link DataStoreProviderHint};
	 * can not be null.
	 * @return this
	 * @throws NullPointerException if hint is null.
	 * @see DataStoreProviderHint
	 */
	public TigrContigFileDataStoreBuilder hint(DataStoreProviderHint hint){
		if(hint==null){
			throw new NullPointerException("hint can not be null");
		}
		this.hint = hint;
		return this;
	}
	
	
	/**
	 * Parse the given ace file and return
	 * a new instance of a {@link AceFileContigDataStore}
	 * using all the input parameters given so far.  
	 * If not all optional parameters are set then default
	 * values will be used:
	 * <ul>
	 * <li>
	 * If no {@link DataStoreFilter} has been specified
	 * by {@link #filter(DataStoreFilter)},
	 * then all {@link AceContig}s will be included in this {@link AceFileContigDataStore}.
	 * </li>
	 * <li>
	 * If no {@link DataStoreProviderHint} has been specified
	 * by {@link #hint(DataStoreProviderHint)},
	 * then this builder will try to store all the 
	 * {@link AceContig}s that meet the {@link DataStoreFilter}
	 * requirements in memory.  This may cause out of memory errors
	 * if there is not enough memory available.
	 * </li>
	 * </ul>
	 * @return a new {@link AceFileContigDataStore} instance;
	 * never null.
	 * @throws IOException if there is a problem parsing the 
	 * ace file. 
	 * @see #hint(DataStoreProviderHint)
	 */
	public TigrContigDataStore build() throws IOException {
		switch(hint){
		case OPTIMIZE_RANDOM_ACCESS_SPEED:
				return DefaultTigrContigFileDataStore.create(contigFile, fullSeqLengthDataStore, filter);
		case OPTIMIZE_RANDOM_ACCESS_MEMORY: return IndexedTigrContigFileDataStore.create(contigFile,fullSeqLengthDataStore, filter);
		case OPTIMIZE_ITERATION: return new LargeTigrContigFileDataStore(contigFile, fullSeqLengthDataStore, filter);
			default:
				//can not happen
				throw new IllegalArgumentException("unknown provider hint : "+ hint);
		}
	}
}
