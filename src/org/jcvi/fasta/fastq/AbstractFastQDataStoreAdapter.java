/*
 * Created on Dec 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta.fastq;

import java.io.IOException;
import java.util.Iterator;

import org.jcvi.datastore.DataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.datastore.DataStoreIterator;

public abstract class AbstractFastQDataStoreAdapter<T> implements DataStore<T>{
    private final DataStore<FastQRecord> dataStore;
    
    
    /**
     * @param dataStore
     */
    public AbstractFastQDataStoreAdapter(DataStore<FastQRecord> dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return dataStore.contains(id);
    }

    

    public DataStore<FastQRecord> getDataStore() {
        return dataStore;
    }

    @Override
    public Iterator<String> getIds() throws DataStoreException {
        return dataStore.getIds();
    }

    @Override
    public int size() throws DataStoreException {
        return dataStore.size();
    }

    @Override
    public void close() throws IOException {
        dataStore.close();
        
    }

    @Override
    public Iterator<T> iterator() {
        return new DataStoreIterator<T>(this);
    }
}
