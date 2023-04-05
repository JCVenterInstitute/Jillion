package org.jcvi.jillion.internal.core.util;

import java.util.function.Supplier;
/**
 * A Supplier that caches its return value
 * after the first invocation.  
 * If multiple threads attempt to compute this supplier
 * concurrently, the other threads will block while the value
 * is computed.
 * 
 * @author dkatzel
 *
 * @param <T>
 */
public final class MemoizedSupplier<T> {

    public static <T> Supplier<T> memoize(Supplier<T> original) {
        return new Supplier<T>() {
            Supplier<T> delegate = this::firstTime;
            boolean initialized;
            public T get() {
                return delegate.get();
            }
            private synchronized T firstTime() {
                if(!initialized) {
                    T value=original.get();
                    delegate=() -> value;
                    initialized=true;
                }
                return delegate.get();
            }
        };
    }
}
