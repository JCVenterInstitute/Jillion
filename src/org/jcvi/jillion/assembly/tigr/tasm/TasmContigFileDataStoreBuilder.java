/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.tigr.tasm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreFilter;
import org.jcvi.jillion.core.datastore.DataStoreFilters;
import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaDataStore;
import org.jcvi.jillion.fasta.nt.NucleotideFastaRecord;

public class TasmContigFileDataStoreBuilder {
	private final File tasmFile;
	
	private DataStoreFilter filter = DataStoreFilters.alwaysAccept();
	//by default store everything in memory
	private DataStoreProviderHint hint = DataStoreProviderHint.RANDOM_ACCESS_OPTIMIZE_SPEED;
	
	private final DataStore<Long> fullSeqLengthDataStore;
	
	/**
	 * Create a new instance of {@code TigrContigFileDataStoreBuilder}
	 * which will build a {@link TigrContigDataStore} for the given
	 * tasm file.
	 * @param tasmFile the tasm file make a {@link TigrContigDataStore} with.
	 * @param  fullLengthSequenceDataStore a datastore containing the full length sequences
	 * of all the input reads that are assembled into the contig file.  This datastore
	 * is used to extract the full ungapped length for each read to correctly populate the 
	 * {@link AssembledRead#getReadInfo()} instance since not all of that
	 * information is stored directly in the contig file. Each read id in the contigs 
	 * must have a corresponding record in this sequence datastore. This sequence datastore
	 * may contain additional records that did not make it into the contig file.
	 * @throws IOException if the tasm file does not exist, or can not be read.
	 * @throws NullPointerException if either tasmFile or fullLengthSequenceDataStore are null.
	 */
	public TasmContigFileDataStoreBuilder(File tasmFile, NucleotideFastaDataStore fullLengthSequenceDataStore) throws IOException{
		if(tasmFile ==null){
			throw new NullPointerException("tasm file can not be null");
		}
		if(!tasmFile.exists()){
			throw new FileNotFoundException("tasm file must exist");
		}
		if(!tasmFile.canRead()){
			throw new IOException("contig file is not readable");
		}
		if(fullLengthSequenceDataStore ==null){
			throw new NullPointerException("sequence datastore can not be null");
		}
		this.tasmFile = tasmFile;
		this.fullSeqLengthDataStore = adapt(fullLengthSequenceDataStore);
	}
	/**
	 * Create a new instance of {@code TigrContigFileDataStoreBuilder}
	 * which will build a {@link TigrContigDataStore} for the given
	 * contig file.
	 * @param tasmFile the tasm file make a {@link TigrContigDataStore} with.
	 * @param  fullLengthSequenceDataStore a datastore containing the full length sequences
	 * of all the input reads that are assembled into the contig file.  This datastore
	 * is used to extract the full ungapped length for each read to correctly populate the 
	 * {@link AssembledRead#getReadInfo()} instance since not all of that
	 * information is stored directly in the contig file. Each read id in the contigs 
	 * must have a corresponding record in this sequence datastore. This sequence datastore
	 * may contain additional records that did not make it into the contig file.
	 * @throws IOException if the tasm file does not exist, or can not be read.
	 * @throws NullPointerException if either tasmFile or fullLengthSequenceDataStore are null.
	 */
	public TasmContigFileDataStoreBuilder(File tasmFile, NucleotideSequenceDataStore fullLengthSequenceDataStore) throws IOException{
		if(tasmFile ==null){
			throw new NullPointerException("tasm file can not be null");
		}
		if(!tasmFile.exists()){
			throw new FileNotFoundException("tasm file must exist");
		}
		if(!tasmFile.canRead()){
			throw new IOException("tasm file is not readable");
		}
		if(fullLengthSequenceDataStore ==null){
			throw new NullPointerException("sequence datastore can not be null");
		}
		this.tasmFile = tasmFile;
		this.fullSeqLengthDataStore = adapt(fullLengthSequenceDataStore);
	}

    @SuppressWarnings("unchecked")
	private DataStore<Long> adapt(NucleotideFastaDataStore fullLengthSequenceDataStore){
    	return (DataStore<Long>)DataStoreUtil.adapt(DataStore.class, fullLengthSequenceDataStore, 
    			new DataStoreUtil.AdapterCallback<NucleotideFastaRecord, Long>() {

					@Override
					public Long get(NucleotideFastaRecord from) {
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
	 * Only include the {@link TasmContig}s which pass
	 * the given {@link DataStoreFilter}.  If a filter
	 * is not given to this builder, then all records
	 * in the tasm file will be included in the built
	 * {@link TasmContigDataStore}.
	 * @param filter a {@link DataStoreFilter} instance that can be
	 * used to filter out specified {@link TasmContig}s; can not be null. 
	 * @return this.
	 * @throws NullPointerException if filter is null.
	 */
	public TasmContigFileDataStoreBuilder filter(DataStoreFilter filter){
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
	 * try to store all the {@link TasmContig} records in memory
	 * which may cause an Exception if there isn't enough memory.
	 * The {@link DataStoreProviderHint}  is just a guideline 
	 * and may be ignored by this builder when determining
	 * which {@link TasmContigDataStore} implementation to chose
	 * to build in {@link #build()}.
	 * @param hint an instance of {@link DataStoreProviderHint};
	 * can not be null.
	 * @return this
	 * @throws NullPointerException if hint is null.
	 * @see DataStoreProviderHint
	 */
	public TasmContigFileDataStoreBuilder hint(DataStoreProviderHint hint){
		if(hint==null){
			throw new NullPointerException("hint can not be null");
		}
		this.hint = hint;
		return this;
	}
	
	
	/**
	 * Parse the given tasm file and return
	 * a new instance of a {@link TasmContigDataStore}
	 * using all the input parameters given so far.  
	 * If not all optional parameters are set then default
	 * values will be used:
	 * <ul>
	 * <li>
	 * If no {@link DataStoreFilter} has been specified
	 * by {@link #filter(DataStoreFilter)},
	 * then all {@link TasmContig}s will be included in this {@link TasmContigDataStore}.
	 * </li>
	 * <li>
	 * If no {@link DataStoreProviderHint} has been specified
	 * by {@link #hint(DataStoreProviderHint)},
	 * then this builder will try to store all the 
	 * {@link TasmContig}s that meet the {@link DataStoreFilter}
	 * requirements in memory.  This may cause out of memory errors
	 * if there is not enough memory available.
	 * </li>
	 * </ul>
	 * @return a new {@link TasmContigDataStore} instance;
	 * never null.
	 * @throws IOException if there is a problem parsing the 
	 * tasm file. 
	 * @see #hint(DataStoreProviderHint)
	 */
	public TasmContigDataStore build() throws IOException {
		switch(hint){
		case RANDOM_ACCESS_OPTIMIZE_SPEED:
			return DefaultTasmFileContigDataStore.create(tasmFile, fullSeqLengthDataStore, filter);
	case RANDOM_ACCESS_OPTIMIZE_MEMORY: return IndexedTasmFileDataStore.create(tasmFile,fullSeqLengthDataStore, filter);
	case ITERATION_ONLY: return new LargeTasmContigFileDataStore(tasmFile, fullSeqLengthDataStore, filter);
		default:
			//can not happen
			throw new IllegalArgumentException("unknown provider hint : "+ hint);
		}
	}
}
