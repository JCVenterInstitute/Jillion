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
package org.jcvi.common.core.datastore;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;

import org.jcvi.common.core.util.Caches;

/**
 * {@code CachedDataStore} uses the Java Proxy classes to
 * wrap a given DataStore instance with a cache for objects returned
 * by {@link DataStore#get(String)}.
 * @author dkatzel
 *
 *
 */
public final class CachedDataStore <D extends DataStore<?>> implements InvocationHandler{

    private final D delegate;
    private final Map<String, Object> cache;
    private static final Class<?>[] GET_PARAMETERS = new Class<?>[]{String.class};
    
    /**
     * Clears the cache from a DataStore created by this utilty
     * class.
     * @param cachedDataStore a DataStore that was created by this
     * utility (implements {@link Cacheable}.
     * @throws IllegalArgumentException if the datastore is not
     * {@link Cacheable}.
     */
    public static void clearCacheFrom(DataStore<?> cachedDataStore){
        if(cachedDataStore instanceof Cacheable){
            ((Cacheable)cachedDataStore).clearCache();
        }
        
       
    }
    /**
     * Create a new Proxy wrapping the given DataStore.  The returned
     * object is similar to the wrapped dataStore except it also
     * implements an additional interface {@link Cacheable}
     * AND all {@link DataStore#get(String)} results are cached
     * in an Least Recently Used (LRU) SoftReference cache of the specified size.  This will
     * keep the Most recent X records in memory as long as the JVM doesn't
     * need the memory for other things.
     * @param <D> interface of DataStore to proxy
     * @param c class object of D
     * @param delegate instance of DataStore
     * @param cacheSize the size of the cache used to keep most recently
     * "gotten" objects.
     * @return a proxy instance of type D which wraps the given delegate and also
     * implements Cacheable.
     */
    @SuppressWarnings("unchecked")
    public static <D extends DataStore<?>> D create(Class<? super D> c,D delegate, int cacheSize){
        try {
            c.getMethod("get", GET_PARAMETERS).getReturnType();
        } catch (Exception e) {
            throw new IllegalArgumentException("delegate does not have a 'get' method", e);
        } 
        
        return (D) Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c, Cacheable.class}, 
                new CachedDataStore<D>(delegate,cacheSize));
    }
   
    private CachedDataStore(D delegate, int cacheSize){
        this.delegate = delegate;
        cache= Caches.createSoftReferencedValueLRUCache(cacheSize);
    }
   
    @Override
    public synchronized Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
    	try{
	        final String methodName = method.getName();
	        if("clearCache".equals(methodName) && args==null){
	            cache.clear();
	            return null;
	        }
	        if("close".equals(methodName) && args==null){
	            cache.clear();
	        }
	        else if("get".equals(methodName) && Arrays.equals(GET_PARAMETERS,method.getParameterTypes())){
	            String id = (String)args[0];
	            Object result =cache.get(id);
	            if(result !=null){
	                return result;
	            }
	            
	            Object obj =method.invoke(delegate, args);
	            
	            cache.put(id, obj);
	            return obj;
	        }
	        return method.invoke(delegate, args);
    	}catch(InvocationTargetException e){
    		throw e.getCause();
    	}
    }   
    
}
