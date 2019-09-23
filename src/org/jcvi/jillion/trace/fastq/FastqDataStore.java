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
/*
 * Created on Dec 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.fastq;

import org.jcvi.jillion.trace.TraceDataStore;

import java.io.File;
import java.io.IOException;

/**
 * {@code FastqDataStore} is a
 * marker-interface for a {@link DataStore}
 * of {@link FastqRecord}s.
 * @author dkatzel
 *
 */
public interface FastqDataStore extends TraceDataStore<FastqRecord>{

    /**
     * Create an in memory datastore of all the fastq records
     * of the given file using the given codec.
     * @param fastqFile the file to parse.
     * @param codec the FastqQualityCodec to use; can not be null.
     * @return a new FastqDataStore
     *
     * @throws IOException if there is a problem parsing the file.
     * @throws NullPointerException if any parameter is null.
     *
     * @since 5.3.2
     */
    static FastqDataStore fromFile(File fastqFile, FastqQualityCodec codec) throws IOException {
        return new FastqFileDataStoreBuilder(fastqFile).qualityCodec(codec).build();
    }
    /**
     * Create an in memory datastore of all the fastq records
     * of the given file guessing the codec for a performance penalty.
     * @param fastqFile the file to parse.
     * @return a new FastqDataStore
     *
     * @throws IOException if there is a problem parsing the file.
     * @throws NullPointerException if any parameter is null.
     *
     * @since 5.3.2
     */
    static FastqDataStore fromFile(File fastqFile) throws IOException {
        return new FastqFileDataStoreBuilder(fastqFile).build();
    }
}
