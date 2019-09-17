package org.jcvi.jillion.core.util;

import java.util.function.Supplier;

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
