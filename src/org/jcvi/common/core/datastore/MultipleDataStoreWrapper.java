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
 * Created on Dec 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.datastore;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jcvi.common.core.util.iter.IteratorUtil;
import org.jcvi.common.core.util.iter.StreamingIterator;
/**
 * {@code MultipleDataStoreWrapper} is a special proxy to wrap
 * several DataStore instances behind a single iterface.  This
 * class knows how to aggregate DataStore specific methods.
 * @author dkatzel
 *
 *
 */
public final class MultipleDataStoreWrapper<T, D extends DataStore<T>> implements InvocationHandler{
    /**
     * These are the parameters in the {@link DataStore#get(String)} method signature.
     */
    private static final Class<?>[] GET_PARAMETERS = new Class[]{String.class};
    
    private final List<D> delegates = new ArrayList<D>();
    
    /**
     * Create a dynamic proxy to wrap the given delegate {@link DataStore} instances.
     * @param <T> the interface Type of objects in the DataStores.
     * @param <D> the DataStore interface to proxy.
     * @param classType the class object of D.
     * @param delegates the list of delegates to wrap in the order in which 
     * they will be called.
     * @return a new instance of D that wraps the delegates.
     * @throws IllegalArgumentException if no delegates are given
     * @throws NullpointerException if classType ==null or or any delegate ==null.
     */
    public static <T,D extends DataStore<T>> D createMultipleDataStoreWrapper(Class<D> classType,D... delegates){

        return createMultipleDataStoreWrapper(classType, Arrays.asList(delegates));
    }
    /**
     * Create a dynamic proxy to wrap the given delegate {@link DataStore} instances.
     * @param <T> the interface Type of objects in the DataStores.
     * @param <D> the DataStore interface to proxy.
     * @param classType the class object of D.
     * @param delegates the list of delegates to wrap in the order in which 
     * they will be called.
     * @return a new instance of D that wraps the delegates.
     * @throws IllegalArgumentException if no delegates are given
     * @throws NullpointerException if classType ==null or or any delegate ==null.
     */
    @SuppressWarnings("unchecked")
    public static <T,D extends DataStore<T>> D createMultipleDataStoreWrapper(Class<D> classType,Collection<D> delegates){
        return (D) Proxy.newProxyInstance(classType.getClassLoader(), new Class[]{classType}, 
                new MultipleDataStoreWrapper<T,D>(delegates));
    }
    
   
    
    private MultipleDataStoreWrapper(Collection<D> delegates){
        
        if(delegates.isEmpty()){
            throw new IllegalArgumentException("must wrap at least one delegate");
        }
        
        for(D delegate : delegates){
            if(delegate ==null){
                throw new NullPointerException("delegate can not be null");
            }
            this.delegates.add(delegate);
        }        
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        final Class<?> returnType = method.getReturnType();
        if(void.class.equals(returnType)){
            return handleVoidMethod(method, args);            
        }
        if(boolean.class.equals(returnType)){
            return handleBooleanMethod(method, args);
        }
        if(int.class.equals(returnType)){
            return handleIntSumMethod(method, args);
        }
        if(long.class.equals(returnType)){
            return handleLongSumMethod(method, args);
        }
        if(Iterator.class.isAssignableFrom(returnType)){
            return handleIterator(method, args);
        }
        return returnFirstValidResult(method, args);
        
      
    }
    
   
    
    private Object returnFirstValidResult(Method method, Object[] args) throws DataStoreException {
        for(D delegate : delegates){
            try {
                Object result = method.invoke(delegate, args);
                if(result !=null){
                    return result;
                }
            } catch (IllegalArgumentException e) {
                throw new DataStoreException("error invoking delegate datastore",e);
            } catch (IllegalAccessException e) {
                throw new DataStoreException("error invoking delegate datastore",e);
            } catch (InvocationTargetException e) {
            	if(e.getCause() instanceof DataStoreException){
            		throw (DataStoreException)e.getCause();
            	}
                throw new DataStoreException("error invoking delegate datastore",e);
            }      
        }
        return null;
        
    }
    private Object handleIterator(Method method, Object[] args) throws Throwable{
    	try{
	        List<StreamingIterator<T>> iterators = new ArrayList<StreamingIterator<T>>();
	        for(D delegate : delegates){
	            @SuppressWarnings("unchecked")
	            final Iterator<T> delegateIterator = (Iterator<T>)method.invoke(delegate, args);
	            if(delegateIterator instanceof StreamingIterator){
	                iterators.add((StreamingIterator<T>)delegateIterator);
	            }else{
	                iterators.add(IteratorUtil.createStreamingIterator(delegateIterator));
	            }
	        }
	        return IteratorUtil.createChainedStreamingIterator(iterators);
    	}catch(InvocationTargetException e){
    		throw e.getCause();
    	}
    }
    private Object handleIntSumMethod(Method method, Object[] args) throws Throwable {
    	try{
	        int sum=0;
	        for(D delegate : delegates){
	            sum+= (Integer)(method.invoke(delegate, args));
	        }
	        return sum;
    	}catch(InvocationTargetException e){
    		throw e.getCause();
    	}
    }
    private Object handleLongSumMethod(Method method, Object[] args) throws Throwable {
    	try{
	        long sum=0;
	        for(D delegate : delegates){
	            sum+= ((Long)(method.invoke(delegate, args))).longValue();
	        }
	        return sum;
    	}catch(InvocationTargetException e){
    		throw e.getCause();
    	}
    }
    private Object handleBooleanMethod(Method method, Object[] args) throws Throwable {
        try{
	    	for(D delegate : delegates){
	            if(((Boolean)method.invoke(delegate, args))){
	                return Boolean.TRUE;
	            }
	        }
	        return Boolean.FALSE;
        }catch(InvocationTargetException e){
        	throw e.getCause();
        }
    }
    private Object handleVoidMethod(Method method, Object[] args) throws Throwable {
    	try{
	        for(D delegate : delegates){
	            method.invoke(delegate, args);
	        }
	        return null;
    	}catch(InvocationTargetException e){
    		throw e.getCause();
    	}
        
    }
}
