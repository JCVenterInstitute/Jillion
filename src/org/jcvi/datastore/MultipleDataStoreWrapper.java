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
package org.jcvi.datastore;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
/**
 * {@code MultipleDataStoreWrapper} is a special proxy to wrap
 * several DataStore instances behind a single iterface.  This
 * class knows how to aggregate DataStore specific methods.
 * @author dkatzel
 *
 *
 */
public class MultipleDataStoreWrapper<T, D extends DataStore<T>> implements InvocationHandler{
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
    public static <T,D extends DataStore<T>> D createMultipleDataStoreWrapper(Class<D> classType,Collection<D> delegates){
        return (D) Proxy.newProxyInstance(classType.getClassLoader(), new Class[]{classType}, 
                new MultipleDataStoreWrapper<T,D>(delegates));
    }
    
    private final List<D> delegates = new ArrayList<D>();
    
    private MultipleDataStoreWrapper(Collection<D> delegates){
        
        if(delegates.size()==0){
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
            return handleSumMethod(method, args);
        }
        if(Iterator.class.equals(returnType)){
            return handleIterator(method, args);
        }
        return returnFirstValidResult(method, args);
        
      
    }
    private Object returnFirstValidResult(Method method, Object[] args) {
        for(D delegate : delegates){
            try {
                Object result = method.invoke(delegate, args);
                if(result !=null){
                    return result;
                }
            } catch (Exception e) {
                //ignore and move on to the next
            } 
        }
        return null;
        
    }
    private Object handleIterator(Method method, Object[] args) throws Throwable{
        List<Iterator<T>> iterators = new ArrayList<Iterator<T>>();
        for(D delegate : delegates){
            iterators.add((Iterator<T>)(method.invoke(delegate, args)));
        }
        return IteratorUtils.chainedIterator(iterators);
    }
    private Object handleSumMethod(Method method, Object[] args) throws Throwable {
        int sum=0;
        for(D delegate : delegates){
            sum+= (Integer)(method.invoke(delegate, args));
        }
        return sum;
    }
    private Object handleBooleanMethod(Method method, Object[] args) throws Throwable {
        for(D delegate : delegates){
            if(((Boolean)method.invoke(delegate, args))){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
    private Object handleVoidMethod(Method method, Object[] args) throws Throwable {
        for(D delegate : delegates){
            method.invoke(delegate, args);
        }
        return null;
        
    }
}
