/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Aug 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.core.datastore;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreClosedException;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * {@code AbstractDataStore} is an abstract implementation
 * of {@link DataStore} that manages
 * the datastore transitions between the open 
 * and close state via {@link #close()} method.
 * All DataStore methods will first check to make sure that the datastore 
 * is not yet closed before delegating to a helper method that is implemented
 * by subclasses.  All DataStore methods are final so subclasses
 * can not accidentally or maliciously override them to break the {@link DataStore}
 * contract of throwing exceptions if already closed.
 * 
 * For example, subclasses need to implement {@link #getImpl(String)}
 * to perform the actual act of getting a record out of the datastore.
 * {@link AbstractDataStore#get(String)} method is final and is implemented
 * with the following pseudocode:
 * <pre>
 *  public final T get(String id) throws DataStoreException {
        throwExceptionIfClosed();
        return getImpl(id);
    }
 * </pre>
 * @author dkatzel
 *
 * @param <T> the type of element in the DataStore.
 */
public abstract class  AbstractDataStore<T> implements DataStore<T>{
    private volatile boolean isClosed;
    
    private final void throwExceptionIfClosed() {
        if(isClosed){
            throw new DataStoreClosedException("DataStore is closed");
        }
    }
    /**
     * This method is called
     * when a datastore is closed
     * via the {@link #close()}.
     * Implementations should
     * use this method to handle
     * any resource cleanup.
     */
    protected abstract void handleClose() throws IOException;
    @Override
    public final synchronized void close() throws IOException {
    	if(!isClosed){
    		handleClose();
    	}
        isClosed = true;
    }
    @Override
    public final boolean isClosed() {
        return isClosed;
    }
    
    @Override
    public final StreamingIterator<T> iterator() throws DataStoreException {
    	throwExceptionIfClosed();
        return iteratorImpl();
    }
    /**
     * Does this DataStore contain the given id.
     * This method is delegated to by {@link #contains(String)}
     * after determining that the DataStore is still open.
     * @param id the id to look for; 
     * @return {@code true} if the DataStore does contain
     * this id; {@code false} otherwise.
     * @throws DataStoreException if there is a problem
     * determining if the datastore contains the id.
     * @see DataStore#contains(String)
     */
    protected abstract boolean containsImpl(String id) throws DataStoreException;
    /**
     * Get the record in this {@link DataStore} with the given id.
     * This method is delegated to by {@link #get(String)}
     * @param id the id of the object to fetch.
     * @return the object being fetched, will be null if
     * {@link #contains(String) contains(id)} is false.
     * @throws DataStoreException if there is a problem fetching the
     * data from this {@link DataStore}.
     * @throws IllegalStateException if this {@link DataStore} is closed.
     * @see DataStore#get(String)
     */
    protected abstract T getImpl(String id) throws DataStoreException;
    /**
     * Get the total number of objects in this DataStore.
     * This method is delegated to by {@link #getNumberOfRecords()}.
     * @return the number of objects in this DataStore; always >=0.
     * @throws DataStoreException if there is a problem fetching the
     * data from this {@link DataStore}.
     * @throws IllegalStateException if this {@link DataStore} is closed.
     * @see DataStore#getNumberOfRecords()
     */
    protected abstract long getNumberOfRecordsImpl() throws DataStoreException;
    /**
     * Create a new {@link StreamingIterator}
     * which will iterate over the ids 
     * of all the records
     * in this {@link DataStore} which 
     * preserves the contract of the Id iterator in
     * {@link DataStore#idIterator()}. 
     * This method is delegated to by {@link #idIterator()}.
     * 
     * @return a new {@link StreamingIterator}
     * instance; never null and never contain any null elements,
     * but could be empty if {@link #getNumberOfRecords()} == 0.
     * @throws DataStoreException if there is a 
     * problem creating this iterator.
     * @throws IllegalStateException if this {@link DataStore} is closed.
     * @see DataStore#idIterator()
     */
    protected abstract StreamingIterator<String> idIteratorImpl() throws DataStoreException;
    /**
     * Create a new {@link StreamingIterator}
     * which will iterate over all the records
     * in this {@link DataStore} which 
     * preserves the contract of the iterator in
     * {@link DataStore#iterator()}. 
     * This method is delegated to by {@link #iterator()}.
     * 
     * @return a new {@link StreamingIterator}
     * instance; never null and never contain any null elements,
     * but could be empty if {@link #getNumberOfRecords()} == 0.
     * @throws DataStoreException if there is a 
     * problem creating this iterator.
     * @throws IllegalStateException if this {@link DataStore} is closed.
     * @see DataStore#iterator()
     */
    protected abstract StreamingIterator<T> iteratorImpl() throws DataStoreException;
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public final boolean contains(String id) throws DataStoreException {
    	if(id ==null){
    		throw new NullPointerException("id can not be null");
    	}
        throwExceptionIfClosed();
        return containsImpl(id);
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public final T get(String id) throws DataStoreException {
    	if(id ==null){
    		throw new NullPointerException("id can not be null");
    	}
        throwExceptionIfClosed();
        return getImpl(id);
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public final StreamingIterator<String> idIterator() throws DataStoreException {
        throwExceptionIfClosed();
        return idIteratorImpl();
    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public final long getNumberOfRecords() throws DataStoreException {
        throwExceptionIfClosed();
        return getNumberOfRecordsImpl();
    }
    
}
