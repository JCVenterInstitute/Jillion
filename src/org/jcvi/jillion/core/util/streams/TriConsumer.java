package org.jcvi.jillion.core.util.streams;
/**
 * A functional interface that consumes 3 parameters
 * and can throw an Exception.
 * @author dkatzel
 *
 * @param <T> the first parameter.
 * @param <U> the second parameter.
 * @param <V> the third parameter.
 * 
 * @since 6.0
 */
@FunctionalInterface
public interface TriConsumer<T,U, V> {
	/**
     * Consume the given 3 parameters and throw an exception if needed.
     * @param t the first parameter.
     * @param u the second parameter.
     * @param v the third parameter.
     */
     void accept(T t, U u, V v);
}
