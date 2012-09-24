package org.jcvi.common.core.datastore;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.jcvi.common.core.util.iter.StreamingIterator;

/**
 * {@code DataStoreAdapter} is a utility
 * class that is able to convert
 * a DataStore of one type into a DataStore
 * of another.  
 * @author dkatzel
 *
 */
public final class DataStoreAdapter {
	/**
	 * {@code AdapterCallback} is a callback
	 * method that can convert one type into 
	 * another.  The {@link DataStoreAdapter} will call
	 * {@link AdapterCallback#get(Object)} to adapt the records
	 * in the original datastore on all calls
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
	public static interface AdapterCallback<F,T>{
		/**
		 * Get the adapted type from the original type.
		 * @param from the object to adapt;
		 * will never be null.
		 * @return an instance of the adapted type,
		 * can not be null.  If this implementation
		 * returns null, then {@link DataStoreAdapter}
		 * will throw a NullPointerException.
		 */
		public T get(F from);
	}
	/**
     * Create a new DataStore instance of the given
     * type T using the data of the given DataStore which
     * contains records of the correct type.
     * This factory method uses the Java Proxy classes
     * to create a new implementation of the given interface
     * which then delegates all cals to the given datastore.  This factory class
     * can only implement methods that conform to the DataStore interface,
     * if the given interface has extension methods that do
     * not exist in the given delegated DataStore, then trying to call
     * those methods will throw an illegalArgumentException.
     * @param datastoreInterface the interface to proxy;
     * can not be null.
     * @param delegate the original {@link DataStore} to adapt into a different type of {@link DataStore}.
     * @return a new DataStore instance which implements the given datastoreInterface;
     * can not be null.
     * @throws NullPointerException if datastoreInterface is null, delegate is null.
     */
    @SuppressWarnings("unchecked")
	public static final <T, D extends DataStore<T>> D adapt(Class<D> datastoreInterface, DataStore<T> delegate){
    	return (D) Proxy.newProxyInstance(datastoreInterface.getClassLoader(), new Class[]{datastoreInterface},
    			new DataStoreInvocationHandler<T>(delegate));
    }
    
    /**
     * /**
     * Create a new DataStore instance of the given
     * type T using the data of the given DataStore which
     * contains records of the correct type.
     * This factory method uses the Java Proxy classes
     * to create a new implementation of the given interface
     * which then delegates all cals to the given datastore.  This factory class
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
	public static final <F, T, D extends DataStore<T>> D adapt(Class<D> datastoreInterface, DataStore<F> delegate, AdapterCallback<F, T> callback){
    	return (D) Proxy.newProxyInstance(datastoreInterface.getClassLoader(), new Class[]{datastoreInterface},
    			new DataStoreInvocationHandler<T>(new AdaptedDataStore<F, T>(delegate, callback)));
    }
    
    private static class AdaptedDataStore<F, T> implements DataStore<T>{
    	private final DataStore<F> delegate;
    	private final AdapterCallback<F,T> callback;
    	
		public AdaptedDataStore(DataStore<F> delegate,AdapterCallback<F,T> callback) {
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
			T ret= callback.get(original);
			if(ret==null){
				throw new NullPointerException("return value of call back can not be null");
				
			}
			return ret;
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
				public void close() throws IOException {
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
}
