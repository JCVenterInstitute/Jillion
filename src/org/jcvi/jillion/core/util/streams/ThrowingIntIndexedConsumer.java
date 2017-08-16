package org.jcvi.jillion.core.util.streams;
/**
 * Functional interface that takes 2 parameters, a primitive index (offset)
 * and the object at that offset.
 * @author dkatzel
 *
 * @param <T> the type to consume.
 * @param <E> the exception that could be thrown.
 *
 * @since 5.3
 */
@FunctionalInterface
public interface ThrowingIntIndexedConsumer<T, E extends Throwable> {

    void accept(int index, T t) throws E;
}
