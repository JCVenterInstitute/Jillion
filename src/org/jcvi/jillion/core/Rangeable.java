/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code Rangeable} is a interface
 * to denote that an object can be expressed
 * as a {@link Range}.
 * @author dkatzel
 *
 */
public interface Rangeable {
        /**
         * Get this {@link Rangeable} expressed
         * as a {@link Range}.
         * @return a {@link Range} object, the
         * return value might be cached.
         */
	Range asRange();
	/**
	 * Get the length of this Rangeable.
	 * 
	 * @return the length.
	 * 
	 * @implNote by default, this method should return
	 * {@code asRange().getLength();}.
	 * 
	 * @since 5.2
	 * 
	 * @see Range#getLength()
	 */
	default long getLength(){
	    return asRange().getLength();
	}
	/**
         * Get the begin coordinate of this Rangeable.
         * 
         * @return the begin coordinate as a long.
         * 
         * @implNote by default, this method should return
         * {@code asRange().getBegin();}.
         * 
         * @since 5.2
         * 
         * @see Range#getBegin()
         */
	default long getBegin(){
	    return asRange().getBegin();
	}
	/**
         * Get the end coordinate of this Rangeable.
         * 
         * @return the end coordinate as a long.
         * 
         * @implNote by default, this method should return
         * {@code asRange().getEnd();}.
         * 
         * @since 5.2
         * 
         * @see Range#getEnd()
         */
	default long getEnd(){
	    return asRange().getEnd();
	}
	/**
     * Is this Rangeable empty.
     * 
     * @return {@code true} if this
     * Rangeable is empty; {@code false} otherwise..
     * 
     * @implNote by default, this method should return
     * {@code asRange().isEmpty();}.
     * 
     * @since 5.2
     * 
     * @see Range#isEmpty()
     * @see #isNotEmpty()
     */
	default boolean isEmpty(){
	    return asRange().isEmpty();
	}
	
	/**
     * Is this Rangeable NOT empty.  This is mostly useful
     * when filtering streams for non-empty Ranges.
     * 
     * @return {@code true} if this
     * Rangeable is not empty; {@code false} otherwise..
     * 
     * @implNote by default, this method should return
     * {@code !isEmpty()}.
     * 
     * @since 6.0
     * 
     * @see Range#isEmpty()
     */
	default boolean isNotEmpty(){
	    return !isEmpty();
	}
    /**
     * Checks to see if the given {@link Rangeable} intersects this one. An
     * empty range will never intersect any other range (even itself).
     * 
     * @param target
     *            The {@link Rangeable} to check.
     * @return <code>true</code> if the coordinates of the two ranges overlap
     *         each other in at least one point.
     * @throws NullPointerException
     *             if target is null.
     * 
     * @since 5.3
     */
    default boolean intersects(Rangeable target) {
        return asRange().intersects(target.asRange());
    }


    /**
     * Get the List of Ranges that represents the 
     * {@code this - other}.  This is similar to the 
     * Set of all coordinates that don't intersect. 
     * 
     * @param other the range to complement with.
     * 
     * @return a List of {@link Ranges}; will never be null but may be empty.
     * 
     * @since 5.3
     */
    default List<Range> complement(Rangeable other){
        return asRange().complement(other.asRange());
    }
    
    /**
     * Get the List of Ranges that represents 
     * {@code this - others}.  This is similar to the 
     * Set of all coordinates in this Range
     * that do not intersect the ranges in others.
     * If the Ranges contained in others extends beyond
     * this Range, then only coordinates within 
     * this Range are returned.
     * @param others the ranges to complement from.
     * @return a List of Ranges; may be empty
     * if this Range is entirely covered by others.
     * @throws NullPointerException if others is null.
     * 
     * @since 5.3
     */
    default List<Range> complementOf(Collection<? extends Rangeable> others){
        List<Range> otherRanges = others.stream().map(Rangeable::asRange).collect(Collectors.toList());
        return asRange().complement(otherRanges);
    }

}
