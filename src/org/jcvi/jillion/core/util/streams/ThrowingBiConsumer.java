package org.jcvi.jillion.core.util.streams;

/**
 * A functional interface that consumes 2 parameters
 * and can throw an Exception.
 * @author dkatzel
 *
 * @param <T> the first parameter.
 * @param <U> the second parameter.
 * @param <E> the exception that may be thrown.
 */
@FunctionalInterface
public interface ThrowingBiConsumer<T,U, E extends Throwable> {
    /**
     * Consume the given 2 parameters and throw an exception if needed.
     * @param t the first parameter.
     * @param u the second parameter.
     * @throws E the exception to throw if there is a problem.
     */
     void accept(T t, U u) throws E;
}
