package org.jcvi.jillion.core.util.streams;
/**
 * Functional interface that takes 2 parameters, a primitive index (offset)
 * and the object at that offset.
 * @author dkatzel
 *
 * @param <T> the type to consume.
 * @param <E> the exception that could be thrown.
 */
@FunctionalInterface
public interface ThrowingIndexedConsumer<T, E extends Throwable> {

    void accept(long index, T t) throws E;
}
