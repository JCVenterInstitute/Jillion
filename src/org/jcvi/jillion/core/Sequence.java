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
/*
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;


/**
 * {@code Sequence} is an interface for an
 * ordered list of objects.  How
 * this sequence is stored is abstracted
 * away so that different implementations
 * may encode or compress the symbols
 * so that they take up less memory.
 * @author dkatzel
 *
 *
 */
public interface Sequence<T> extends Iterable<T>{
   
    /**
     * Gets the specific element at the specified index.
     * @param offset the 0-based offset of the element to get.
     * @return the element at the specified index;
     * will never be null.
     * @throws IndexOutOfBoundsException if the given offset
     * is negative or beyond the last offset in
     * the sequence.
     */
    T get(long offset);
    /**
     * Get the number of elements that are in
     * this sequence.
     * @return the length, will never
     * be less than {@code 0}.
     */
    long getLength();
    @Override
    int hashCode();
    /**
     * Two sequences should be equal
     * if they are both the same
     * length and contain the same 
     * elements in the same order.
     */
    @Override
    boolean equals(Object obj);

    /**
     * Create a new {@link Iterator}
     * which only iterates over the specified
     * Range of elements in this sequence.
     * @param range the range to iterate over.
     * @return a new {@link Iterator}; will never
     * be null.
     * @throws NullPointerException if range is null.
     * @throws IndexOutOfBoundsException if Range contains
     * values outside of the possible sequence offsets.
     */
    Iterator<T> iterator(Range range);
    
    
    /**
     * Create a new SequenceBuilder object that is initialized
     * to the current sequence.  Any changes made to the returned Builder
     * will <strong>NOT</strong> affect this immutable Sequence.
     * @return a new Builder instance, will never be null.
     * @since 5.1
     */
    SequenceBuilder<T, ? extends Sequence<T>> toBuilder();
    
    
    /**
     * Convert this sequence into a String using the user defined function 
     * to write out each element in this sequence..
     * @param toStringFunction  The Function to convert each element
     * into a string.  If the function returns {@code null},
     * then that element is not included in the resulting output String.
     * @return a new String; will never be null, but may be empty
     * if either this sequence is empty or the provided function always returns null.
     * 
     * @throws NullPointerException if toStringFunction is null.
     * 
     * 
     * 
     * @since 5.3
     */
    default String toString(Function<T, String> toStringFunction){
        Objects.requireNonNull(toStringFunction);
        
        StringBuilder builder = new StringBuilder((int) getLength()*3);
        for(T aa : this){
            String r = toStringFunction.apply(aa);
            if(r !=null){
                builder.append(r);
            }
        }
        return builder.toString();
    }
}
