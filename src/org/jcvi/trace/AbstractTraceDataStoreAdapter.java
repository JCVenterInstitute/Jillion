/*
 * Created on Nov 13, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace;

import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;

public abstract class AbstractTraceDataStoreAdapter<D extends Trace, T> implements DataStore<T> {

    private final DataStore<D> delegate;

    protected  abstract T adapt(D delegate);
    /**
     * @param delegate
     */
    public AbstractTraceDataStoreAdapter(DataStore<D> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return delegate.contains(id);
    }

    @Override
    public T get(String id) throws DataStoreException {
        return adapt(delegate.get(id));
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
