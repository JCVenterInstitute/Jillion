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
 * Created on Oct 9, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.fastx.fastq;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.util.iter.CloseableIterator;
import org.jcvi.common.core.util.iter.CloseableIteratorAdapter;

public class DefaultFastQDataStore<T extends FastQRecord> implements FastQDataStore<T> {

    private final Map<String, T> map;
    private boolean closed = false;
    
    private void checkNotClosed() throws DataStoreException{
        if(closed){
            throw new DataStoreException("can not access closed dataStore");
        }
    }
    /**
     * @param map
     */
    private DefaultFastQDataStore(Map<String, T> map) {
        this.map = map;
    }

    @Override
    public boolean contains(String id) throws DataStoreException{
        checkNotClosed();
        return map.containsKey(id);
    }

    @Override
    public T get(String id) throws DataStoreException{
        checkNotClosed();
        return map.get(id);
    }

    @Override
    public int size() throws DataStoreException{
        checkNotClosed();
        return map.size();
    }

    @Override
    public CloseableIterator<String> getIds() throws DataStoreException {
        checkNotClosed();
        return CloseableIteratorAdapter.adapt(map.keySet().iterator());
    }

    @Override
    public void close() throws IOException {
        closed=true;
        map.clear();
        
    }

    @Override
    public CloseableIterator<T> iterator() {
        try {
            checkNotClosed();
        } catch (DataStoreException e) {
            throw new IllegalStateException("could not create iterator", e);
        }
        return CloseableIteratorAdapter.adapt(map.values().iterator());
    }
    
    public static class Builder<T extends FastQRecord> implements org.jcvi.common.core.util.Builder<DefaultFastQDataStore<T>>{
        private final Map<String, T> map;
        
        public Builder(){
            map = new LinkedHashMap<String, T>();
        }
        public Builder(int numberOfRecords){
            map = new LinkedHashMap<String, T>(numberOfRecords);
        }
        public Builder put(T fastQRecord){
            map.put(fastQRecord.getId(), fastQRecord);
            return this;
        }
        public Builder remove(T fastQRecord){
            map.remove(fastQRecord.getId());
            return this;
        }
        public Builder putAll(Collection<T> fastQRecords){
            for(T fastQRecord : fastQRecords){
                put(fastQRecord);
            }           
            return this;
        }
        
        public Builder removeAll(Collection<T> fastQRecords){
            for(T fastQRecord : fastQRecords){
                remove(fastQRecord);
            }           
            return this;
        }
        @Override
        public DefaultFastQDataStore<T> build() {
            return new DefaultFastQDataStore<T>(map);
        }
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean isClosed() throws DataStoreException {
        return closed;
    }

}
