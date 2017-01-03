package org.jcvi.jillion.trace.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.Pair;
import org.jcvi.jillion.core.util.ThrowingStream;
/**
 * Helper class to simplify the process of reading Fastq encoded files
 * to just get the Stream of {@link FastqRecord} objects.
 * <p>
 * For example, 
 * </p>
 * <pre>
 * try(
 *     FastqFileDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
 *                                                      .hint(DataStoreProviderHint.ITERATION_ONLY)
 *                                                      .build();
 * 
 *     {@code ThrowingStream<FastqRecord>} stream = datastore.records();
 * ){
 *     stream.forEach( ... );
 * }
 * </pre>
 * <p>
 * Can now be rewritten as:
 * </p>
 * <pre>
 * try(
 *     Results parsedFastqs = FastqFileReader.read(fastqFile);
 * 
 *     {@code ThrowingStream<FastqRecord>} stream = parsedFastqs.records();
 * ){
 *     stream.forEach( ... );
 * }
 * </pre>
 * <p>
 * Or as a one-liner:
 * </p>
 * <pre>
 * try({@code ThrowingStream<FastqRecord>} = FastqFileReader.read(fastqFile).records()){
 * 
 *     stream.forEach( ... );
 * }
 * </pre>
 * @author dkatzel
 * @since 5.3
 */
public final class FastqFileReader {

    private FastqFileReader(){
        //can not instantiate
    }
    /**
     * Get a {@link ThrowingStream} of all the fastq records and the {@link FastqQualityCodec}
     * used in the given fastq file. The {@link FastqQualityCodec} that is used to encode this file
     * will be automatically detected for a performance penalty 
     * (the file will have to be read twice, once to determine the codec, once again to parse the data) for 
     * better performance please use {@link #read(File, FastqQualityCodec)} if the codec is already known.
     * This assumes each section of each fastq record
     * is one line each and does not have comments.  If the fastq has multi-line sections or comments, use {@link #read(FastqParser)}.
     * 
     * 
     * @param fastqFile the fastq file to read; can not be null.
     * 
     * @return a {@link Results} object that contains the {@link ThrowingStream} of all the {@link FastqRecord}s
     * and the {@link FastqQualityCodec} used to encode the file; will not be null.
     * 
     * @throws IOException if there is a problem reading the fastq file.
     * @throws NullPointerException if fastqFile is null.
     * 
     * @see #read(File, FastqQualityCodec)
     * @see #read(FastqParser)
     */
    public static Results read(File fastqFile) throws IOException{
        FastqFileDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
                                                .hint(DataStoreProviderHint.ITERATION_ONLY)
                                                .build();
        return new Results(datastore.records(), datastore.getQualityCodec());
    }
    /**
     * Get a {@link ThrowingStream} of all the fastq records and the {@link FastqQualityCodec}
     * used in the given fastq file using the given {@link FastqQualityCodec}  to decode
     * the quality values.
     * This assumes each section of each fastq record
     * is one line each.  If the fastq has multi-line sections, use {@link #read(FastqParser)}.
     * 
     * @param fastqParser the {@link FastqParser} that knows the input source (file or inputstream), 
     * how the file is compressed, and if thre are multiline sections or comments; can not be null.
     * 
     * @return a {@link Results} object that contains the {@link ThrowingStream} of all the {@link FastqRecord}s
     * and the {@link FastqQualityCodec} used to encode the file; will not be null.
     * 
     * @throws IOException if there is a problem reading the fastq file.
     * @throws NullPointerException if either parameter is null.
     * 
     * @see #read(File, FastqQualityCodec)
     * @see #read(FastqParser)
     * @see FastqFileParserBuilder
     */
    public static Results read(FastqParser fastqParser) throws IOException{
        FastqFileDataStore datastore = new FastqFileDataStoreBuilder(fastqParser)
                                                .hint(DataStoreProviderHint.ITERATION_ONLY)
                                                .build();
        return new Results(datastore.records(), datastore.getQualityCodec());
    }
    /**
     * Get a {@link ThrowingStream} of all the fastq records and the {@link FastqQualityCodec}
     * used in the given fastq file using the given {@link FastqQualityCodec}  to decode
     * the quality values.
     * This assumes each section of each fastq record
     * is one line each and does not have comments.  If the fastq has multi-line sections or comments, use {@link #read(FastqParser)}.
     * 
     * @param fastqFile the fastq file to read; can not be null.
     * 
     * @param codec the {@link FastqQualityCodec} known to encode this file; can not be null.
     * 
     * @return a {@link Results} object that contains the {@link ThrowingStream} of all the {@link FastqRecord}s
     * and the {@link FastqQualityCodec} used to encode the file; will not be null.
     * 
     * @throws IOException if there is a problem reading the fastq file.
     * @throws NullPointerException if either parameter is null.
     * 
     * @see #read(File, FastqQualityCodec)
     * @see #read(FastqParser)
     */
    public static Results read(File fastqFile, FastqQualityCodec codec) throws IOException{
        FastqFileDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
                                                .qualityCodec(codec)
                                                .hint(DataStoreProviderHint.ITERATION_ONLY)
                                                .build();
        return new Results(datastore.records(), datastore.getQualityCodec());
    }
    /**
     * Get a {@link ThrowingStream} of all the fastq records and the {@link FastqQualityCodec}
     * used in the given fastq file using the given {@link FastqQualityCodec}  to decode
     * the quality values.
     * This assumes each section of each fastq record
     * is one line each.  If the fastq has multi-line sections, use {@link #read(FastqParser)}.
     * 
     * @param fastqParser the {@link FastqParser} that knows the input source (file or inputstream), 
     * how the file is compressed, and if thre are multiline sections or comments; can not be null.
     * 
     * @param codec the {@link FastqQualityCodec} known to encode this file; can not be null.
     * 
     * @return a {@link Results} object that contains the {@link ThrowingStream} of all the {@link FastqRecord}s
     * and the {@link FastqQualityCodec} used to encode the file; will not be null.
     * 
     * @throws IOException if there is a problem reading the fastq file.
     * @throws NullPointerException if either parameter is null.
     * 
     * @see #read(File, FastqQualityCodec)
     * @see #read(FastqParser)
     * @see FastqFileParserBuilder
     */
    public static Results read(FastqParser fastqParser, FastqQualityCodec codec) throws IOException{
        FastqFileDataStore datastore = new FastqFileDataStoreBuilder(fastqParser)
                                                .qualityCodec(codec)
                                                .hint(DataStoreProviderHint.ITERATION_ONLY)
                                                .build();
        return new Results(datastore.records(), datastore.getQualityCodec());
    }
    
    /**
     * Results of a fastq file read operation, accessor methods can be used
     * to get a Stream of the {@link FastqRecord}s contained in the file.
     * 
     * Results are closeable so they can be used in a try-with-resource block.
     * Closing the Results will close the underlying {@link ThrowingStream} of records.
     * @author dkatzel
     * @since 5.3
     */
    public static final class Results extends Pair<ThrowingStream<FastqRecord>, FastqQualityCodec>{

        private Results(ThrowingStream<FastqRecord> first, FastqQualityCodec second) {
            super(first, second);
        }
        /**
         * Get a {@link ThrowingStream} of all the {@link FastqRecord}s contained
         * in the file.  Any filtering the user desires must be done through the stream methods.
         * @return a new {@link ThrowingStream} will never be null.
         */
        public ThrowingStream<FastqRecord> records(){
            return getFirst();
        }
        /**
         * Get the {@link FastqQualityCodec} that was used to encode
         * the fastq file.
         * @return the {@link FastqQualityCodec}; will never be null.
         */
        public FastqQualityCodec getCodec(){
            return getSecond();
        }
    }
}
