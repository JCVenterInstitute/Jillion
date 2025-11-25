package org.jcvi.jillion.core.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * A wrapper class around the comparison of 2 different offsets
 * and what values are unique to each one and what values are in common.
 *
 * @since 6.1
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UniqueOffsets {

    private static final Offsets.XorOptions SPLIT_OPTIONS = Offsets.XorOptions.builder()
            .shift(false)
            .include(false)
            .build();
    /**
     * Get the Unique values in A.
     */
    Offsets a;
    /**
     * Get the Unique values in B.
     */
    Offsets b;
    /**
     * Get the {@link Offsets} that were in both A and B
     */
    Offsets common;

    /**
     * Factory method to create a new {@link UniqueOffsets} instance.
     * @param a the first Offsets to compare; can not be null.
     * @param b the second Offsets to compare; can not be null.
     * @return a new UniqueOffsets; will never be null.
     *
     * @throws NullPointerException if either a or b are null.
     */
    public static UniqueOffsets between(Offsets a, Offsets b) {
        Offsets uniqueA = a.xor(b, SPLIT_OPTIONS);
        return new UniqueOffsets(
                uniqueA,
                b.xor(a, SPLIT_OPTIONS),
                a.xor(uniqueA, SPLIT_OPTIONS));
    }


}
