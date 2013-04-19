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
 * Created on Apr 22, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.datastore;

import java.io.Closeable;

import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * A {@code DataStore} is an interface which represents a 
 * repository of entity records which can be  fetched by
 * a unique (to this DataStore) id.  How the data in the datastore is stored is implementation
 * dependent.
 * <p/>
 * <strong>NOTE:</strong> DataStores have a method {@link #iterator()} but do not 
 * implement {@link Iterable}.  This is because if the returned {@link StreamingIterator} throws an exception,
 * client code may not always be able to properly clean up resources by
 * explicitly closing the iterator returned by syntatic-sugar uses of {@link Iterable}s.
 * Not closing a {@link StreamingIterator} in a finally block 
 * (or Java 7 try-with resource) can cause deadlock or blocked threads.
 * For example, {@link DataStore}s have been designed not
 * to work with syntatic-sugar uses of {@link Iterable}s
 * such as the Java 5 for-each loop construct.
 * 
 * The code below will not compile since DataStore
 * does not implement {@link Iterable}:
 * <pre>
 * //not allowed since can't directly
 * //access iterator to close if throws Exception
 * for(T record : datastore){
 *   ...
 * }
 * </pre>
 * @author dkatzel
 *
 *
 */
public interface DataStore<T> extends Closeable{
	 /**
     * Create a new {@link StreamingIterator}
     * which will iterate over the ids 
     * of all the records
     * in this {@link DataStore}. The iteration
     * order is guaranteed to match the iteration
     * order by {@link #iterator()}.  The {@link StreamingIterator}
     * is only valid while this {@link DataStore} is open.
     * If the {@link StreamingIterator} is still
     * not finished iterating 
     * when this {@link DataStore} is closed via {@link #close()},
     * then any calls to {@link StreamingIterator#hasNext()}
     * or {@link StreamingIterator#next()} will throw 
     * {@link DataStoreClosedException}.
     * 
     * @return a new {@link StreamingIterator}
     * instance; never null and never contain any null elements,
     * but could be empty if {@link #getNumberOfRecords()} == 0.
     * @throws DataStoreException if there is a 
     * problem creating this iterator.
     * @throws DataStoreClosedException if this {@link DataStore} is closed.
     */
    StreamingIterator<String> idIterator() throws DataStoreException;
    /**
     * Get the record in this {@link DataStore} with the given id.
     * @param id the id of the object to fetch; may not be null.
     * @return the object being fetched, will be null if
     * {@link #contains(String) contains(id)} is false.
     * @throws DataStoreException if there is a problem fetching the
     * data from this {@link DataStore}.
     * @throws DataStoreClosedException if this {@link DataStore} is closed.
     * @throws NullPointerException if id is null.
     */
    T get(String id) throws DataStoreException;
    /**
     * Does this DataStore contain an object with the given id.
     * @param id the id of the object to check for containment; may not be null.
     * @return {@code true} if an object with this id exists; {@code false}
     * otherwise.
     * @throws DataStoreException if there is a problem fetching the
     * data from this {@link DataStore}.
     * @throws DataStoreClosedException if this {@link DataStore} is closed.
     * @throws NullPointerException if id is null.
     */
    boolean contains(String id) throws DataStoreException;
    /**
     * Get the total number of objects in this DataStore.
     * @return the number of objects in this DataStore; always >=0.
     * @throws DataStoreException if there is a problem fetching the
     * data from this {@link DataStore}.
     * @throws DataStoreClosedException if this {@link DataStore} is closed.
     */
    long getNumberOfRecords() throws DataStoreException;
    /**
     * Checks to see if this {@link DataStore} been closed by the {@link #close()}
     * method.
     * @return {@code true} if this {@link DataStore} is
     * closed; {@code false} otherwise.
     */
    boolean isClosed();
    /**
     * Create a new {@link StreamingIterator}
     * which will iterate over all the records
     * in this {@link DataStore}.  The iteration
     * order is guaranteed to match the iteration
     * order by {@link #idIterator()}.
     * The {@link StreamingIterator}
     * is only valid while this {@link DataStore} is open.
     * If the {@link StreamingIterator} is still
     * not finished iterating 
     * when this {@link DataStore} is closed via {@link #close()},
     * then any calls to {@link StreamingIterator#hasNext()}
     * or {@link StreamingIterator#next()} will throw 
     * {@link DataStoreClosedException}.
     * @return a new {@link StreamingIterator}
     * instance; never null and will never contain any null elements;
     * however the returned instance may be empty if {@link #getNumberOfRecords()} ==0.
     * @throws DataStoreException if there is a 
     * problem creating this iterator.
     * @throws DataStoreClosedException if this {@link DataStore} is closed.
     */
    StreamingIterator<T> iterator() throws DataStoreException;
    
}
