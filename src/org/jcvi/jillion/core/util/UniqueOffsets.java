package org.jcvi.jillion.core.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    List<Offsets> originals;
    List<Offsets> uniques;
    /**
     * Get the {@link Offsets} that were in both A and B
     */
    Offsets common;

    /**
     * Get the ith Unique Offsets.
     * @param i the ith item to fetch;
     *
     * @return the Offsets to fetch
     * @throws IndexOutOfBoundsException if {@code i} is out of range of the list.
     *
     * @apiNote this is the same as {@code getUniques().get(i)}.
     */
    public Offsets getUnique(int i){
        return getUniques().get(i);
    }

    /**
     * Get the offsets for the ith element
     * that have had their common values removed and shifted.
     *
     * @implNote this is the same as:
     * {@code
     * <pre>
     *      Offsets offsets = originals.get(i).copy();
     *      offsets.removeAllAndShift(getCommon());
     *      return offsets;
     *     </pre>
     * }
     * @param i the ith element.
     * @return a new Offsets which may be empty.
     * @since 6.1.4
     */
    public Offsets getUniqueAndShifted(int i){
        Offsets offsets = originals.get(i).copy();
        offsets.removeAllAndShift(getCommon());
        return offsets;
    }
    /**
     * Factory method to create a new {@link UniqueOffsets} instance.
     * @param offsets the Offsets to compare against.
     * @return a new UniqueOffsets; will never be null.
     *
     * @throws NullPointerException if either a or b are null.
     */
    public static UniqueOffsets between(Offsets... offsets) {
        List<Offsets> list = Stream.of(offsets)
                .map(Offsets::copy)
                .collect(Collectors.toList());

        Iterator<Offsets> iter = list.iterator();
        if(!iter.hasNext()){
            return new UniqueOffsets(list, Collections.emptyList(), Offsets.withInitialCapacity(0));
        }
        Offsets common = iter.next();
        while(iter.hasNext()){
            Offsets other = iter.next();
            common = common.and(other);
        }
        List<Offsets> unique = new ArrayList<>(list.size());
        for(Offsets o : list){
            unique.add(o.xor(common, SPLIT_OPTIONS));
        }

        return new UniqueOffsets(
                list,
                unique,
                common);
    }


}
