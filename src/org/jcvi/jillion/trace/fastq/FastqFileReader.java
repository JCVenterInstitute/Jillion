/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
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
package org.jcvi.jillion.trace.fastq;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;
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
 * FastqFileReader.forEach(fastqFile,
 *                       (id, record) ->{ ... });
 * 
 * </pre>
 * @author dkatzel
 * @since 5.3
 */
public final class FastqFileReader {

    private FastqFileReader(){
        //can not instantiate
    }
    
    /**
     * Iterate through all the records in the datastore and call the given consumer on each one.
     * The {@link FastqQualityCodec} that is used to encode this file
     * will be automatically detected for a performance penalty 
     * (the file will have to be read twice, once to determine the codec, once again to parse the data) for 
     * better performance please use {@link #forEach(File, FastqQualityCodec)} if the codec is already known.
     * This assumes each section of each fastq record
     * is one line each and does not have comments.  If the fastq has multi-line sections or comments, use {@link #read(FastqParser)}.
     * 
     * 
     * @param fastqFile the fastq file to read; can not be null.
     * @param consumer a BiConsumer that takes the id of the record as the first parameter and the record as the second parameter. 
     * 
     * 
     * @throws IOException if there is a problem reading the fastq file.
     * @throws NullPointerException if fastqFile is null.
     * 
     * @see #read(File, FastqQualityCodec)
     * @see #read(FastqParser)
     */
    public static <E extends Throwable> void forEach(File fastqFile, ThrowingBiConsumer<String, FastqRecord, E> consumer) throws IOException, E{
        Objects.requireNonNull(consumer, "consumer can not be null");
        try(FastqFileDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
                                                .hint(DataStoreProviderHint.ITERATION_ONLY)
                                                .build()){
            datastore.forEach(consumer);
        }
        
    }
    /**
     * Iterate through all the records in the datastore and call the given consumer on each one.
     * The {@link FastqQualityCodec} that is used to encode this file
     * will be automatically detected for a performance penalty 
     * (the file will have to be read twice, once to determine the codec, once again to parse the data) for 
     * better performance please use {@link #forEach(File, FastqQualityCodec)} if the codec is already known.
     * This assumes each section of each fastq record
     * is one line each and does not have comments.  If the fastq has multi-line sections or comments, use {@link #read(FastqParser)}.
     * 
     * 
     * @param fastqParser the {@link FastqParser} that knows the input source (file or inputstream), 
     * how the file is compressed, and if there are multiline sections or comments; can not be null.
     * @param consumer a BiConsumer that takes the id of the record as the first parameter and the record as the second parameter. 
     * 
     * 
     * @throws IOException if there is a problem reading the fastq file.
     * @throws NullPointerException if fastqFile is null.
     * 
     * @see #read(File, FastqQualityCodec)
     * @see #read(FastqParser)
     */
    public static <E extends Throwable> void forEach(FastqParser fastqParser, ThrowingBiConsumer<String, FastqRecord, E> consumer) throws IOException, E{
        Objects.requireNonNull(consumer, "consumer can not be null");
        try(FastqFileDataStore datastore = new FastqFileDataStoreBuilder(fastqParser)
                                                .hint(DataStoreProviderHint.ITERATION_ONLY)
                                                .build()){
            datastore.forEach(consumer);
        }
        
    }
    /**
     * Iterate through all the records in the datastore and call the given consumer on each one.
     * The {@link FastqQualityCodec} that is used to encode this file
     * will be automatically detected for a performance penalty 
     * (the file will have to be read twice, once to determine the codec, once again to parse the data) for 
     * better performance please use {@link #forEach(File, FastqQualityCodec)} if the codec is already known.
     * This assumes each section of each fastq record
     * is one line each and does not have comments.  If the fastq has multi-line sections or comments, use {@link #read(FastqParser)}.
     * 
     * 
     * @param fastqParser the {@link FastqParser} that knows the input source (file or inputstream), 
     * how the file is compressed, and if there are multiline sections or comments; can not be null.
     * 
     * @param codec the {@link FastqQualityCodec} known to encode this file; can not be null.
     * 
     * @param consumer a BiConsumer that takes the id of the record as the first parameter and the record as the second parameter. 
     * 
     * 
     * @throws IOException if there is a problem reading the fastq file.
     * @throws NullPointerException if fastqFile is null.
     * 
     */
    public static <E extends Throwable> void forEach(FastqParser fastqParser, FastqQualityCodec codec, ThrowingBiConsumer<String, FastqRecord, E> consumer) throws IOException, E{
        Objects.requireNonNull(consumer, "consumer can not be null");
        try(FastqFileDataStore datastore = new FastqFileDataStoreBuilder(fastqParser)
                                                .qualityCodec(codec)
                                                .hint(DataStoreProviderHint.ITERATION_ONLY)
                                                .build()){
            datastore.forEach(consumer);
        }
        
    }
    /**
     * Iterate through all the records in the datastore and call the given consumer on each one that match the given filters.
     * The {@link FastqQualityCodec} that is used to encode this file
     * will be automatically detected for a performance penalty 
     * (the file will have to be read twice, once to determine the codec, once again to parse the data) for 
     * better performance please use {@link #forEach(File, FastqQualityCodec)} if the codec is already known.
     * This assumes each section of each fastq record
     * is one line each and does not have comments.  If the fastq has multi-line sections or comments, use {@link #read(FastqParser)}.
     * 
     * 
     * @param fastqParser the {@link FastqParser} that knows the input source (file or inputstream), 
     * how the file is compressed, and if there are multiline sections or comments; can not be null.
     * 
     * @param codec the {@link FastqQualityCodec} known to encode this file; can not be null.
     * 
     * @param idFilter a Predicate to include only records whose by Id makes the predicate return true; if null, then no id filter is used.
     * @param recordFilter a Predicate to include only the parsed records that return true; if null, then no record filter is used.
     * 
     * @param consumer a BiConsumer that takes the id of the record as the first parameter and the record as the second parameter. 
     * 
     * 
     * @throws IOException if there is a problem reading the fastq file.
     * @throws NullPointerException if fastqFile is null.
     * 
     */
    public static <E extends Throwable> void forEach(FastqParser fastqParser, FastqQualityCodec codec,
            Predicate<String> idFilter,
            Predicate<FastqRecord> recordFilter,
            
            ThrowingBiConsumer<String, FastqRecord, E> consumer) throws IOException, E{
        _forEach(fastqParser, codec, idFilter, recordFilter, consumer);
    }
    
    /**
     * Iterate through all the records in the datastore and call the given consumer on each one that match the given filters.
     * The {@link FastqQualityCodec} that is used to encode this file
     * will be automatically detected for a performance penalty 
     * (the file will have to be read twice, once to determine the codec, once again to parse the data) for 
     * better performance please use {@link #forEach(File, FastqQualityCodec)} if the codec is already known.
     * This assumes each section of each fastq record
     * is one line each and does not have comments.  If the fastq has multi-line sections or comments, use {@link #read(FastqParser)}.
     * 
     * 
     * @param fastqParser the {@link FastqParser} that knows the input source (file or inputstream), 
     * how the file is compressed, and if there are multiline sections or comments; can not be null.
     * 
     * @param codec the {@link FastqQualityCodec} known to encode this file; can not be null.
     * 
     * @param recordFilter a Predicate to include only the parsed records that return true; if null, then no record filter is used.
     * 
     * @param consumer a BiConsumer that takes the id of the record as the first parameter and the record as the second parameter. 
     * 
     * 
     * @throws IOException if there is a problem reading the fastq file.
     * @throws NullPointerException if fastqFile is null.
     * 
     */
    public static <E extends Throwable> void forEach(FastqParser fastqParser, FastqQualityCodec codec,
            Predicate<FastqRecord> recordFilter,
            
            ThrowingBiConsumer<String, FastqRecord, E> consumer) throws IOException, E{
        _forEach(fastqParser, codec, null, recordFilter, consumer);
    }
    
    /**
     * Iterate through all the records in the datastore and call the given consumer on each one that match the given filters.
     * The {@link FastqQualityCodec} that is used to encode this file
     * will be automatically detected for a performance penalty 
     * (the file will have to be read twice, once to determine the codec, once again to parse the data) for 
     * better performance please use {@link #forEach(File, FastqQualityCodec)} if the codec is already known.
     * This assumes each section of each fastq record
     * is one line each and does not have comments.  If the fastq has multi-line sections or comments, use {@link #read(FastqParser)}.
     * 
     * 
     * @param fastqParser the {@link FastqParser} that knows the input source (file or inputstream), 
     * how the file is compressed, and if there are multiline sections or comments; can not be null.
     * 
     * @param codec the {@link FastqQualityCodec} known to encode this file; can not be null.
     * 
     * @param idFilter a Predicate to include only records whose by Id makes the predicate return true; if null, then no id filter is used.
     * @param recordFilter a Predicate to include only the parsed records that return true; if null, then no record filter is used.
     * 
     * @param consumer a BiConsumer that takes the id of the record as the first parameter and the record as the second parameter. 
     * 
     * 
     * @throws IOException if there is a problem reading the fastq file.
     * @throws NullPointerException if fastqFile is null.
     * 
     */
    public static <E extends Throwable> void forEach(FastqParser fastqParser,
            Predicate<String> idFilter,
            Predicate<FastqRecord> recordFilter,
            
            ThrowingBiConsumer<String, FastqRecord, E> consumer) throws IOException, E{
        _forEach(fastqParser, null, idFilter, recordFilter, consumer);
    }
    /**
     * Iterate through all the records in the datastore and call the given consumer on each one that match the given filters.
     * The {@link FastqQualityCodec} that is used to encode this file
     * will be automatically detected for a performance penalty 
     * (the file will have to be read twice, once to determine the codec, once again to parse the data) for 
     * better performance please use {@link #forEach(File, FastqQualityCodec)} if the codec is already known.
     * This assumes each section of each fastq record
     * is one line each and does not have comments.  If the fastq has multi-line sections or comments, use {@link #read(FastqParser)}.
     * 
     * 
     * @param fastqParser the {@link FastqParser} that knows the input source (file or inputstream), 
     * how the file is compressed, and if there are multiline sections or comments; can not be null.
     * 
     * @param codec the {@link FastqQualityCodec} known to encode this file; can not be null.
     * 
     * @param idFilter a Predicate to include only records whose by Id makes the predicate return true; if null, then no id filter is used.
     * @param recordFilter a Predicate to include only the parsed records that return true; if null, then no record filter is used.
     * 
     * @param consumer a BiConsumer that takes the id of the record as the first parameter and the record as the second parameter. 
     * 
     * 
     * @throws IOException if there is a problem reading the fastq file.
     * @throws NullPointerException if fastqFile is null.
     * 
     */
    public static <E extends Throwable> void forEach(FastqParser fastqParser,
            Predicate<FastqRecord> recordFilter,
            
            ThrowingBiConsumer<String, FastqRecord, E> consumer) throws IOException, E{
        _forEach(fastqParser, null, null, recordFilter, consumer);
    }
    private static <E extends Throwable> void _forEach(FastqParser fastqParser, FastqQualityCodec codec,
            Predicate<String> idFilter,
            Predicate<FastqRecord> recordFilter,
            
            ThrowingBiConsumer<String, FastqRecord, E> consumer) throws IOException, E{
        Objects.requireNonNull(consumer, "consumer can not be null");
        FastqFileDataStoreBuilder builder = new FastqFileDataStoreBuilder(fastqParser)                             
                                                .hint(DataStoreProviderHint.ITERATION_ONLY);
        
        if(idFilter !=null){
            builder.filter(idFilter);
        }
        if(recordFilter !=null){
            builder.filterRecords(recordFilter);
        }
        
        if(codec !=null){
            builder.qualityCodec(codec);
        }
        try(FastqFileDataStore datastore = builder.build()){
            datastore.forEach(consumer);
        }
        
    }
    
    /**
     * Iterate through all the records in the datastore and call the given consumer on each one.
     * The given {@link FastqQualityCodec}
     * will be used to decode the quality values.
     * 
     * This assumes each section of each fastq record
     * is one line each.  If the fastq has multi-line sections, use {@link #read(FastqParser)}..
     * This assumes each section of each fastq record
     * is one line each and does not have comments.  If the fastq has multi-line sections or comments, use {@link #read(FastqParser)}.
     * 
     * 
     * @param fastqFile the fastq file to read; can not be null.
     * 
     * @param codec the {@link FastqQualityCodec} known to encode this file; can not be null.
     * 
     * @param consumer a BiConsumer that takes the id of the record as the first parameter and the record as the second parameter. 
     * 
     * 
     * @throws IOException if there is a problem reading the fastq file.
     * @throws NullPointerException if fastqFile is null.
     * 
     */
    public static <E extends Throwable> void forEach(File fastqFile, FastqQualityCodec codec, ThrowingBiConsumer<String, FastqRecord, E> consumer) throws IOException, E{
        Objects.requireNonNull(consumer, "consumer can not be null");
        try(FastqFileDataStore datastore = new FastqFileDataStoreBuilder(fastqFile)
                                                .qualityCodec(codec)
                                                .hint(DataStoreProviderHint.ITERATION_ONLY)
                                                .build()){
            datastore.forEach(consumer);
        }
        
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
        return new Results(datastore);
    }
    /**
     * Get a {@link ThrowingStream} of all the fastq records and the {@link FastqQualityCodec}
     * used in the given fastq file. The {@link FastqQualityCodec} that is used to encode this file
     * will be automatically detected for a performance penalty 
     * This assumes each section of each fastq record
     * is one line each.  If the fastq has multi-line sections, use {@link #read(FastqParser)}.
     * 
     * @param fastqParser the {@link FastqParser} that knows the input source (file or inputstream), 
     * how the file is compressed, and if there are multiline sections or comments; can not be null.
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
        return new Results(datastore);
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
        return new Results(datastore);
    }
    /**
     * Get a {@link ThrowingStream} of all the fastq records and the {@link FastqQualityCodec}
     * used in the given fastq file using the given {@link FastqQualityCodec}  to decode
     * the quality values.
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
        return new Results(datastore);
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
    public static final class Results implements Closeable{

        private final FastqFileDataStore datastore;
        
        private Results(FastqFileDataStore datastore) {
            this.datastore = datastore;
        }
        /**
         * Get a {@link ThrowingStream} of all the {@link FastqRecord}s contained
         * in the file.  Any filtering the user desires must be done through the stream methods.
         * @return a new {@link ThrowingStream} will never be null.
         */
        public ThrowingStream<FastqRecord> records() throws IOException{
            return datastore.records();
        }
        /**
         * Get the {@link FastqQualityCodec} that was used to encode
         * the fastq file.
         * @return the {@link FastqQualityCodec}; will never be null.
         */
        public FastqQualityCodec getCodec(){
            return datastore.getQualityCodec();
        }

        public <E extends Throwable> void forEach(ThrowingBiConsumer<String, FastqRecord, E> consumer) throws E, IOException{
            datastore.forEach(consumer);
        }
        @Override
        public void close() throws IOException {
           datastore.close();
        }
        
        
    }
}
