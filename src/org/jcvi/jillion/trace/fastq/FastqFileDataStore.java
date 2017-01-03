/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
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

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.jcvi.jillion.core.datastore.DataStoreProviderHint;

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
    /**
     * Get the actual {@link java.io.File} that this datastore
     * wraps.  Note, because of datastore filtering and other decorators, the file may contain additional reads
     * not present in this datastore and the {@link FastqRecord}s that are present, may not match 100% to
     * the input file.
     * 
     * @return an {@link Optional}  {@link java.io.File}, if it is known.
     * 
     * @since 5.2
     */
    Optional<File> getFile();
    
    /**
     * Create a {@link FastqFileDataStoreBuilder} of all the records
     * in the given fasta file.  Warning! This usually stores all the records in memory
     * use {@link #fromFile(File, DataStoreProviderHint)} or {@link FastqFileDataStoreBuilder}
     * to handle the file or datastore implementation differently.
     * 
     * @param fastqFile the fastq file make a {@link FastqFileDataStoreBuilder} with;
     * can not be null and must exist.
     * 
     * 
     * @throws IOException if the fastq file does not exist, or can not be read.
     * @throws NullPointerException if fastqFile is null.
     * @return a new FastqFileDataStore; will never be null.
     * @since 5.3
     * 
     * @see NucleotideFastaFileDataStoreBuilder
     * @see #fromFile(File, DataStoreProviderHint)
     */
    public static FastqFileDataStore fromFile(File fastqFile) throws IOException{
        return new FastqFileDataStoreBuilder(fastqFile).build();
    }
    /**
     * Create a {@link FastqFileDataStore} of all the records
     * in the given fastq file with the given {@link DataStoreProviderHint}
     * to help jillion choose a datastore implementation.  If filtering records
     * is desired, pleas use {@link FastqFileDataStoreBuilder}.
     * 
     * @param fastqFile the fastq file make a {@link FastqFileDataStore} with;
     * can not be null and must exist.
     * 
     * @param hint the {@link DataStoreProviderHint} to use; can not be null.
     * @throws IOException if the fasta file does not exist, or can not be read.
     * @throws NullPointerException if any parameter is null.
     * @return a new FastqFileDataStore; will never be null.
     * @since 5.3
     * 
     * @see NucleotideFastaFileDataStoreBuilder
     */
    public static FastqFileDataStore fromFile(File fastqFile, DataStoreProviderHint hint) throws IOException{
        FastqFileDataStoreBuilder builder =  new FastqFileDataStoreBuilder(fastqFile);
        builder.hint(hint);
        
        return builder.build();
    }
}
