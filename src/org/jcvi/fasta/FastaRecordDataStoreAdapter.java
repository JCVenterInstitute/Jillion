/*
 * Created on Jan 26, 2010
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;
/**
 * {@code FastaRecordDataStoreAdapter} adapts a {@link DataStore} of {@link FastaRecord}s
 * into a {@link DataStore} of the value returned by {@link FastaRecord#getValues()}.
 * @author dkatzel
 *
 *
 */
public class FastaRecordDataStoreAdapter<T,F extends FastaRecord<T>> implements DataStore<T> {

    private final DataStore<F> delegate;
    /**
     * Convert of {@code DataStore<F>} into a DataStore{@code DataStore<T>}
     * @param <T> the values of the fastaRecord.
     * @param <F> a FastaRecord.
     * @param datastoreOfFastaRecords the DataStore of F to wrap.
     * @return a new {@code DataStore<T>} which wraps the given datastore. 
     */
    public static <T,F extends FastaRecord<T>> FastaRecordDataStoreAdapter<T,F> adapt(DataStore<F> datastoreOfFastaRecords){
        return new FastaRecordDataStoreAdapter<T,F>(datastoreOfFastaRecords);
    }
    public FastaRecordDataStoreAdapter(DataStore<F> datastoreOfFastaRecords){
        this.delegate = datastoreOfFastaRecords;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return delegate.contains(id);
    }

    @Override
    public T get(String id) throws DataStoreException {
        return delegate.get(id).getValues();
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        return delegate.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        return delegate.size();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
        
    }

    @Override
    public Iterator<T> iterator() {
        return new DataStoreIterator<T>(this);
    }
    
    
}
