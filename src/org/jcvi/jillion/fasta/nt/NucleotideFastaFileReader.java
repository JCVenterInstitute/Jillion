package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
import org.jcvi.jillion.core.util.ThrowingStream;

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
                        .build()
                        .records();
    }
}
