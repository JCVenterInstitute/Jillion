package org.jcvi.jillion.core.util.streams;

/**
 * A {@link java.util.function.Supplier} that can throw an exception.
 * @author dkatzel
 *
 * @param <T> the type the supplier returns.
 * @param <E> the exception that can be thrown.
 * 
 * @since 6.0
 */
@FunctionalInterface
public interface ThrowingSupplier <T, E extends Throwable>{
    T get() throws E;

}
