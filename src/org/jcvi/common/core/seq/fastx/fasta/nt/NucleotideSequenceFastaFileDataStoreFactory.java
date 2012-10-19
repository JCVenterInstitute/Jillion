package org.jcvi.common.core.seq.fastx.fasta.nt;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
/**
 * {@code NucleotideSequenceFastaFileDataStoreFactory}
 * is a factory class that can create new instances
 * of {@link NucleotideSequenceFastaDataStore}s
 * using data from a given input fasta file.
 * @author dkatzel
 *
 */
public final class NucleotideSequenceFastaFileDataStoreFactory {

	
	
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
	 * @param type an {@link DataStoreProviderHint} instance
	 * that explains what kind of {@link NucleotideSequenceFastaDataStore} implementation 
	 * to return; can not be null.
	 * @param filter a {@link DataStoreFilter} instance that can be
	 * used to filter out specified fasta records; can not be null. 
	 * @return a new {@link NucleotideSequenceFastaDataStore}; will never be null.
	 * @throws IOException if there is a problem parsing the fasta file.
	 * @throws NullPointerException if any input parameter is null.
	 * @throws IllegalArgumentException if the fasta file does not exist or is not readable.
	 */
	public static NucleotideSequenceFastaDataStore create(File fastaFile, DataStoreProviderHint type, DataStoreFilter filter) throws IOException{
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
			case OPTIMIZE_RANDOM_ACCESS_SPEED: return DefaultNucleotideSequenceFastaFileDataStore.create(fastaFile,filter);
			case OPTIMIZE_RANDOM_ACCESS_MEMORY: return IndexedNucleotideSequenceFastaFileDataStore.create(fastaFile,filter);
			case OPTIMIZE_ONE_PASS_ITERATION: return LargeNucleotideSequenceFastaFileDataStore.create(fastaFile,filter);
			default:
				throw new IllegalArgumentException("unknown type : "+ type);
		}
	}
	
	/**
	 * Create a new {@link NucleotideSequenceFastaDataStore} instance
	 * for the given fasta file of the given implementation type
	 * which will contain ALL the {@link NucleotideSequenceFastaRecord}s in the fasta file.
	 * This is a convenience method for
	 * {@link #create(File, DataStoreProviderHint, DataStoreFilter) create(fastaFile, type,  AcceptingDataStoreFilter.INSTANCE)}.
	 * @param fastaFile the fasta file to used to create the {@link DataStore};
	 * the file must exist and be readable and can not be null.
	 * @param type an {@link DataStoreProviderHint} instance
	 * that explains what kind of {@link NucleotideSequenceFastaDataStore} implementation 
	 * to return; can not be null.
	 * @return a new {@link NucleotideSequenceFastaDataStore}; will never be null.
	 * @throws IOException if there is a problem parsing the fasta file.
	 * @throws NullPointerException if any input parameter is null.
	 * @throws IllegalArgumentException if the fasta file does not exist or is not readable.
	 */
	public static NucleotideSequenceFastaDataStore create(File fastaFile, DataStoreProviderHint type) throws IOException{
		return create(fastaFile, type, AcceptingDataStoreFilter.INSTANCE);
	}
}
