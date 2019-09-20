package org.jcvi.jillion.core.util.streams;

/**
 * A {@link java.util.function.Function} that can throw an exception.
 * @author dkatzel
 *
 * @param <T> the type the function accepts.
 * @param <R> the type the function returns.
 * @param <E> the exception that can be thrown.
 *
 * @since 5.3.2
 */
@FunctionalInterface
public interface ThrowingFunction<T, R,  E extends Throwable>{
    R apply(T t) throws E;

}
