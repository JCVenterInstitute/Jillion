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
import java.util.Objects;
import java.util.Optional;

import org.jcvi.jillion.core.datastore.DataStoreEntry;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * Simple implementation of {@link FastqFileDataStore}.
 * 
 * @author dkatzel
 * @since 5.0
 */
final class FastqFileDataStoreImpl implements FastqFileDataStore{

    private final FastqDataStore datastore;
    private final FastqQualityCodec codec;
    
    private final File fastqFile;
    
    public FastqFileDataStoreImpl(FastqDataStore datastore,
            FastqQualityCodec codec, File fastqFile) {
        Objects.requireNonNull(datastore);
        Objects.requireNonNull(codec);
        
        this.datastore = datastore;
        this.codec = codec;
        this.fastqFile = fastqFile;
    }

    
    @Override
    public Optional<File> getFile() {
        return Optional.ofNullable(fastqFile);
    }


    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
        return datastore.idIterator();
    }

    @Override
    public FastqRecord get(String id) throws DataStoreException {
        return datastore.get(id);
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return datastore.contains(id);
    }

    @Override
    public long getNumberOfRecords() throws DataStoreException {
        return datastore.getNumberOfRecords();
    }

    @Override
    public boolean isClosed() {
        return datastore.isClosed();
    }

    @Override
    public StreamingIterator<FastqRecord> iterator() throws DataStoreException {
        return datastore.iterator();
    }

    @Override
    public StreamingIterator<DataStoreEntry<FastqRecord>> entryIterator()
            throws DataStoreException {
        return datastore.entryIterator();
    }

    @Override
    public void close() throws IOException {
        datastore.close();
    }

    @Override
    public FastqQualityCodec getQualityCodec() {
        return codec;
    }

}
