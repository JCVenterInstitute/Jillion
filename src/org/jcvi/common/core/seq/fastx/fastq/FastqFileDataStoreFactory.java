package org.jcvi.common.core.seq.fastx.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.datastore.AcceptingDataStoreFilter;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreFilter;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
/**
 * {@code FastqFileDataStoreFactory}
 * is a factory class that can create new instances
 * of {@link FastqDataStore}s
 * using data from a given input fastq file.
 * @author dkatzel
 *
 */
public final class FastqFileDataStoreFactory {
	
	private FastqFileDataStoreFactory(){
		//can not instantiate
	}
	/**
	 * Create a new {@link FastqDataStore} instance
	 * for the given fastq file using the given {@link DataStoreProviderHint}
	 * as an implementation guide
	 * which will contain all the {@link FastqRecord}s in the file.
	 * The {@link FastqQualityCodec} is auto-detected at the price of 
	 * a one time performance penalty by parsing the first few thousand records
	 * twice in order to detect the range of quality values seen.
	 * @param fastqFile the fastq file to used to create the {@link DataStore};
	 * the file must exist and be readable and can not be null.
	 * @param providerHint an {@link DataStoreProviderHint} instance
	 * that explains what kind of {@link FastqDataStore} implementation 
	 * to return; can not be null. 
	 * 
	 * @return a new {@link FastqDataStore}; will never be null.
	 * @throws IOException if there is a problem parsing the fastq file.
	 * @throws NullPointerException if any input parameter is null.
	 * @throws IllegalArgumentException if the fastq file does not exist or is not readable.
	 * @throws IllegalArgumentException if the encoded qualities are decoded into 
	 * invalid phred scores using the auto-detected {@link FastqQualityCodec}.  This implies
	 * the wrong codec was detected or the file was incorrectly encoded.
	 */
	public static FastqDataStore create(File fastqFile, DataStoreProviderHint providerHint) throws IOException{
		FastqQualityCodec codec = FastqUtil.guessQualityCodecUsed(fastqFile);
		return create(fastqFile, providerHint, codec);
	}
	/**
	 * Create a new {@link FastqDataStore} instance
	 * for the given fastq file using the given {@link DataStoreProviderHint}
	 * as an implementation guide
	 * which will contain all the {@link FastqRecord}s in the file.
	 * @param fastqFile the fastq file to used to create the {@link DataStore};
	 * the file must exist and be readable and can not be null.
	 * @param providerHint an {@link DataStoreProviderHint} instance
	 * that explains what kind of {@link FastqDataStore} implementation 
	 * to return; can not be null.
	 * @param qualityCodec the {@link FastqQualityCodec} that should
	 * be used to decode the encoded qualities of each record in the file;
	 * can not be null.  If the given codec is not the same
	 * codec that was used to encode the file, then incorrect
	 * quality values might silently decode 
	 * the wrong (possibly higher or lower) quality values or
	 * cause an {@link IllegalArgumentException}
	 * to be thrown if the incorrectly decoded quality values are not
	 * valid phred scores.
	 * @return a new {@link FastqDataStore}; will never be null.
	 * @throws IOException if there is a problem parsing the fastq file.
	 * @throws NullPointerException if any input parameter is null.
	 * @throws IllegalArgumentException if the fastq file does not exist or is not readable.
	 * @throws IllegalArgumentException if the encoded qualities are decoded into 
	 * invalid phred scores using the given {@link FastqQualityCodec}.  This implies
	 * the wrong codec was given or the file was incorrectly encoded.
	 */
	public static FastqDataStore create(File fastqFile, DataStoreProviderHint providerHint, FastqQualityCodec qualityCodec) throws IOException{
		return create(fastqFile, providerHint, qualityCodec, AcceptingDataStoreFilter.INSTANCE);
	}
	/**
	 * Create a new {@link FastqDataStore} instance
	 * for the given fastq file using the given {@link DataStoreProviderHint}
	 * as an implementation guide
	 * which will only contain the {@link FastqRecord}s specified by the given 
	 * {@link DataStoreFilter}.
	 * @param fastqFile the fastq file to used to create the {@link DataStore};
	 * the file must exist and be readable and can not be null.
	 * @param providerHint an {@link DataStoreProviderHint} instance
	 * that explains what kind of {@link FastqDataStore} implementation 
	 * to return; can not be null.
	 * @param qualityCodec the {@link FastqQualityCodec} that should
	 * be used to decode the encoded qualities of each record in the file;
	 * can not be null.  If the given codec is not the same
	 * codec that was used to encode the file, then incorrect
	 * quality values might silently decode 
	 * the wrong (possibly higher or lower) quality values or
	 * cause an {@link IllegalArgumentException}
	 * to be thrown if the incorrectly decoded quality values are not
	 * valid phred scores.
	 * 
	 * @param filter a {@link DataStoreFilter} instance that can be
	 * used to filter out specified fastq records; can not be null. 
	 * @return a new {@link FastqDataStore}; will never be null.
	 * @throws IOException if there is a problem parsing the fastq file.
	 * @throws NullPointerException if any input parameter is null.
	 * @throws IllegalArgumentException if the fastq file does not exist or is not readable.
	 * @throws IllegalArgumentException if the encoded qualities are decoded into 
	 * invalid phred scores using the given {@link FastqQualityCodec}.  This implies
	 * the wrong codec was given or the file was incorrectly encoded.
	 */
	public static FastqDataStore create(File fastqFile, DataStoreProviderHint providerHint, FastqQualityCodec qualityCodec,  DataStoreFilter filter) throws IOException{
		if(providerHint ==null){
			throw new NullPointerException("DataStoreProviderHint can not be null");
		}
		if(fastqFile==null){
			throw new NullPointerException("fastq file can not be null");
		}
		if(!fastqFile.exists()){
			throw new IllegalArgumentException("fastq file must exist");
		}
		if(!fastqFile.canRead()){
			throw new IllegalArgumentException("fastq file must be readable");
		}
		if(filter==null){
			throw new NullPointerException("datastore filter can not be null");
		}
		if(qualityCodec==null){
			throw new NullPointerException("quality codec can not be null");
		}
		switch(providerHint){
			case OPTIMIZE_RANDOM_ACCESS_SPEED:
				return DefaultFastqFileDataStore.create(fastqFile,filter, qualityCodec);
			case OPTIMIZE_RANDOM_ACCESS_MEMORY:
				return IndexedFastqFileDataStore.create(fastqFile, qualityCodec, filter);
			case OPTIMIZE_ITERATION:
				return LargeFastqFileDataStore.create(fastqFile, filter, qualityCodec);
			default:
				throw new IllegalArgumentException("unknown provider hint : "+ providerHint);
		}
	}
}
