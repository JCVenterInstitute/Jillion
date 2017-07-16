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
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

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
    
    
    /**
     * Create a new {@link DataStore} instance using the data of the given {@link Map}.
     * The entries in the given map are copied into a new private map so any future
     * manipulations to the input map will not affect the returned {@link DataStore}.
     * The order of entries return by the {@link DataStore#idIterator()}
     * and {@link DataStore#iterator()} are determined by the iteration
     * order of input Map <strong>at the time this method is called</strong>.
     * 
     * @param map the map to adapt into a {@link DataStore}.
     * @return a new DataStore instance.
     * @throws NullPointerException if map is null, or if any keys or values in the map
     * are null.
     * @param <T> The type of Records in the DataStore.
     * 
     * @since 5.3
     */
    public static <T> DataStore<T> of(Map<String, T> map){
        return DataStoreUtil.adapt(map);
    }
    
    /**
     * Create a new {@link DataStore} instance of the given
     * type D using the data of the given map.
     * The entries in the given map are copied into a new private map so any future
     * manipulations to the input map will not affect the returned {@link DataStore}.
     * The order of entries return by the {@link DataStore#idIterator()}
     * and {@link DataStore#iterator()} are determined by the iteration
     * order of input Map <strong>at the time this method is called</strong>.
     * <p>
     * This factory method uses the Java Dynamic Proxy classes
     * to create a new implementation of the given interface
     * which uses the map as a backing store.  This factory class
     * can only implement methods that conform to the DataStore interface,
     * if the given DataStore interface has extension methods that are not
     * part of the core DataStore interface then trying to call
     * those methods will throw an illegalArgumentException.
     * @param datastoreInterface the interface to proxy; can not be null.
     * @param map the map to adapt into a datastore; can not be null.
     * @return a new DataStore instance which implements the given datastoreInterface and contains
     * the records in the input Map.
     * @throws NullPointerException if datastoreInterface is null, map is null, or if any keys or values in the map
     * are null.
     * @throws IllegalArgumentException if the given datastoreInterface is not a public interface 
     * or violates the constraints set by {@link Proxy#getProxyClass(ClassLoader, Class...)}
     * @see Proxy#getProxyClass(ClassLoader, Class...)
     * @param <T> the type of record in the returned dataStore and the type of the values in the given Map.
     * @param <D> the type of DataStore to return (created using a dynamic proxy)
     * 
     * @since 5.3
     */
    public static <T, D extends DataStore<T>> D of(Map<String, T> map, Class<D> datastoreInterface){
        return DataStoreUtil.adapt(datastoreInterface, map);
    }
    
    /**
     * Create a new Dynamic Proxy wrapping the given DataStore.  The returned
     * object is similar to the wrapped dataStore except
     * all {@link DataStore#get(String)} results are cached
     * in an Least Recently Used (LRU) SoftReference cache of the specified size
     * and an additional interface, {@link CacheableDataStore} has been added
     * to the list of interfaces the returned {@link DataStore} implements.  This will
     * keep the Most recent {@code cacheSize} records in memory as long as the JVM doesn't
     * need the memory for other things.
     * @param <D> interface of DataStore to proxy
     * @param c class object of D
     * @param delegate instance of DataStore
     * @param cacheSize the size of the cache used to keep most recently
     * "gotten" objects.
     * @return a proxy instance of type D which wraps the given delegate
     * and caches all results returned by get in an LRU cache.
     * @see #clearCacheFrom(DataStore)
     * @see #isACachedDataStore(DataStore)
     * 
     * @since 5.3
     */
    public static <D extends DataStore<?>> D cache(Class<D> c,D delegate, int cacheSize){
        return DataStoreUtil.createNewCachedDataStore(c, delegate, cacheSize);
    }
    
    /**
     * Create a new DataStore instance of the given
     * type T using the data of the given DataStore which
     * contains records of the correct type.
     * This factory method uses the Java Proxy classes
     * to create a new implementation of the given interface
     * which then delegates all calls to the given datastore.  This factory class
     * can only implement methods that conform to the DataStore interface,
     * if the given interface has extension methods that do
     * not exist in the given delegated DataStore, then trying to call
     * those methods will throw an illegalArgumentException.
     * @param datastoreInterface the interface to proxy;
     * can not be null.
     * @param delegate the original {@link DataStore} to adapt into a different type of {@link DataStore};
     * can not be null.
     * @param callback an instance of {@link AdapterCallback} used to adapt
     * records of type {@literal <F>} into records of type {@literal <T>};
     * can not be null.
     * @return a new DataStore instance which implements the given datastoreInterface.
     * @throws NullPointerException if datastoreInterface is null, delegate is null, or if callback is null.
     *
     * @param <F> the "From" type.  This is the type that the original datastore has its type as
     * @param <T> the "To" type.  This is the type that we want to convert the type F into which may
     * require method calls or new object creation.
     * @param <D> the Database interface type we want the returned datastore to mimic.
     * 
     * @since 5.3
     */
    public static <F, T, D extends DataStore<T>> D adapt(Class<D> datastoreInterface, DataStore<F> delegate, Function<F, T> callback){
        return DataStoreUtil.adapt(datastoreInterface, delegate, callback);
    }
    
    /**
     * Create a new {@link DataStore} instance of the given
     * type D using which wraps the given DataStore
     * in a new Dynamic Proxy class which mimics the desired DataStore.
     * This is useful to convert a {@code DataStore<T>} into a different
     * DataStore subinterface which has the same {@link DataStore#get(String)} signature.
     * All method calls on the returned proxy Datastore are derived by delegating to the input DataStore.
     * Closing the input DataStore will also close this Proxy DataStore and vice versa.
     * The order of entries return by the {@link DataStore#idIterator()}
     * and {@link DataStore#iterator()} are determined by the 
     * the iteration
     * order of input DataStore.
     * <p>
     * This factory method uses the Java Dynamic Proxy classes
     * to create a new implementation of the given interface
     * which wraps the input DataStore as a backing store.  This factory class
     * can only implement methods that conform to the input DataStore interface,
     * if the return DataStore interface has extension methods that are not
     * part of the input DataStore interface then trying to call
     * those methods will throw an illegalArgumentException.
     * @param datastoreInterface the interface to proxy;
     * can not be null.
     * @param delegate the original {@link DataStore} to adapt into a different type of {@link DataStore}.
     * @throws NullPointerException if datastoreInterface is null, delegate is null.
     * @return a new DataStore instance which implements the given datastoreInterface and contains
     * the records in the input DataStore.
     * @throws NullPointerException if datastoreInterface is null, map is null, or if any keys or values in the map
     * are null.
     * @throws IllegalArgumentException if the given datastoreInterface is not a public interface 
     * or violates the constraints set by {@link Proxy#getProxyClass(ClassLoader, Class...)}
     * @see Proxy#getProxyClass(ClassLoader, Class...)
     * @param <T> the type of record both the input and output dataStores.
     * @param <D> the type of DataStore to return (created using a dynamic proxy)
     * 
     * @since 5.3
     */
     public static <T, D extends DataStore<T>> D adapt(Class<D> datastoreInterface, DataStore<T> delegate){
        return DataStoreUtil.adapt(datastoreInterface, delegate);
    }
     
     /**
      * Create a new DataStore that contains the entire contents of each of the 
      * input DataStores.  The order of the input DataStores is the order
      * that these DataStores will be chained together.  This results in the following
      * contract: 
      * <ul>
      * <li> Calls to {@link DataStore#get(String)} or {@link DataStore#contains(String)}
      * will check each DataStore for the given record until the record is found
      * or all DataStores are checked.</li>
      * <li> Calls to {@link DataStore#getNumberOfRecords()}
      * will return a combined total over all the datastores.</li>
      * <li> Calls to {@link DataStore#iterator()} and {@link DataStore#idIterator()}
      * will chain the iterators of each DataStore one after the other.  When the first datastore's
      * iterator is finished, then {@link StreamingIterator#next()} will move onto the iterator
      * from the next DataStore etc.  </li>
      * <li>Closing the returned DataStore will close all the input DataStores</li>
      * </ul>
      * <p>
      * This is a useful method for combining several different DataStore objects to appear
      * as a single DataStore.  For example combining several sequence input files (possibly in different
      * file formats) which have been parsed into DataStores can be adapted into a single 
      * chained DataStore for processing.  The fact that the sequence data comes from multiple
      * files (objects) has been abstracted away.
      * <p>
      * This factory method uses the Java Proxy classes
      * to create a new implementation of the given interface
      * which then delegates all calls to the given datastore.  This factory class
      * can only implement methods that conform to the DataStore interface,
      * if the given interface has extension methods that do
      * not exist in the given delegated DataStore, then trying to call
      * those methods will throw an illegalArgumentException.
      * @param datastores the datastores to chain together; can not be null or empty.
      * @param <T> the type of record both the input and output dataStores.
      * @param <D> the type of DataStore to return (created using a dynamic proxy)
      * @return a new instance of type D.
      * 
      * @since 5.3
      */
     public static <T,D extends DataStore<T>> DataStore<T> chain(Collection<D> datastores){
                return DataStoreUtil.chain(datastores);
         }
     
     /**
      * Create a new DataStore that contains the entire contents of each of the 
      * input DataStores.  The order of the input DataStores is the order
      * that these DataStores will be chained together.  This results in the following
      * contract: 
      * <ul>
      * <li> Calls to {@link DataStore#get(String)} or {@link DataStore#contains(String)}
      * will check each DataStore for the given record until the record is found
      * or all DataStores are checked.</li>
      * <li> Calls to {@link DataStore#getNumberOfRecords()}
      * will return a combined total over all the datastores.</li>
      * <li> Calls to {@link DataStore#iterator()} and {@link DataStore#idIterator()}
      * will chain the iterators of each DataStore one after the other.  When the first datastore's
      * iterator is finished, then {@link StreamingIterator#next()} will move onto the iterator
      * from the next DataStore etc.  </li>
      * <li>Closing the returned DataStore will close all the input DataStores</li>
      * </ul>
      * <p>
      * This is a useful method for combining several different DataStore objects to appear
      * as a single DataStore.  For example combining several sequence input files (possibly in different
      * file formats) which have been parsed into DataStores can be adapted into a single 
      * chained DataStore for processing.  The fact that the sequence data comes from multiple
      * files (objects) has been abstracted away.
      * <p>
      * This factory method uses the Java Proxy classes
      * to create a new implementation of the given interface
      * which then delegates all calls to the given datastore.  This factory class
      * can only implement methods that conform to the DataStore interface,
      * if the given interface has extension methods that do
      * not exist in the given delegated DataStore, then trying to call
      * those methods will throw an illegalArgumentException.
      * @param datastores the datastores to chain together; can not be null or empty.
      * @param <T> the type of record both the input and output dataStores.
      * @param <D> the type of DataStore to return (created using a dynamic proxy)
      * @return a new instance of type D.
      * 
      * @since 5.3
      */
     @SafeVarargs
    public static <T,D extends DataStore<T>> DataStore<T> chain(D...datastores){
                return DataStoreUtil.chain(Arrays.asList(datastores));
         }
     /**
      * Create a new DataStore that contains the entire contents of each of the 
      * input DataStores.  The order of the input DataStores is the order
      * that these DataStores will be chained together.  This results in the following
      * contract: 
      * <ul>
      * <li> Calls to {@link DataStore#get(String)} or {@link DataStore#contains(String)}
      * will check each DataStore for the given record until the record is found
      * or all DataStores are checked.</li>
      * <li> Calls to {@link DataStore#getNumberOfRecords()}
      * will return a combined total over all the datastores.</li>
      * <li> Calls to {@link DataStore#iterator()} and {@link DataStore#idIterator()}
      * will chain the iterators of each DataStore one after the other.  When the first datastore's
      * iterator is finished, then {@link StreamingIterator#next()} will move onto the iterator
      * from the next DataStore etc.  </li>
      * <li>Closing the returned DataStore will close all the input DataStores</li>
      * </ul>
      * <p>
      * This is a useful method for combining several different DataStore objects to appear
      * as a single DataStore.  For example combining several sequence input files (possibly in different
      * file formats) which have been parsed into DataStores can be adapted into a single 
      * chained DataStore for processing.  The fact that the sequence data comes from multiple
      * files (objects) has been abstracted away.
      * <p>
      * This factory method uses the Java Proxy classes
      * to create a new implementation of the given interface
      * which then delegates all calls to the given datastore.  This factory class
      * can only implement methods that conform to the DataStore interface,
      * if the given interface has extension methods that do
      * not exist in the given delegated DataStore, then trying to call
      * those methods will throw an illegalArgumentException.
      * @param datastoreInterface the interface to proxy;
      * can not be null.
      * @param datastores the datastores to chain together; can not be null or empty.
      * @param <T> the type of record both the input and output dataStores.
      * @param <D> the type of DataStore to return (created using a dynamic proxy)
      * @return a new instance of type D.
      * 
      * @since 5.3
      */
     public static <T,D extends DataStore<T>> D chain(Class<D> datastoreInterface,Collection<D> datastores){
        return DataStoreUtil.chain(datastoreInterface, datastores);
         }
     
}
