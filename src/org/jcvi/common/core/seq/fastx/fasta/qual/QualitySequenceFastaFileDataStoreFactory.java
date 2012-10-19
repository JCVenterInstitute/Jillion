package org.jcvi.common.core.seq.fastx.fasta.qual;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.seq.fastx.fasta.FastaFileDataStoreType;

/**
 * {@code QualitySequenceFastaFileDataStoreFactory}
 * is a factory class that can create new instances
 * of {@link QualitySequenceFastaDataStore}s
 * using data from a given input fasta file.
 * @author dkatzel
 *
 */
public final class QualitySequenceFastaFileDataStoreFactory {

	private QualitySequenceFastaFileDataStoreFactory(){
		//can not instantiate
	}
	/**
	 * Create a new {@link QualitySequenceFastaDataStore} instance
	 * for the given fasta file of the given implementation type
	 * which will only contain the {@link QualitySequenceFastaRecord}s specified by the given 
	 * {@link DataStoreFilter}.
	 * @param fastaFile the fasta file to used to create the {@link DataStore};
	 * the file must exist and be readable and can not be null.
	 * @param type an {@link FastaFileDataStoreType} instance
	 * that explains what kind of {@link QualitySequenceFastaDataStore} implementation 
	 * to return; can not be null.
	 * @param filter a {@link DataStoreFilter} instance that can be
	 * used to filter out specified fasta records; can not be null. 
	 * @return a new {@link QualitySequenceFastaDataStore}; will never be null.
	 * @throws IOException if there is a problem parsing the fasta file.
	 * @throws NullPointerException if any input parameter is null.
	 * @throws IllegalArgumentException if the fasta file does not exist or is not readable.
	 */
	public static QualitySequenceFastaDataStore create(File fastaFile, FastaFileDataStoreType type, DataStoreFilter filter) throws IOException{
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
			case MAP_BACKED: return DefaultQualityFastaFileDataStore.create(fastaFile,filter);
			case INDEXED: return IndexedQualityFastaFileDataStore.create(fastaFile,filter);
			case LARGE: return LargeQualityFastaFileDataStore.create(fastaFile,filter);
			default:
				throw new IllegalArgumentException("unknown type : "+ type);
		}
	}
	
	/**
	 * Create a new {@link QualitySequenceFastaDataStore} instance
	 * for the given fasta file of the given implementation type
	 * which will contain ALL the {@link QualitySequenceFastaRecord}s in the fasta file.
	 * This is a convenience method for
	 * {@link #create(File, FastaFileDataStoreType, DataStoreFilter) create(fastaFile, type,  AcceptingDataStoreFilter.INSTANCE)}.
	 * @param fastaFile the fasta file to used to create the {@link DataStore};
	 * the file must exist and be readable and can not be null.
	 * @param type an {@link FastaFileDataStoreType} instance
	 * that explains what kind of {@link QualitySequenceFastaDataStore} implementation 
	 * to return; can not be null.
	 * @return a new {@link QualitySequenceFastaDataStore}; will never be null.
	 * @throws IOException if there is a problem parsing the fasta file.
	 * @throws NullPointerException if any input parameter is null.
	 * @throws IllegalArgumentException if the fasta file does not exist or is not readable.
	 */
	public static QualitySequenceFastaDataStore create(File fastaFile, FastaFileDataStoreType type) throws IOException{
		return create(fastaFile, type, AcceptingDataStoreFilter.INSTANCE);
	}
}
