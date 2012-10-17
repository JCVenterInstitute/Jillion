package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreFilter;
/**
 * {@code AceFileContigDataStoreFactory} is a 
 * factory class that creates new instances
 * of {@link AceFileContigDataStore}s.
 * @author dkatzel
 *
 */
public final class AceFileContigDataStoreFactory {
	/**
	 * {@code AceFileDataStoreType}
	 * describes implementation types
	 * that this factory can create.
	 * @author dkatzel
	 *
	 */
	public enum AceFileDataStoreType{
		/**
		 * All contig data from the ace file is stored
		 * in a map.  This implementation allows for very fast
		 * random access but is not very 
		 * memory efficient and therefore should not be used
		 * for large ace files.
		 */
		MAP_BACKED,
		/**
		 * The only data that is stored for the contigs is an index containing
		 * byte offsets to the various contigs contained
		 * inside the ace file. Furthermore, each {@link AceContig}
		 * in this datastore will not store all the underlying read
		 * data in memory either.  Calls to {@link AceContig#getRead(String)} may
		 * cause part of the ace file to be re-parsed in order to retrieve
		 * any missing information.  
		 * <p/>
		 * This allows large files to provide random 
		 * access without taking up much memory.  The down side is each contig
		 * must be re-parsed each time and the ace file must exist and not
		 * get altered during the entire lifetime of this object.
		 */
		INDEXED,
		/**
		 * This implementation doesn't store any contig or 
		 * read information in memory.
		 * This means that each {@link DataStore#get(String)} or {@link DataStore#contains(String)}
		 * requires re-parsing the ace file which can take some time.
		 * Other methods such as {@link #getNumberOfRecords()} are lazy-loaded
		 * and are only parsed the first time they are asked for.
		 * <p/>
		 * This implementation is ideal for use cases
		 * where the contents of the ace file
		 * will only be read once in a single pass.
		 * For example, iterating over each contig only once 
		 * using {@link DataStore#iterator()}.
		 * <p/>
		 * Since each method call involves re-parsing the ace file,
		 * that file must not be modified or moved during the
		 * entire lifetime of the instance.
		 * It is recommended that instances
		 * are wrapped by {@link CachedDataStore}.
		 */
		LARGE
		;
	}
	
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
	 * @param type an {@link AceFileDataStoreType} instance
	 * that explains what kind of {@link AceFileContigDataStore} implementation 
	 * to return; can not be null.
	 * @param filter a {@link DataStoreFilter} instance that can be
	 * used to filter out specified contigs; can not be null. 
	 * @return a new {@link AceFileContigDataStore}; will never be null.
	 * @throws IOException if there is a problem parsing the ace file.
	 * @throws NullPointerException if any input parameter is null.
	 * @throws IllegalArgumentException if the ace file does not exist or is not readable.
	 */
	public static AceFileContigDataStore create(File aceFile,AceFileDataStoreType type,DataStoreFilter filter) throws IOException{
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
			case MAP_BACKED: return DefaultAceFileDataStore.create(aceFile,filter);
			case INDEXED: return IndexedAceFileDataStore.create(aceFile,filter);
			case LARGE: return LargeAceFileDataStore.create(aceFile,filter);
			default:
				throw new IllegalArgumentException("unknown type : "+ type);
		}
		
	}
	
	/**
	 * Create a new {@link AceFileContigDataStore} instance
	 * for the given ace file of the given implementation type.
	 * This is a convenience method for
	 * {@link #create(File, AceFileDataStoreType, DataStoreFilter) create(aceFile, type, AcceptingDataStoreFilter.INSTANCE}.
	 * @param aceFile the ace file to used to create the {@link DataStore};
	 * the file must exist and can not be null.
	 * @param type an {@link AceFileDataStoreType} instance
	 * that explains what kind of {@link AceFileContigDataStore} implementation 
	 * to return; can not be null.
	 * @return a new {@link AceFileContigDataStore}; will never be null.
	 * @throws IOException if there is a problem parsing the ace file.
	 * @throws NullPointerException if any input parameter is null.
	 * @throws IllegalArgumentException if the ace file does not exist or is not readable.
	 */
	public static AceFileContigDataStore create(File aceFile,AceFileDataStoreType type) throws IOException{
		return create(aceFile, type, AcceptingDataStoreFilter.INSTANCE);		
	}
	
}
