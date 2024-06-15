package org.jcvi.jillion.core.util.streams;
/**
 * Functional interface that takes 2 parameters, a primitive index (offset)
 * and the object at that offset.
 * @author dkatzel
 *
 * @param <T> the type to consume.
 */
@FunctionalInterface
public interface IntIndexedConsumer<T> {

    void accept(int index, T t);
}
