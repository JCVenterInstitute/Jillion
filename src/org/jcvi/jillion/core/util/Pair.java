package org.jcvi.jillion.core.util;

import java.io.Closeable;
import java.util.Objects;
import java.util.function.Supplier;

import org.jcvi.jillion.core.io.IOUtil;

/**
 * A tuple pair used to return a 2-tuple return value.
 * @author dkatzel
 *
 */
public class Pair<T,U> implements AutoCloseable{

    private final Supplier<T> first;
    private final Supplier<U> second;
    
    public Pair(T first, U second) {
        this.first = () -> first;
        this.second = () ->second;
    }
    
    

    public Pair(Supplier<T> first, Supplier<U> second) {
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
    }



    public T getFirst() {
        return first.get();
    }

    public U getSecond() {
        return second.get();
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
