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
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * {@code MultipleWrapper} uses dymanic proxies to wrap
 * several instances of an interface.  This allows
 * all wrapped instances to be called by only a single
 * call to the wrapper.
 * @author dkatzel
 */
public final class  MultipleWrapper<T> implements InvocationHandler{
    
	private final ReturnPolicy policy;
    private final List<T> delegates = new ArrayList<T>();
	/**
     * Since methods can only return a single
     * return value, only one of the wrapped
     * methods can be returned to the caller (even though
     * they will all be called).
     * @author dkatzel
     */
    public static enum ReturnPolicy{
        /**
         * Return the first wrapped instance.
         */
        RETURN_FIRST,
        /**
         * Return the last wrapped instance.
         */
        RETURN_LAST
    }
    /**
     * Create a dynamic proxy to wrap the given delegate instances.
     * @param <T> the interface to proxy.
     * @param <I> the instances of T.
     * @param classType the class object of T.
     * @param policy the return policy to use on methods that return something.
     * @param delegates the list of delegates to wrap in the order in which 
     * they will be called.
     * @return a new instance of T that wraps the delegates.
     * @throws IllegalArgumentException if no delegates are given
     * @throws NullPointerException if classType ==null or policy ==null or any delegate ==null.
     */
    @SuppressWarnings("unchecked")
    public static <T, I extends T> T createMultipleWrapper(Class<T> classType,ReturnPolicy policy, Iterable<I> delegates){
        
        return (T) Proxy.newProxyInstance(classType.getClassLoader(), new Class[]{classType}, 
                new MultipleWrapper<T>(policy,delegates));
    }
    /**
     * Convenience constructor which is the same as calling
     * {@link #createMultipleWrapper(Class, ReturnPolicy, Object...)
     * createMultipleWrapper(classType,ReturnPolicy.RETURN_FIRST,delegates)}
     * @see #createMultipleWrapper(Class, ReturnPolicy, Object...)
     */
    public static <T,I extends T> T createMultipleWrapper(Class<T> classType,Iterable<I> delegates){
       return createMultipleWrapper(classType,ReturnPolicy.RETURN_FIRST,delegates);
    }
    /**
     * Convenience constructor which is the same as calling
     * {@link #createMultipleWrapper(Class, ReturnPolicy, Object...)
     * createMultipleWrapper(classType,ReturnPolicy.RETURN_FIRST,delegates)}
     * @see #createMultipleWrapper(Class, ReturnPolicy, Object...)
     */
    public static <T,I extends T> T createMultipleWrapper(Class<T> classType,I... delegates){
       return createMultipleWrapper(classType,ReturnPolicy.RETURN_FIRST,Arrays.asList(delegates));
    }
    /**
     * Convenience constructor which is the same as calling
     * {@link #createMultipleWrapper(Class, ReturnPolicy, Object...)
     * createMultipleWrapper(classType,ReturnPolicy.RETURN_FIRST,delegates)}
     * @see #createMultipleWrapper(Class, ReturnPolicy, Object...)
     */
    public static <T,I extends T> T createMultipleWrapper(Class<T> classType,ReturnPolicy policy,I... delegates){
       return createMultipleWrapper(classType,policy,Arrays.asList(delegates));
    }
    
    
    private MultipleWrapper(ReturnPolicy policy,Iterable<? extends T> delegates){
        if(policy==null){
            throw new NullPointerException("policy can not be null");
        }
        
        this.policy = policy;
        for(T delegate : delegates){
            if(delegate ==null){
                throw new NullPointerException("delegate can not be null");
            }
            this.delegates.add(delegate);
        }    
        if(this.delegates.size()==0){
            throw new IllegalArgumentException("must wrap at least one delegate");
        }
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        List<Object> returns = new ArrayList<Object>(delegates.size());
        try{
	        for(T delegate :delegates){
	            returns.add(method.invoke(delegate, args));
	        }
	        if(policy == ReturnPolicy.RETURN_LAST){
	            return returns.get(returns.size()-1);
	        }
	        return returns.get(0);
        }catch(InvocationTargetException e){
        	throw e.getCause();
        }
      
    }
    
}
