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
         * Rangeable is emtpy; {@code false} otherwise..
         * 
         * @implNote by default, this method should return
         * {@code asRange().isEmpty();}.
         * 
         * @since 5.2
         * 
         * @see Range#isEmpty()
         */
	default boolean isEmpty(){
	    return asRange().isEmpty();
	}
}
