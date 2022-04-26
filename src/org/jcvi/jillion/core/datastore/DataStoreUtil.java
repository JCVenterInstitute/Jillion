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
package org.jcvi.jillion.core.datastore;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.ThrowingStream;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.util.streams.ThrowingBiConsumer;
import org.jcvi.jillion.internal.core.datastore.DataStoreStreamingIterator;
import org.jcvi.jillion.internal.core.util.Caches;
import org.jcvi.jillion.internal.core.util.Sneak;
import org.jcvi.jillion.sam.SamRecord;
/**
 * Utility class containing static
 * factory methods to  adapt {@link DataStore}s
 * into another types of {@link DataStore}s.
 * @author dkatzel
 *
 */
public final class DataStoreUtil {

	private DataStoreUtil(){
		//can not instantiate
	}
	
	/**
	 * {@code AdapterCallback} is a callback
	 * method that can convert one type into 
	 * another.   The {@link DataStoreUtil#adapt(Class, DataStore, Function)}
	 * factory method will call
	 * {@link AdapterCallback#get(Object)} to adapt the records
	 * in the original {@link DataStore} on all calls
	 * to {@link DataStore#get(String)} and the {@link StreamingIterator#next()}
	 * calls to the iterator returned by {@link DataStore#iterator()}.
	 * 
	 * 
	 * @author dkatzel
	 *
	 * @param <F> the "From" type.  This is the type that the original datastore has its type as
     * @param <T> the "To" type.  This is the type that we want to convert the type F into which may
     * require method calls or new object creation.
	 */
	public interface AdapterCallback<F,T> extends Function<F, T>{
		/**
		 * Get the adapted type from the original type.
		 * @param from the object to adapt;
		 * will never be null.
		 * @return an instance of the adapted type,
		 * can not be null.  If this implementation
		 * returns null, then the adapted
		 * DataStore
		 * will throw a NullPointerException.
		 */
		T get(F from);
		
		@Override
        default T apply(F from){
			return get(from);
		}
	}
	
	
	/**
     * Create a new {@link DataStore} instance using the data of the given {@link Map}.
     * The entries in the given map are copied into a new private map so any future
     * manipulations to the input map will not affect the returned {@link DataStore}.
     * The order of entries return by the {@link DataStore#idIterator()}
     * and {@link DataStore#iterator()} are determined by the iteration
     * order of input Map <strong>at the time this method is called</strong>.
     * @param map the map to adapt into a {@link DataStore}.
     * @return a new DataStore instance.
     * @throws NullPointerException if map is null, or if any keys or values in the map
     * are null.
     * @param <T> The type of Records in the DataStore.
     */
    public static <T> DataStore<T> adapt(Map<String, T> map){
    	return new MapDataStoreAdapter<T>(map);
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
     */
	public static <T, D extends DataStore<T>> D adapt(Class<D> datastoreInterface, Map<String, T> map){
    	return adapt(datastoreInterface, DataStore.of(map));
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
     */

    @SuppressWarnings("unchecked")
	public static final <T, D extends DataStore<T>> D adapt(Class<D> datastoreInterface, DataStore<T> delegate){
    	return (D) Proxy.newProxyInstance(datastoreInterface.getClassLoader(), new Class<?>[]{datastoreInterface},
    			new DataStoreInvocationHandler<T>(delegate));
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
     */
    @SuppressWarnings("unchecked")
	public static final <F, T, D extends DataStore<T>> D adapt(Class<D> datastoreInterface, DataStore<F> delegate, Function<F, T> callback){
    	return (D) Proxy.newProxyInstance(datastoreInterface.getClassLoader(), new Class<?>[]{datastoreInterface},
    			new DataStoreInvocationHandler<T>(new AdaptedDataStore<F, T>(delegate, callback)));
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
     */
    public static <T,D extends DataStore<T>> DataStore<T> chain(Collection<D> datastores){
	       return new WrapperDataStore<T,D>(datastores);
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
     */
    public static <T,D extends DataStore<T>> D chain(Class<D> datastoreInterface,Collection<D> datastores){
	       DataStore<T> wrappedDataStore = new WrapperDataStore<T,D>(datastores);
	       return adapt(datastoreInterface, wrappedDataStore);
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
     */
    @SuppressWarnings("unchecked")
    public static <D extends DataStore<?>> D createNewCachedDataStore(Class<D> c,D delegate, int cacheSize){
        return (D) Proxy.newProxyInstance(c.getClassLoader(), new Class<?>[]{c, CacheableDataStore.class}, 
                new CachedDataStoreInvocationHandler<D>(delegate,cacheSize));
    }
    
    
    /**
     * Clears the cache from a DataStore created by this utility
     * class if it has a cache; This method does nothing
     * if the given datastore is not a {@link CacheableDataStore}.
     * @param cachedDataStore a DataStore that was created by this
     * utility (implements {@link CacheableDataStore}.
     * @see DataStoreUtil#isACachedDataStore(DataStore)
     */
    public static void clearCacheFrom(DataStore<?> cachedDataStore){
        if(isACachedDataStore(cachedDataStore)){
            ((CacheableDataStore<?>)cachedDataStore).clearCache();
        }
    }
    /**
     * Is the given DataStore a Cached DataStore created by
     * {@link DataStoreUtil#createNewCachedDataStore(Class, DataStore, int)}.
     * @param cachedDataStore a DataStore that may or may not have
     * been created by this
     * utility (implements {@link CacheableDataStore}.
     * @return {@code true} if this is a {@link CacheableDataStore};
     * {@code false} otherwise.
     */
    public static boolean isACachedDataStore(DataStore<?> cachedDataStore){
        return cachedDataStore instanceof CacheableDataStore;
    }
    private static class AdaptedDataStore<F, T> implements DataStore<T>{
    	private final DataStore<F> delegate;
    	private final Function<F,T> callback;
    	
		public AdaptedDataStore(DataStore<F> delegate,Function<F,T> callback) {
			this.delegate = delegate;
			this.callback =callback;
		}

		@Override
		public void close() throws IOException {
			delegate.close();
			
		}

		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			return delegate.idIterator();
		}

		@Override
		public T get(String id) throws DataStoreException {
			F original = delegate.get(id);
			if(original==null){
				return null;
			}
			return getResultFromCallback(original);
		}

		private T getResultFromCallback(F original) {
			T ret= callback.apply(original);
			if(ret==null){
				throw new NullPointerException("return value of call back can not be null");
				
			}
			return ret;
		}

		@Override
		public StreamingIterator<DataStoreEntry<T>> entryIterator()
				throws DataStoreException {
			return new StreamingIterator<DataStoreEntry<T>>(){
				StreamingIterator<DataStoreEntry<F>> delegateIterator = delegate.entryIterator();

				@Override
				public boolean hasNext() {
					return delegateIterator.hasNext();
				}

				@Override
				public void close() {
					delegateIterator.close();
				}

				@Override
				public DataStoreEntry<T> next() {
					DataStoreEntry<F> next = delegateIterator.next();
					String key = next.getKey();
					T ret= callback.apply(next.getValue());
					return new DataStoreEntry<T>(key, ret);
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException("remove not supported");					
				}
				
				
			};
			
		}

		@Override
		public boolean contains(String id) throws DataStoreException {
			return delegate.contains(id);
		}

		@Override
		public long getNumberOfRecords() throws DataStoreException {
			return delegate.getNumberOfRecords();
		}

		@Override
		public boolean isClosed() {
			return delegate.isClosed();
		}

		@Override
		public StreamingIterator<T> iterator() throws DataStoreException {
			return new StreamingIterator<T>() {
				private final StreamingIterator<F> iter = delegate.iterator();

				@Override
				public boolean hasNext() {
					return iter.hasNext();
				}

				@Override
				public void close(){
					iter.close();
					
				}

				@Override
				public T next() {
					return getResultFromCallback(iter.next());
				}

				@Override
				public void remove() {
					iter.remove();					
				}
				
			};
		}
    }
	
	private static class DataStoreInvocationHandler<T> implements InvocationHandler{
		private final DataStore<T> delegate;

		public DataStoreInvocationHandler(DataStore<T> delegate) {
			this.delegate = delegate;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			//we need to wrap the call of invoke and catch any
			//InvocationTargetExceptions which the dynamic
			//proxy uses to wrap undeclared thrown checked exceptions.
			//By rethrowing the cause (the checked exception)
			//we rethrow the original exception and
			//maintain the contract of the object
			//being proxied.
			//see
			//http://amitstechblog.wordpress.com/2011/07/24/java-proxies-and-undeclaredthrowableexception/
			//for a more complete description.
			try{
			   return method.invoke(delegate, args);
			}catch(InvocationTargetException e){			       
				throw e.getCause();
			}
		}
		
		
	}
	
	
	/**
	 * {@code MapDataStoreAdapter} is a utility class
	 * that can adapt a {@code Map<String,T>} into a {@code DataStore<T>}.
	 * 
	 * @author dkatzel
	 *
	 * @param <T> the type of values returned by the datastore.
	 */
	private static final class MapDataStoreAdapter<T> implements DataStore<T>{
		private volatile boolean isClosed;
	    
	    private final Map<String, T> map = new LinkedHashMap<String, T>();
	    
	    
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
        public <E extends Throwable> void forEach(ThrowingBiConsumer<String, T, E> consumer) throws IOException, E {
           map.forEach((k,v) ->{
               try{
                   consumer.accept(k, v);
               }catch(Throwable e){
                   Sneak.sneakyThrow(e);
               }
           });
        }


        @Override
		public ThrowingStream<T> records() throws DataStoreException {
			return ThrowingStream.asThrowingStream(map.values().stream());
		}


		@Override
	    public boolean contains(String id) throws DataStoreException {
	    	if(id ==null){
	    		throw new NullPointerException("id can not be null");
	    	}
	    	throwExceptionIfClosed();
	        return map.containsKey(id);
	    }
	    @Override
	    public T get(String id) throws DataStoreException {
	    	if(id ==null){
	    		throw new NullPointerException("id can not be null");
	    	}
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

		private final void throwExceptionIfClosed() {
			if (isClosed) {
				throw new DataStoreClosedException("DataStore is closed");
			}
		}

		@Override
		public final void close() throws IOException {
			isClosed = true;
		}

		@Override
        public final boolean isClosed() {
			return isClosed;
		}
		
		@Override
		public StreamingIterator<T> iterator() {
			throwExceptionIfClosed();
			return DataStoreStreamingIterator.create(this, map.values().iterator());
			 
		}
		@Override
		public StreamingIterator<DataStoreEntry<T>> entryIterator()
				throws DataStoreException {
			return IteratorUtil.createStreamingIterator(map.entrySet().iterator(),
					new IteratorUtil.TypeAdapter<Entry<String,T>, DataStoreEntry<T>>() {

						@Override
						public DataStoreEntry<T> adapt(Entry<String, T> from) {
							return new DataStoreEntry<T>(from.getKey(), from.getValue());
						}
					
				}
					);
		}
		
		
	}
	
	
		
	private static class WrapperDataStore<T, D extends DataStore<T>> implements DataStore<T>{

		private final List<D> delegates;
		
		
		public WrapperDataStore(Collection<D> delegates) {
			
			if(delegates.isEmpty()){
				throw new IllegalArgumentException("must be at least one DataStore");
			}
			this.delegates = new ArrayList<D>(delegates);
			for(DataStore<T> delegate : delegates){
				if(delegate==null){
					throw new NullPointerException("DataStore can not be null");
				}
			}
		}

		@Override
		public void close() throws IOException {
			for(DataStore<T> delegate : delegates){
				IOUtil.closeAndIgnoreErrors(delegate);
			}
			
		}

		@Override
		public StreamingIterator<DataStoreEntry<T>> entryIterator()
				throws DataStoreException {
			List<StreamingIterator<DataStoreEntry<T>>> iterators = new ArrayList<StreamingIterator<DataStoreEntry<T>>>();
			for(DataStore<T> delegate : delegates){
				iterators.add(delegate.entryIterator());
			}
			return IteratorUtil.createChainedStreamingIterator(iterators);
		}

		@Override
		public StreamingIterator<String> idIterator() throws DataStoreException {
			List<StreamingIterator<String>> iterators = new ArrayList<StreamingIterator<String>>();
			for(DataStore<T> delegate : delegates){
				iterators.add(delegate.idIterator());
			}
			return IteratorUtil.createChainedStreamingIterator(iterators);
		}

		@Override
		public T get(String id) throws DataStoreException {
			if(id ==null){
	    		throw new NullPointerException("id can not be null");
	    	}
			for(DataStore<T> delegate : delegates){
				T ret= delegate.get(id);
				if(ret !=null){
					return ret;
				}
			}
			return null;
		}

		@Override
		public boolean contains(String id) throws DataStoreException {
			for(DataStore<T> delegate : delegates){
				if(delegate.contains(id)){
					return true;
				}
			}
			return false;
		}

		@Override
		public long getNumberOfRecords() throws DataStoreException {
			long total=0L;
			for(DataStore<T> delegate : delegates){
				total +=delegate.getNumberOfRecords();
			}
			return total;
		}

		@Override
		public boolean isClosed() {
			for(DataStore<T> delegate : delegates){
				if(delegate.isClosed()){
					return true;
				}
			}
			return false;
		}

		@Override
		public StreamingIterator<T> iterator() throws DataStoreException {
			List<StreamingIterator<T>> iterators = new ArrayList<StreamingIterator<T>>();
			for(DataStore<T> delegate : delegates){
				iterators.add(delegate.iterator());
			}
			return IteratorUtil.createChainedStreamingIterator(iterators);
		}
		
	}

	
	/**
	 * {@code CachedDataStore} uses the Java Proxy classes to
	 * wrap a given DataStore instance with a cache for objects returned
	 * by {@link DataStore#get(String)}.
	 * @author dkatzel
	 *
	 *
	 */
	private static final class CachedDataStoreInvocationHandler <D extends DataStore<?>> implements InvocationHandler{

	    private final D delegate;
	    private final Map<String, Object> cache;
	    private static final Class<?>[] GET_PARAMETERS = new Class<?>[]{String.class};
	   
	    private CachedDataStoreInvocationHandler(D delegate, int cacheSize){
	        this.delegate = delegate;
	        cache= Caches.createSoftReferencedValueLRUCache(cacheSize);
	    }
	   
	    @Override
	    public synchronized Object invoke(Object proxy, Method method, Object[] args)
	            throws Throwable {
	    	try{
		        final String methodName = method.getName();
		        if("close".equals(methodName) && args==null){
		            cache.clear();
		        }
		        else if("clearCache".equals(methodName) && args==null){
		            cache.clear();
		            return null;
		        }
		        else if("get".equals(methodName) && Arrays.equals(GET_PARAMETERS,method.getParameterTypes())){
		            String id = (String)args[0];
		            //we are have to check for null
		            //because the object could be removed
		            //between checking and getting
		            //even in a synchronized block?
		            Object result =cache.get(id);
		            if(result !=null){
		                return result;
		            }
		            
		            Object obj =method.invoke(delegate, args);
		            if(obj==null){
		            	//we don't put nulls in our cache because
			            //it might kickout something else
			            //and cause us to refetch anyway
			            //since we assume a null value means it's not in the cache.
		            	return null;
		            }		            
		            cache.put(id, obj);
		            return obj;
		        }
		        return method.invoke(delegate, args);
	    	}catch(InvocationTargetException e){
	    		throw e.getCause();
	    	}
	    }   
	    
	}
	/**
     * {@code CacheableDataStore} is an interface that is used
     * for Cached objects created by {@link DataStoreUtil#createNewCachedDataStore(Class, DataStore, int)}.
     * This way it is possible to determine
     * at runtime if a given {@link DataStore} has a cache.
     * @author dkatzel
     */
    public interface CacheableDataStore<T> extends DataStore<T>{
        /**
         * Clears the cache without
         * closing the datastore.
         */
        void clearCache();
    }
    
    /**
     * Adapter method to convert a StreamingIterator of Ts into a StreamingIterator
     * of {@code DataStoreEntry<T>}.
     * 
     * @param iterator the StreamingIterator to adapt.
     * @param keyFunction the function to get the datstore id of T.
     * @since 6.0
     */
    public static <T> StreamingIterator<DataStoreEntry<T>> asDataStoreEntryIterator(StreamingIterator<T> iterator, Function<T, String> keyFunction){
    	Objects.requireNonNull(keyFunction);
    	return new StreamingIterator<DataStoreEntry<T>>(){
            
            @Override
            public boolean hasNext() {
                    return iterator.hasNext();
            }

            @Override
            public void close() {
            	iterator.close();
            }

            @Override
            public DataStoreEntry<T> next() {
                T next = iterator.next();
                return new DataStoreEntry<>(keyFunction.apply(next), next);
            }

            @Override
            public void remove() {
                    iterator.remove();
            }
            
    };
    }
}
