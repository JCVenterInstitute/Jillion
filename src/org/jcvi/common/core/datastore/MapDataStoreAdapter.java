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
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.datastore;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code MapDataStoreAdapter} is a utility class
 * that can adapt a {@code Map<String,T>} into a {@code DataStore<T>}.
 * 
 * @author dkatzel
 *
 * @param <T> the type of values returned by the datastore.
 */
public final class MapDataStoreAdapter<T> implements DataStore<T> {
	private volatile boolean isClosed;
    
    private final Map<String, T> map = new LinkedHashMap<String, T>();
    /**
     * Create a new DataStore instance using the data of the given map.
     * the entries in the given map are copied into a new private map so any future
     * manipulations to the input map will not affect the returned DataStore.
     * @param map the map to adapt into a datastore.
     * @return a new DataStore instance.
     * @throws NullPointerException if map is null, or if any keys or values in the map
     * are null.
     */
    public static <T> DataStore<T> adapt(Map<String, T> map){
    	return new MapDataStoreAdapter<T>(map);
    }
    private MapDataStoreAdapter(Map<String, T> map){
    	for(Entry<String, T> entry : map.entrySet()){
    		String key = entry.getKey();
    		if(key==null){
    			throw new NullPointerException("null keys not allowed");
    		}
    		T value = entry.getValue();
    		if(value==null){
    			throw new NullPointerException("null values not allowed");
    		}
    		this.map.put(key, value);
    	}
    }
    @Override
    public boolean contains(String id) throws DataStoreException {
    	throwExceptionIfClosed();
        return map.containsKey(id);
    }
    @Override
    public T get(String id) throws DataStoreException {
    	throwExceptionIfClosed();
        return map.get(id);
    }
    @Override
    public StreamingIterator<String> idIterator() throws DataStoreException {
    	throwExceptionIfClosed();
    	return DataStoreStreamingIterator.create(this, map.keySet().iterator());
    }
    @Override
    public long getNumberOfRecords() throws DataStoreException {
    	throwExceptionIfClosed();
        return map.size();
    }
	
	 
	    protected final void throwExceptionIfClosed() {
	        if(isClosed){
	            throw new IllegalStateException("DataStore is closed");
	        }
	    }

	    @Override
	    public final void close() throws IOException {
	        isClosed = true;
	    }

	    public final boolean isClosed() {
	        return isClosed;
	    }
	
	@Override
	public StreamingIterator<T> iterator() {
		throwExceptionIfClosed();
		return DataStoreStreamingIterator.create(this, map.values().iterator());
		 
	}
}
