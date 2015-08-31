package org.jcvi.jillion.trace.fastq;
/**
 * {@link FastqFileDataStore} is a {@link FastqDataStore}
 * where all the {@link FastqRecord}s in the datastore
 * belong to the same {@code .fastq} file.
 * @author dkatzel
 * @since 5.0
 */
public interface FastqFileDataStore extends FastqDataStore{
    /**
     * Get the {@link FastqQualityCodec} that was 
     * used to encode all the {@link FastqRecord}s
     * in this file.  This is useful for when
     * processing a fastq file and you want any output
     * fastq files to use the same fastq quality codec
     * as the input files.
     * 
     * @return a {@link FastqQualityCodec}; may be null
     * if there are no records in the fastq file
     * depending on the implementation of the {@link FastqDataStore}.
     */
    FastqQualityCodec getQualityCodec();
}
