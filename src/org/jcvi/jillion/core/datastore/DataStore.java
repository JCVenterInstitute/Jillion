/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;
/**
 * A {@code DataStore} is an interface which represents a 
 * repository of entity records which can be  fetched by
 * a unique (to this DataStore) id.  How the data in the datastore is stored is implementation
 * dependent.
 * <p>
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
     * Get the ids of all the records as a {@link ThrowingStream}.
     * @apiNote this is the same as {@code idIterator().toThrowingStream()}.
     * 
     * @return a new ThrowingStream; will never be null, and never contain
     * null elements but may be empty.
     * 
     * @throws DataStoreException if there is a 
     * problem creating this iterator.
     * @throws DataStoreClosedException if this {@link DataStore} is closed.
     * 
     * @since 5.3
     */
    default ThrowingStream<String> ids() throws DataStoreException{
        return idIterator().toThrowingStream();
    }
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
     * @return the number of objects in this DataStore; always &ge; 0.
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
    
    /**
     * Create a new {@link StreamingIterator}
     * which will iterate over all the {@link DataStoreEntry}s
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
    StreamingIterator<DataStoreEntry<T>> entryIterator() throws DataStoreException;
    
    /**
     * Create a new {@link ThrowingStream} of the records
     * in this DataStore.
     * @return a new ThrowingStream.
     * @throws DataStoreException if there is a problem creating this stream.
     * 
     * @apiNote Jillion Version 5.3 changed the return type to be {@link ThrowingStream} instead of Stream.
     * @since 5.0
     */
    default ThrowingStream<T> records() throws DataStoreException{
    	return iterator().toThrowingStream();
    }
    
    /**
     * Create a new {@link ThrowingStream} of the {@link DataStoreEntry}s
     * in this {@link DataStore}.
     * @return a new ThrowingStream.
     * @throws DataStoreException if there is a problem creating this stream.
     * 
     * @since 5.3
     */
    default ThrowingStream<DataStoreEntry<T>> entries() throws DataStoreException{
        return entryIterator().toThrowingStream();
    }
    
    /**
     * Convenience method to adapt this datastore into another datastore type.
     * @param datastoreInterface the {@link Class} of the DataStore interface to adapt into; can not be null.
     * @param adapter a {@link Function} to convert an element of this datastore into the equivalent 
     * in the adapted datastore; can not be null.
     * @return a new DataStore; will never be null.
     * 
     * @implNote this is the same as calling {@code DataStoreUtil.adapt(datastoreInterface, this, adapter);}
     * 
     * @throws NullPointerException if any parameter is null.
     * 
     * @see DataStoreUtil#adapt(Class, DataStore, Function)
     * 
     * @since 5.2
     */
    default <E, D extends DataStore<E>> D adapt(Class<D> datastoreInterface, Function<T, E> adapter){
        return DataStoreUtil.adapt(datastoreInterface, this, adapter);
    }
    /**
     * Iterate through all the records in the datastore
     * and call the given consumer on each one.
     * 
     * @param consumer a BiConsumer that takes the id of the record as the first parameter
     * and the record as the second parameter.
     * 
     * @throws IOException if there is a problem iterating through the datastore.
     * 
     * @implNote By default, this is the same as calling:
     * <pre>
     *  try(StreamingIterator{@code <DataStoreEntry<T>>} iter = entryIterator()){
            while(iter.hasNext()){
                {@code DataStoreEntry<T>} entry = iter.next();
                consumer.accept(entry.getKey(), entry.getValue());
            }
        }
     * </pre>
     * 
     * But datastore implementations may override this method
     * to create a more efficient traversal.
     * 
     * @since 5.3
     */
    default <E extends Throwable> void  forEach(ThrowingBiConsumer<String, T, E> consumer) throws IOException, E{
        try(StreamingIterator<DataStoreEntry<T>> iter = entryIterator()){
            while(iter.hasNext()){
                DataStoreEntry<T> entry = iter.next();
                consumer.accept(entry.getKey(), entry.getValue());
            }
        }
    }
    
}
