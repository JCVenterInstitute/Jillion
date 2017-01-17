package org.jcvi.jillion.core.util;

import java.io.Closeable;

import org.jcvi.jillion.core.io.IOUtil;

/**
 * A tuple pair used to return a 2-tuple return value.
 * @author dkatzel
 *
 */
public class Pair<T,U> implements AutoCloseable{

    private final T first;
    private final U second;
    
    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    @Override
    public void close() {
        if(first instanceof Closeable){
            IOUtil.closeAndIgnoreErrors((Closeable) first);
        }
        if(second instanceof Closeable){
            IOUtil.closeAndIgnoreErrors((Closeable) second);
        }
        
    }
    
    
}
