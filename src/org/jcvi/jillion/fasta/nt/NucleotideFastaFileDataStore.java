package org.jcvi.jillion.fasta.nt;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;
/**
 * A {@link NucleotideFastaDataStore} where all the {@link NucleotideFastaRecord}s
 * in this datastore belong to the same fasta file.
 * @author dkatzel
 * @since 5.3
 */
public interface NucleotideFastaFileDataStore extends NucleotideFastaDataStore{

    /**
     * Create a {@link NucleotideFastaDataStore} of all the records
     * in the given fasta file.  Warning! This usually stores all the records in memory
     * use {@link #fromFile(File, DataStoreProviderHint)} or {@link NucleotideFastaFileDataStoreBuilder}
     * to handle the file or datastore implementation differently.
     * 
     * @param fastaFile the fasta file make a {@link NucleotideFastaDataStore} with;
     * can not be null and must exist.
     * 
     * @apiNote As of Jillion 5.1, this will also look for a FastaIndex ({@code .fai})
     * file using the standard naming conventions and is equivalent to
     * <pre>
     * new NucleotideFastaFileDataStoreBuilder(fastaFile, new File(fastaFile.getParentFile(), fastaFile.getName() + ".fai"));
     * </pre>
     * 
     * @throws IOException if the fasta file does not exist, or can not be read.
     * @throws NullPointerException if fastaFile is null.
     * @return a new NucleotideFastaDataStore; will never be null.
     * @since 5.3
     * 
     * @see NucleotideFastaFileDataStoreBuilder
     * @see #fromFile(File, DataStoreProviderHint)
     */
    public static NucleotideFastaFileDataStore fromFile(File fastaFile) throws IOException{
        return new NucleotideFastaFileDataStoreBuilder(fastaFile).build();
    }
    /**
     * Create a {@link NucleotideFastaDataStore} of all the records
     * in the given fasta file with the given {@link DataStoreProviderHint}
     * to help jillion choose a datastore implementation.  If filtering records
     * is desired, pleas use {@link NucleotideFastaFileDataStoreBuilder}.
     * 
     * @param fastaFile the fasta file make a {@link NucleotideFastaDataStore} with;
     * can not be null and must exist.
     * 
     * @param hint the {@link DataStoreProviderHint} to use; can not be null.
     * 
     * @apiNote As of Jillion 5.1, this will also look for a FastaIndex ({@code .fai})
     * file using the standard naming conventions and is equivalent to
     * <pre>
     * new NucleotideFastaFileDataStoreBuilder(fastaFile, new File(fastaFile.getParentFile(), fastaFile.getName() + ".fai"));
     * </pre>
     * 
     * @throws IOException if the fasta file does not exist, or can not be read.
     * @throws NullPointerException if any parameter is null.
     * @return a new NucleotideFastaDataStore; will never be null.
     * @since 5.3
     * 
     * @see NucleotideFastaFileDataStoreBuilder
     */
    public static NucleotideFastaFileDataStore fromFile(File fastaFile, DataStoreProviderHint hint) throws IOException{
        NucleotideFastaFileDataStoreBuilder builder =  new NucleotideFastaFileDataStoreBuilder(fastaFile);
        builder.hint(hint);
        
        return builder.build();
    }
    
    /**
     * Get the actual {@link java.io.File} that this datastore
     * wraps.  Note, because of datastore filtering and other decorators, the file may contain additional reads
     * not present in this datastore and the {@link NucleotideFastaRecord}s that are present,
     *  may not match 100% to the input file.  It is also possible that this file
     *  uses some kind of compression that this datastore knows how to decompress.
     * 
     * @return an {@link Optional}  {@link java.io.File}, if it is known.
     * 
     * @since 5.3
     */
    Optional<File> getFile();
}
