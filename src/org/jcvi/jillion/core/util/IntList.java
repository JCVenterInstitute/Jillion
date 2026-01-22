package org.jcvi.jillion.core.util;

import org.jcvi.jillion.internal.core.util.ArrayUtil;

import java.util.List;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

/**
 * Interface for a List of primitive ints.
 *
 * @since 6.1
 */
public interface IntList extends List<Integer> {

    /**
     * Get a new {@link java.util.PrimitiveIterator.OfInt} primitive int
     * iterator (not threadsafe).
     * @return a new OfInt.
     */
    PrimitiveIterator.OfInt intIterator();

    int getAsInt(int index);

    default boolean addInt(int value){
        return add(value);
    }

    default void forEach(IntConsumer consumer){
        Objects.requireNonNull(consumer);
        PrimitiveIterator.OfInt iter = intIterator();
        while(iter.hasNext()){
            consumer.accept(iter.nextInt());
        }
    }
    default void addInt(int index, int value){
        add(index, value);
    }

    IntStream intStream();

    /**
     * Get a new {@link java.util.PrimitiveIterator.OfInt} primitive int
     * iterator (not threadsafe) that iterates BACKWARDs over the values.
     * @return a new OfInt.
     *
     * @since 6.1.1
     */
    PrimitiveIterator.OfInt reverseIntIterator();
}
