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
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.datastore;

import java.io.Closeable;

import org.jcvi.common.core.util.iter.CloseableIterator;
/**
 * A {@code DataStore} is an interface for fetching records by
 * an id.  How the data in the datastore is stored is implementation
 * dependent so clients don't have to know how that data is stored.
 * @author dkatzel
 *
 *
 */
public interface DataStore<T> extends Closeable{
	 /**
     * Create a new {@link CloseableIterator}
     * which will iterate over the ids 
     * of all the records
     * in this datastore.
     * @return a new {@link CloseableIterator}
     * instance; never null.
     * @throws DataStoreException if there is a 
     * problem creating this iterator.
     */
    CloseableIterator<String> idIterator() throws DataStoreException;
    /**
     * Get the record in this {@link DataStore} with the given id.
     * @param id the id of the object to fetch.
     * @return the object being fetched, may be null.
     * @throws DataStoreException if there is a problem fetching the
     * data from this Datastore.
     */
    T get(String id) throws DataStoreException;
    /**
     * Does this DataStore contain an object with the given id.
     * @param id the id of the object to check for containment.
     * @return {@code true} if an object with this id exists; {@code false}
     * otherwise.
     * @throws DataStoreException if there is a problem fetching the
     * data from this Datastore.
     */
    boolean contains(String id) throws DataStoreException;
    /**
     * Get the total number of objects in this DataStore.
     * @return the number of objects in this DataStore.
     * @throws DataStoreException if there is a problem fetching the
     * data from this Datastore.
     */
    long getNumberOfRecords() throws DataStoreException;
    /**
     * Has this datastore been closed by the {@link #close()}
     * method?
     * @return {@code true} if this {@link DataStore} is
     * closed; {@code false} otherwise.
     */
    boolean isClosed() throws DataStoreException;
    /**
     * Create a new {@link CloseableIterator}
     * which will iterate over all the records
     * in this {@link DataStore}.
     * @return a new {@link CloseableIterator}
     * instance; never null.
     * @throws DataStoreException if there is a 
     * problem creating this iterator.
     */
    CloseableIterator<T> iterator() throws DataStoreException;
    
}
