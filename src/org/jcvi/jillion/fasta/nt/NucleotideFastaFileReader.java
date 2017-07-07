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
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;

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
     * @see #records(File, Predicate)
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
     * @return a new {@link ThrowingStream} of {@link NucleotideFastaRecord}s.
     * @throws IOException if there is a problem parsing the fasta file.
     * 
     * @throws NullPointerException if fastaFile is null.
     * 
     * @see #records(File, Predicate)
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
        
        
        new NucleotideFastaFileDataStoreBuilder(fastaFile)
                        .hint(DataStoreProviderHint.ITERATION_ONLY)
                        .filter(idFilter==null? s-> true: idFilter)
                        .filterRecords(recordFilter ==null ? r -> true : recordFilter)
                        .build()
                        .forEach(consumer);
    }
    /**
     * Get a {@link ThrowingStream} of all the {@link NucleotideFastaRecord}s
     * in the given fasta file.  
     * 
     * @param fastaFile the fasta file to parse; can not be null.
     * @param idFilter only include records that make the given filter return {@code true};
     * can not be null.
     * 
     * @return a new {@link ThrowingStream} of {@link NucleotideFastaRecord}s.
     * 
     * @throws IOException if there is a problem parsing the fasta file.
     * 
     * @throws NullPointerException if either parameter is null.
     * 
     * @see #records(File, Predicate)
     * @see NucleotideFastaFileDataStoreBuilder
     */
    public static ThrowingStream<NucleotideFastaRecord> records(File fastaFile, Predicate<String> idFilter) throws IOException{
        return new NucleotideFastaFileDataStoreBuilder(fastaFile)
                        .hint(DataStoreProviderHint.ITERATION_ONLY)
                        .filter(idFilter)
                        .build()
                        .records();
    }
}
