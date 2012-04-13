/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Dec 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fastq;

import java.io.IOException;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreIterator;
import org.jcvi.common.core.util.iter.CloseableIterator;

public abstract class AbstractFastqDataStoreAdapter<T> implements DataStore<T>{
    private final DataStore<FastqRecord> dataStore;
    
    
    /**
     * @param dataStore
     */
    public AbstractFastqDataStoreAdapter(DataStore<FastqRecord> dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public boolean contains(String id) throws DataStoreException {
        return dataStore.contains(id);
    }

    

    public DataStore<FastqRecord> getDataStore() {
        return dataStore;
    }

    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        return dataStore.getIds();
    }

    @Override
    public long getNumberOfRecords() throws DataStoreException {
        return dataStore.getNumberOfRecords();
    }

    @Override
    public void close() throws IOException {
        dataStore.close();
        
    }

    @Override
    public boolean isClosed() throws DataStoreException {
        return dataStore.isClosed();
    }

    @Override
    public CloseableIterator<T> iterator() {
        return new DataStoreIterator<T>(this);
    }
}
