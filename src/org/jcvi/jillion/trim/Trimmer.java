package org.jcvi.jillion.trim;

import org.jcvi.jillion.core.Range;
/**
 * Examines an input object and returns 
 * the {@link Range} of the good portion.
 * 
 * @author dkatzel
 *
 * @param <T> the type to be trimmed.
 * @since 5.3
 * 
 */
public interface Trimmer<T> {

    /**
     * Find the Good Range to keep for the given object.
     * @param t the object to examine.
     * 
     * @return a {@link Range} of the portion of the sequence to keep.
     * will never be null but may be empty if there is no portion
     * of the sequence to keep.
     * 
     * @throws NullPointerException if the given builder is null.
     */
    Range trim(T t);
}
