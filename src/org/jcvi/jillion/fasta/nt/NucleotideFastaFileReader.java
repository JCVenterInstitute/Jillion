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
package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;

/**
 * Helper class which can
 * to iterate over the records contained in a fasta file.
 */
public class NucleotideFastaFileReader {
    /**
     * Get a {@link ThrowingStream} of all the {@link NucleotideFastaRecord}s
     * in the given fasta file. 
     * @param fastaFile the fasta file to parse; can not be null.
     * @return a new {@link ThrowingStream} of {@link NucleotideFastaRecord}s.
     * @throws IOException if there is a problem parsing the fasta file.
     * 
     * @throws NullPointerException if fastaFile is null.
     * 
     * @see #records(File, Consumer)
     * @see NucleotideFastaFileDataStoreBuilder
     */
    public static ThrowingStream<NucleotideFastaRecord> records(File fastaFile) throws IOException{
        return new NucleotideFastaFileDataStoreBuilder(fastaFile)
                        .hint(DataStoreProviderHint.ITERATION_ONLY)
                        .build()
                        .records();
    }
    
    /**
     * Get a {@link ThrowingStream} of all the {@link NucleotideFastaRecord}s
     * in the given fasta file. 
     * @param fastaFile the fasta file to parse; can not be null.
     *
     * @throws IOException if there is a problem parsing the fasta file.
     * 
     * @throws NullPointerException if fastaFile is null.
     * 
     * @see #records(File, Consumer)
     * @see NucleotideFastaFileDataStoreBuilder
     */
    public static <E extends Throwable>void forEach(File fastaFile, ThrowingBiConsumer<String, NucleotideFastaRecord, E> consumer) throws IOException, E{
        new NucleotideFastaFileDataStoreBuilder(fastaFile)
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
     * @see NucleotideFastaFileDataStoreBuilder
     */
    public static <E extends Throwable> void forEach(File fastaFile, Predicate<String> idFilter, Predicate<NucleotideFastaRecord> recordFilter,
                        ThrowingBiConsumer<String, NucleotideFastaRecord, E> consumer) throws IOException, E{
        
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
     * @see NucleotideFastaFileDataStoreBuilder
     * @since 6.1
     */
    public static <E extends Throwable> void forEach(File fastaFile, Consumer<NucleotideFastaFileDataStoreBuilder> extraBuilderOptions,
                                                     ThrowingBiConsumer<String, NucleotideFastaRecord, E> consumer) throws IOException, E{

        Objects.requireNonNull(consumer);
        NucleotideFastaFileDataStoreBuilder builder = new NucleotideFastaFileDataStoreBuilder(fastaFile);

        if(extraBuilderOptions !=null){
            extraBuilderOptions.accept(builder);
        }
        try(NucleotideFastaDataStore datastore = builder.hint(DataStoreProviderHint.ITERATION_ONLY)
                .build()){
            datastore.forEach(consumer);
        }
    }


    /**
     * Get a {@link ThrowingStream} of all the {@link NucleotideFastaRecord}s
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
     * @see NucleotideFastaFileDataStoreBuilder
     * @since 6.1
     */
    public static  ThrowingStream<NucleotideFastaRecord> records(File fastaFile, Consumer<NucleotideFastaFileDataStoreBuilder> extraBuilderOptions) throws IOException{


        NucleotideFastaFileDataStoreBuilder builder = new NucleotideFastaFileDataStoreBuilder(fastaFile);

        if(extraBuilderOptions !=null){
            extraBuilderOptions.accept(builder);
        }

        return builder.hint(DataStoreProviderHint.ITERATION_ONLY)
                .build()
                .records();

    }

    /**
     * Get a {@link StreamingIterator} of all the {@link NucleotideFastaRecord}s
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
     * @see NucleotideFastaFileDataStoreBuilder
     * @since 6.1
     */
    public static StreamingIterator<NucleotideFastaRecord> iterator(File fastaFile, Consumer<NucleotideFastaFileDataStoreBuilder> extraBuilderOptions) throws IOException{


        NucleotideFastaFileDataStoreBuilder builder = new NucleotideFastaFileDataStoreBuilder(fastaFile);

        if(extraBuilderOptions !=null){
            extraBuilderOptions.accept(builder);
        }

        return builder.hint(DataStoreProviderHint.ITERATION_ONLY)
                .build()
                .iterator();

    }

    /**
     * Get a {@link StreamingIterator} of all the {@link NucleotideFastaRecord}s
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
     * @throws NullPointerException if fastaFile is null.
     *
     * @see NucleotideFastaFileDataStoreBuilder
     * @since 6.1
     */
    public static StreamingIterator<NucleotideFastaRecord> iterator(File fastaFile) throws IOException{


        return iterator(fastaFile, null);

    }
}
