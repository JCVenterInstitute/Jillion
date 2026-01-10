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
package org.jcvi.jillion.fasta.aa;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;


import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Helper class which can
 * to iterate over the records contained in a fasta file.
 */
public class ProteinFastaFileReader {
    /**
     * Get a {@link ThrowingStream} of all the {@link ProteinFastaRecord}s
     * in the given fasta file. 
     * @param fastaFile the fasta file to parse; can not be null.
     * @return a new {@link ThrowingStream} of {@link ProteinFastaRecord}s.
     * @throws IOException if there is a problem parsing the fasta file.
     * 
     * @throws NullPointerException if fastaFile is null.
     * 
     * @see #records(File, Consumer)
     * @see ProteinFastaFileDataStoreBuilder
     */
    public static ThrowingStream<ProteinFastaRecord> records(File fastaFile) throws IOException{
        return new ProteinFastaFileDataStoreBuilder(fastaFile)
                        .hint(DataStoreProviderHint.ITERATION_ONLY)
                        .build()
                        .records();
    }
    
    /**
     * Get a {@link ThrowingStream} of all the {@link ProteinFastaRecord}s
     * in the given fasta file. 
     * @param fastaFile the fasta file to parse; can not be null.
     *
     * @throws IOException if there is a problem parsing the fasta file.
     * 
     * @throws NullPointerException if fastaFile is null.
     * 
     * @see #records(File, Consumer)
     * @see ProteinFastaFileDataStoreBuilder
     */
    public static <E extends Throwable>void forEach(File fastaFile, ThrowingBiConsumer<String, ProteinFastaRecord, E> consumer) throws IOException, E{
        new ProteinFastaFileDataStoreBuilder(fastaFile)
                        .hint(DataStoreProviderHint.ITERATION_ONLY)
                        .build()
                        .forEach(consumer);
    }
    /**
     * Parse the given Fasta File and for each record that passes the given filters
     * call the provided consumer.
     * The filters are chained so that only ids that pass
     * the filter will be parsed and given to the recordFilter.  Only
     * records that pass the recordFilter will be passed to the consumer.
     * 
     *  
     * @param fastaFile the fasta file to parse; can not be null.
     * @param idFilter a Predicate of Ids to include in the for each.  If this
     * predicate is null, then all records will be parsed.
     * @param recordFilter a Predicate to include/exclude parsed records.  If this
     * predicate is null, then all parsed records will be provided to the consumer.
     * 
     * @param consumer the consumer that will be called for each record that passes the filter.
     * 
     * @throws IOException if there is a problem parsing the fasta file.
     * 
     * @throws NullPointerException if fastaFile is null.
     * 
     * @see ProteinFastaFileDataStoreBuilder
     */
    public static <E extends Throwable> void forEach(File fastaFile, Predicate<String> idFilter, Predicate<ProteinFastaRecord> recordFilter,
                        ThrowingBiConsumer<String, ProteinFastaRecord, E> consumer) throws IOException, E{
        
        forEach (fastaFile, builder->
            builder.filter(idFilter==null? s-> true: idFilter)
                    .filterRecords(recordFilter ==null ? r -> true : recordFilter)
        ,
        consumer);

    }
    /**
     * Parse the given Fasta File and for each record that passes the given filters
     * of the datastore, call the provided consumer.
     *
     *
     *
     * @param fastaFile the fasta file to parse; can not be null.
     * @param extraBuilderOptions Consumer of the builder used to parse the fasta file
     *                            to add any extra filters or defline converters, decoding options etc.
     *                            If {@code null}, then no extra options will be set.
     *
     *
     * @param consumer the consumer that will be called for each record that passes the filter; can not be null.
     *
     * @throws IOException if there is a problem parsing the fasta file.
     *
     * @throws NullPointerException if either fastaFile or consumer are null.
     *
     * @see ProteinFastaFileDataStoreBuilder
     * @since 6.1
     */
    public static <E extends Throwable> void forEach(File fastaFile, Consumer<ProteinFastaFileDataStoreBuilder> extraBuilderOptions,
                                                     ThrowingBiConsumer<String, ProteinFastaRecord, E> consumer) throws IOException, E{

        Objects.requireNonNull(consumer);
        ProteinFastaFileDataStoreBuilder builder = new ProteinFastaFileDataStoreBuilder(fastaFile);

        if(extraBuilderOptions !=null){
            extraBuilderOptions.accept(builder);
        }
        try(ProteinFastaDataStore datastore = builder.hint(DataStoreProviderHint.ITERATION_ONLY)
                .build()){
            datastore.forEach(consumer);
        }
    }


    /**
     * Get a {@link ThrowingStream} of all the {@link ProteinFastaRecord}s
     * that passes the given filters.
     *
     *
     *
     * @param fastaFile the fasta file to parse; can not be null.
     * @param extraBuilderOptions Consumer of the builder used to parse the fasta file
     *                            to add any extra filters or defline converters, decoding options etc.
     *                            If {@code null}, then no extra options will be set.
     *
     *
     *
     * @throws IOException if there is a problem parsing the fasta file.
     *
     * @throws NullPointerException if either fastaFile or consumer are null.
     *
     * @see ProteinFastaFileDataStoreBuilder
     * @since 6.1
     */
    public static  ThrowingStream<ProteinFastaRecord> records(File fastaFile, Consumer<ProteinFastaFileDataStoreBuilder> extraBuilderOptions) throws IOException{


        ProteinFastaFileDataStoreBuilder builder = new ProteinFastaFileDataStoreBuilder(fastaFile);

        if(extraBuilderOptions !=null){
            extraBuilderOptions.accept(builder);
        }

        return builder.hint(DataStoreProviderHint.ITERATION_ONLY)
                .build()
                .records();

    }

    /**
     * Get a {@link StreamingIterator} of all the {@link ProteinFastaRecord}s
     * that passes the given filters.
     *
     *
     *
     * @param fastaFile the fasta file to parse; can not be null.
     * @param extraBuilderOptions Consumer of the builder used to parse the fasta file
     *                            to add any extra filters or defline converters, decoding options etc.
     *                            If {@code null}, then no extra options will be set.
     *
     *
     *
     * @throws IOException if there is a problem parsing the fasta file.
     *
     * @throws NullPointerException if either fastaFile or consumer are null.
     *
     * @see ProteinFastaFileDataStoreBuilder
     * @since 6.1
     */
    public static StreamingIterator<ProteinFastaRecord> iterator(File fastaFile, Consumer<ProteinFastaFileDataStoreBuilder> extraBuilderOptions) throws IOException{


        ProteinFastaFileDataStoreBuilder builder = new ProteinFastaFileDataStoreBuilder(fastaFile);

        if(extraBuilderOptions !=null){
            extraBuilderOptions.accept(builder);
        }

        return builder.hint(DataStoreProviderHint.ITERATION_ONLY)
                .build()
                .iterator();

    }

    /**
     * Get a {@link StreamingIterator} of all the {@link ProteinFastaRecord}s
     * that passes the given filters.
     *
     *
     *
     * @param fastaFile the fasta file to parse; can not be null.
     *
     *
     *
     * @throws IOException if there is a problem parsing the fasta file.
     *
     * @throws NullPointerException if either fastaFile or consumer are null.
     *
     * @see ProteinFastaFileDataStoreBuilder
     * @since 6.1
     */
    public static StreamingIterator<ProteinFastaRecord> iterator(File fastaFile) throws IOException{


        return iterator(fastaFile, null);

    }
}
