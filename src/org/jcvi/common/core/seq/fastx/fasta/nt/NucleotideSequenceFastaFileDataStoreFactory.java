package org.jcvi.common.core.seq.fastx.fasta.nt;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreFilter;
/**
 * {@code NucleotideSequenceFastaFileDataStoreFactory}
 * is a factory class that can create new instances
 * of {@link NucleotideSequenceFastaDataStore}s
 * using data from a given input fasta file.
 * @author dkatzel
 *
 */
public final class NucleotideSequenceFastaFileDataStoreFactory {

	/**
	 * {@code FastaDataStoreType}
	 * describes implementation types
	 * that this factory can create.
	 * @author dkatzel
	 *
	 */
	public enum FastaDataStoreType{
		/**
		 * All fasta data from the fasta file is stored
		 * in a map.  This implementation allows for very fast
		 * random access but is not very 
		 * memory efficient and therefore should not be used
		 * for large fasta files.
		 */
		MAP_BACKED,
		/**
		 * The only data that is stored for the fasta is an index containing
		 * byte offsets to the various fasta records contained
		 * inside the fasta file. 
		 * <p/>
		 * This allows large files to provide random 
		 * access without taking up much memory.  The down side is each fasta record
		 * must be re-parsed each time and the fasta file must exist and not
		 * get altered during the entire lifetime of this object.
		 */
		INDEXED,
		/**
		 * This implementation doesn't store any fasta or 
		 * read information in memory.
		 * This means that each {@link DataStore#get(String)} or {@link DataStore#contains(String)}
		 * requires re-parsing the fasta file which can take some time.
		 * Other methods such as {@link DataStore#getNumberOfRecords()} are lazy-loaded
		 * and are only parsed the first time they are asked for.
		 * <p/>
		 * This implementation is ideal for use cases
		 * where the contents of the fasta file
		 * will only be read once in a single pass.
		 * For example, iterating over each fasta record only once 
		 * using {@link DataStore#iterator()}.
		 * <p/>
		 * Since each method call involves re-parsing the fasta file,
		 * that file must not be modified or moved during the
		 * entire lifetime of the instance.
		 * It is recommended that instances
		 * are wrapped by {@link CachedDataStore}.
		 */
		LARGE
		;
	}
	
	private NucleotideSequenceFastaFileDataStoreFactory(){
		//can not instantiate
	}
	/**
	 * Create a new {@link NucleotideSequenceFastaDataStore} instance
	 * for the given fasta file of the given implementation type
	 * which will only contain the {@link NucleotideSequenceFastaRecord}s specified by the given 
	 * {@link DataStoreFilter}.
	 * @param fastaFile the fasta file to used to create the {@link DataStore};
	 * the file must exist and be readable and can not be null.
	 * @param type an {@link FastaDataStoreType} instance
	 * that explains what kind of {@link NucleotideSequenceFastaDataStore} implementation 
	 * to return; can not be null.
	 * @param filter a {@link DataStoreFilter} instance that can be
	 * used to filter out specified fasta records; can not be null. 
	 * @return a new {@link NucleotideSequenceFastaDataStore}; will never be null.
	 * @throws IOException if there is a problem parsing the fasta file.
	 * @throws NullPointerException if any input parameter is null.
	 * @throws IllegalArgumentException if the fasta file does not exist or is not readable.
	 */
	public static NucleotideSequenceFastaDataStore create(File fastaFile, FastaDataStoreType type, DataStoreFilter filter) throws IOException{
		if(fastaFile==null){
			throw new NullPointerException("fasta file can not be null");
		}
		if(!fastaFile.exists()){
			throw new IllegalArgumentException("fasta file must exist");
		}
		if(!fastaFile.canRead()){
			throw new IllegalArgumentException("fasta file must be readable");
		}
		if(type==null){
			throw new NullPointerException("FastaDataStoreType can not be null");
		}
		if(filter==null){
			throw new NullPointerException("DataStoreFilter can not be null");
		}
		switch(type){
			case MAP_BACKED: return DefaultNucleotideSequenceFastaFileDataStore.create(fastaFile,filter);
			case INDEXED: return IndexedNucleotideSequenceFastaFileDataStore.create(fastaFile,filter);
			case LARGE: return LargeNucleotideSequenceFastaFileDataStore.create(fastaFile,filter);
			default:
				throw new IllegalArgumentException("unknown type : "+ type);
		}
	}
	
	/**
	 * Create a new {@link NucleotideSequenceFastaDataStore} instance
	 * for the given fasta file of the given implementation type
	 * which will contain ALL the {@link NucleotideSequenceFastaRecord}s in the fasta file.
	 * This is a convenience method for
	 * {@link #create(File, FastaDataStoreType, DataStoreFilter) create(fastaFile, type,  AcceptingDataStoreFilter.INSTANCE)}.
	 * @param fastaFile the fasta file to used to create the {@link DataStore};
	 * the file must exist and be readable and can not be null.
	 * @param type an {@link FastaDataStoreType} instance
	 * that explains what kind of {@link NucleotideSequenceFastaDataStore} implementation 
	 * to return; can not be null.
	 * @return a new {@link NucleotideSequenceFastaDataStore}; will never be null.
	 * @throws IOException if there is a problem parsing the fasta file.
	 * @throws NullPointerException if any input parameter is null.
	 * @throws IllegalArgumentException if the fasta file does not exist or is not readable.
	 */
	public static NucleotideSequenceFastaDataStore create(File fastaFile, FastaDataStoreType type) throws IOException{
		return create(fastaFile, type, AcceptingDataStoreFilter.INSTANCE);
	}
}
