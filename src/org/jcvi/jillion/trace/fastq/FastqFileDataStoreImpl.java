package org.jcvi.jillion.trace.fastq;

import java.io.IOException;
import java.util.Objects;

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
    
    public FastqFileDataStoreImpl(FastqDataStore datastore,
            FastqQualityCodec codec) {
        Objects.requireNonNull(datastore);
        Objects.requireNonNull(codec);
        
        this.datastore = datastore;
        this.codec = codec;
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
