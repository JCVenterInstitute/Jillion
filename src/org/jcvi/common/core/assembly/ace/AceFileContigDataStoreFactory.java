package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
/**
 * {@code AceFileContigDataStoreFactory} is a 
 * factory class that creates new instances
 * of {@link AceFileContigDataStore}s.
 * The order of {@link AceFileContigDataStore#iterator()}
 * and {@link AceFileContigDataStore#idIterator()}
 * is the order of that the contigs appear 
 * in the ace file.
 * @author dkatzel
 *
 */
public final class AceFileContigDataStoreFactory {
	
	private AceFileContigDataStoreFactory(){
		//can not instantiate
	}
	/**
	 * Create a new {@link AceFileContigDataStore} instance
	 * for the given ace file of the given implementation type
	 * which only contains contigs specified by the given 
	 * {@link DataStoreFilter}.
	 * @param aceFile the ace file to used to create the {@link DataStore};
	 * the file must exist and can not be null.
	 * @param type an {@link DataStoreProviderHint} instance
	 * that explains what kind of {@link AceFileContigDataStore} implementation 
	 * to return; can not be null.
	 * @param filter a {@link DataStoreFilter} instance that can be
	 * used to filter out specified contigs; can not be null. 
	 * @return a new {@link AceFileContigDataStore}; will never be null.
	 * @throws IOException if there is a problem parsing the ace file.
	 * @throws NullPointerException if any input parameter is null.
	 * @throws IllegalArgumentException if the ace file does not exist or is not readable.
	 */
	public static AceFileContigDataStore create(File aceFile,DataStoreProviderHint type,DataStoreFilter filter) throws IOException{
		if(aceFile==null){
			throw new NullPointerException("ace file can not be null");
		}
		if(!aceFile.exists()){
			throw new IllegalArgumentException("ace file must exist");
		}
		if(!aceFile.canRead()){
			throw new IllegalArgumentException("ace file must be readable");
		}
		if(type==null){
			throw new NullPointerException("AceFileDataStoreType can not be null");
		}
		if(filter==null){
			throw new NullPointerException("DataStoreFilter can not be null");
		}
		switch(type){
			case OPTIMIZE_RANDOM_ACCESS_SPEED: return DefaultAceFileDataStore.create(aceFile,filter);
			case OPTIMIZE_RANDOM_ACCESS_MEMORY: return IndexedAceFileDataStore.create(aceFile,filter);
			case OPTIMIZE_ITERATION: return LargeAceFileDataStore.create(aceFile,filter);
			default:
				throw new IllegalArgumentException("unknown type : "+ type);
		}
		
	}
	
	/**
	 * Create a new {@link AceFileContigDataStore} instance
	 * for the given ace file of the given implementation type.
	 * This is a convenience method for
	 * {@link #create(File, DataStoreProviderHint, DataStoreFilter) create(aceFile, type, AcceptingDataStoreFilter.INSTANCE}.
	 * @param aceFile the ace file to used to create the {@link DataStore};
	 * the file must exist and can not be null.
	 * @param type an {@link DataStoreProviderHint} instance
	 * that explains what kind of {@link AceFileContigDataStore} implementation 
	 * to return; can not be null.
	 * @return a new {@link AceFileContigDataStore}; will never be null.
	 * @throws IOException if there is a problem parsing the ace file.
	 * @throws NullPointerException if any input parameter is null.
	 * @throws IllegalArgumentException if the ace file does not exist or is not readable.
	 */
	public static AceFileContigDataStore create(File aceFile,DataStoreProviderHint type) throws IOException{
		return create(aceFile, type, AcceptingDataStoreFilter.INSTANCE);		
	}
	
}
