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


import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.util.Builder;
/**
 * {@code NucleotideSequenceBuilder}  is a way to
 * construct a {@link Sequence}
 * similar to how a {@link StringBuilder} can be used
 * to create a String.  The contents of the Sequence
 * can be changed by method calls. 
 * <p>
 *  {@link SequenceBuilder} uses method chaining to make combining
 *  several operations easier to read.
 *  
 *  The {@link #build()} method will return an instance of the {@link Sequence}.
 *  <p>
 *  Implementations of {@link SequenceBuilder} are probably
 *  not thread safe unless thread safety is explicitly mentioned.
 * 
 * @author dkatzel
 *
 * @param <T> the Type of element in the sequence
 * @param <S> the Type of Sequence to be built.
 * @param <I> the Type to iterate over, usually the same as <T> but not always.
 */
public interface SequenceBuilder <T, S extends Sequence<T>, B extends SequenceBuilder<T, S, B>> extends Builder<S> , Iterable<T> {
	/**
     * Appends the given symbol to the end
     * of the builder's mutable sequence.
     * @param symbol a single residue to be appended
     * to the end our builder.
     * @throws NullPointerException if the symbol is null.
     * 
     * @return this
     */
	B append(T symbol);
	/**
	 * Get the element at the given offset.
	 * @param offset the offset-th element of the sequence to get.
	 * 
	 * @return this
	 * 
	 * @throws IndexOutOfBoundsException if offset
	 * is &lt; 0 or &ge; length.
	 */
	T get(int offset);
   
    /**
     * Get the current length of the mutable
     * sequence. 
     * @return the current length
     * of the nucleotide sequence.
     */
    long getLength();

    /**
     * Replace the element at the given offset with a different nucleotide.
     * @param offset the gapped offset to modify.
     * @param replacement the new {@link org.jcvi.jillion.core.residue.Residue} to replace the old
     * {@link org.jcvi.jillion.core.residue.Residue} at that location.
     * @return this
     * @throws NullPointerException if replacement is null.
     * @throws IllegalArgumentException if offset is invalid.
     */
    B replace(int offset, T replacement);
    /**
     * Deletes the elements from the given range of this 
     * partially constructed residue sequence.  If the given
     * range is empty, then the residue sequence will not
     * be modified. If the range extends beyond the currently
     * built sequence, then this will delete until the end of
     * the sequence.
     * @param range the range to delete can not be null.
     * @return this.
     * @throws NullPointerException if range is null.
     * @throws IllegalArgumentException if range's start is negative
     * or greater than this residue sequence's current length.
     */
    B delete(Range range);
    /**
     * Helper method to return "this" of the correct type,
     * should not be directly called outside the implementation.
     * @return this
     */
    B getSelf();

    /**
     * Deletes the elements specified in the given Ranges.  If multiple Ranges are given,
     * then the Ranges are sorted from end offset and then removed.  This prevents 
     * having to deal with correcting for downstream offsets.  If there are overlapping
     * ranges, then those positions will be deleted multiple times.
     * @param ranges the list of ranges, can not be null or empty.
     * @return this
     * 
     * @throws NullPointerException if ranges are null or any range is null.
     * @throws IllegalArgumentException if no ranges are provided.
     * @since 6.0
     */
    default B delete(Range...ranges){
    	if(ranges.length ==0) {
    		throw new IllegalArgumentException("must have at least one range to delete");
    	}
    	if(ranges.length ==1) {
    		return delete(ranges[0]);
    	}
    	List<Range> rangeList = Arrays.asList(ranges);
    	rangeList.sort(Range.Comparators.DEPARTURE.reversed());
    	Iterator<Range> iter = rangeList.iterator();
    	do {
    		Range r = iter.next();
    		if(iter.hasNext()) {
    			delete(r);
    		}else {
    			return delete(r);
    		}
    	}while(iter.hasNext());
    	//can't happen but makes compiler happy
    	return getSelf();
    }
   
    /**
     * Inserts the given {@link org.jcvi.jillion.core.residue.Residue} to the builder's mutable sequence
     * at the given offset.  If any nucleotides existed
     * downstream of this offset before this insert method
     * was executed, then those nucleotides will be shifted by 1
     * base.
     * @param offset the GAPPED offset into this mutable sequence
     * to begin insertion.
     * @param base the {@link org.jcvi.jillion.core.residue.Residue} to be 
     * inserted at the given offset.
     * @return this
     * @throws NullPointerException if base is null.
     * @throws IllegalArgumentException if offset &lt; 0 or &gt; current sequence length.
     */
    B insert(int offset, T base);

    /**
    * {@inheritDoc}
    * <p>
    * Create a new {@link Sequence} instance
    * from the current mutable elements.  This method
    * does not destroy any temp data so this method
    * could be called multiple times each time 
    * creating a new {@link Sequence}.
    * 
    * @return a new Sequence; will never null
    * but may be empty.
    */
    @Override
    S build();
   
	
	
	/**
     * Modify the current sequence to keep only that the sub sequence
     * within the given Range.  
     * If the range extends beyond the current
     * sequence, then this will keep all the symbols until the end of
     * the sequence. If the trim range is empty, then the entire sequence
     * will be trimmed.
     * 
     * @param range the range of residues to keep (gapped).
     * @return this.
     */
    B trim(Range range);
	/**
	 * Create a new deep copy instance of the Builder.
	 * Any downstream modifications to either this Builder or the returned one
     * are independent of each other.
     * 
     * @return a new {@link SequenceBuilder} that contains the same state
     * as this builder; will never be null.
	 */
    B copy();
    
    /**
	 * Create a new deep copy instance of the Builder.
	 * Any downstream modifications to either this Builder or the returned one
     * are independent of each other.
     * 
     * @return a new {@link SequenceBuilder} that contains the same state
     * as this builder; will never be null.
     * 
     * @since 6.0
	 */
    B copy(Range range);
    
    /**
     * Get the current symbols as a String.
     */
    @Override
    String toString();
    
    
    /**
     * Reverse <strong>but not complement</strong> all the symbols currently in this builder.
     * Calling this method will only reverse symbols that 
     * already exist in this builder; any additional operations
     * to insert symbols will not be affected.
     * @return this.
     */
    B reverse();
    /**
     * Creates an {@link Iterator}
     * which iterates over the current sequence.
     * Any changes to this {@link SequenceBuilder}
     * between creating this iterator and
     * actually iterating over the elements
     * might not be seen by this iterator.
     * {@inheritDoc}
     */
    @Override
    Iterator<T> iterator();
    /**
     * Removes all the current values
     * from the builder and sets the length 
     * to 0.
     * @return this.
     */
    B clear();
}
