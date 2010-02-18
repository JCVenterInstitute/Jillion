/*
 * Created on Oct 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
/**
 * {@code MultipleWrapper} uses dymanic proxies to wrap
 * several instances of an interface.  This allows
 * all wrapped instances to be called by only a single
 * call to the wrapper.
 * @author dkatzel
 */
public class  MultipleWrapper<T> implements InvocationHandler{
    /**
     * Since methods can only return a single
     * return value, only one of the wrapped
     * methods can be returned to the caller (even though
     * they will all be called).
     * @author dkatzel
     */
    public enum ReturnPolicy{
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
     * @throws NullpointerException if classType ==null or policy ==null or any delegate ==null.
     */
    public static <T, I extends T> T createMultipleWrapper(Class<T> classType,ReturnPolicy policy, I... delegates){
        
        return (T) Proxy.newProxyInstance(classType.getClassLoader(), new Class[]{classType}, 
                new MultipleWrapper<T>(policy,delegates));
    }
    /**
     * Convenience constructor which is the same as calling
     * {@link #createMultipleWrapper(Class, ReturnPolicy, Object...)
     * createMultipleWrapper(classType,ReturnPolicy.RETURN_FIRST,delegates)}
     * @see #createMultipleWrapper(Class, ReturnPolicy, Object...)
     */
    public static <T,I extends T> T createMultipleWrapper(Class<T> classType,I... delegates){
       return createMultipleWrapper(classType,ReturnPolicy.RETURN_FIRST,delegates);
    }
    
    private final ReturnPolicy policy;
    private final List<T> delegates = new ArrayList<T>();
    
    private MultipleWrapper(ReturnPolicy policy,T ... delegates){
        if(policy==null){
            throw new NullPointerException("policy can not be null");
        }
        if(delegates.length==0){
            throw new IllegalArgumentException("must wrap at least one delegate");
        }
        this.policy = policy;
        for(T delegate : delegates){
            if(delegate ==null){
                throw new NullPointerException("delegate can not be null");
            }
            this.delegates.add(delegate);
        }        
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        List returns = new ArrayList(delegates.size());
        for(T delegate :delegates){
            returns.add(method.invoke(delegate, args));
        }
        if(policy == ReturnPolicy.RETURN_LAST){
            return returns.get(returns.size()-1);
        }
        return returns.get(0);
      
    }
    
}
