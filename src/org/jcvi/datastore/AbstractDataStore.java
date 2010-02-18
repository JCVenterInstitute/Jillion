/*
 * Created on Aug 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.IOException;
import java.util.Iterator;

public abstract class  AbstractDataStore<T> implements DataStore<T>{
    private boolean isClosed;
    
    private synchronized void throwExceptionIfClosed() throws DataStoreException {
        if(isClosed){
            throw new DataStoreException("DataStore is closed");
        }
    }
    
    @Override
    public synchronized void close() throws IOException {
        isClosed = true;
    }

    public synchronized boolean isClosed() {
        return isClosed;
    }
    
    @Override
    public synchronized Iterator<T> iterator() {
        return new DataStoreIterator<T>(this);
    }

    @Override
    public synchronized boolean contains(String id) throws DataStoreException {
        throwExceptionIfClosed();
        return false;
    }

    @Override
    public synchronized T get(String id) throws DataStoreException {
        throwExceptionIfClosed();
        return null;
    }

    @Override
    public synchronized Iterator<String> getIds() throws DataStoreException {
        throwExceptionIfClosed();
        return null;
    }

    @Override
    public synchronized int size() throws DataStoreException {
        throwExceptionIfClosed();
        return 0;
    }
    
}
