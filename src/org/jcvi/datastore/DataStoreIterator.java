/*
 * Created on Jun 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.util.Iterator;

public class DataStoreIterator<T> implements Iterator<T>{
    private final Iterator<String> ids; 
    private final DataStore<T> dataStore;
    public DataStoreIterator(DataStore<T> dataStore){
        this.dataStore =  dataStore;
        try {
            ids = dataStore.getIds();
        } catch (DataStoreException e) {
            throw new IllegalStateException("could not iterate over ids", e);
        }
    }
    @Override
    public boolean hasNext() {
        return ids.hasNext();
    }

    @Override
    public T next() {
        try {
            return dataStore.get(ids.next());
        } catch (DataStoreException e) {
            throw new IllegalStateException("could not get next element", e);
        }
    }

    @Override
    public void remove() {
       throw new UnsupportedOperationException("can not remove");
        
    }
}
