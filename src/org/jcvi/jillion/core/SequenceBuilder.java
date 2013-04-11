/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core;


import java.util.Iterator;

import org.jcvi.jillion.core.util.Builder;
/**
 * {@code NucleotideSequenceBuilder}  is a way to
 * construct a {@link Sequence}
 * similar to how a {@link StringBuilder} can be used
 * to create a String.  The contents of the Sequence
 * can be changed by method calls. 
 * <p/>
 *  {@link SequenceBuilder} uses method chaining to make combining
 *  several operations easier to read.
 *  
 *  The {@link #build()} method will return an instance of the {@link Sequence}.
 *  <p/>
 *  Implementations of {@link SequenceBuilder} are probably
 *  not thread safe unless thread safety is explicitly mentioned.
 * 
 * @author dkatzel
 *
 * @param <T> the Type of element in the sequence
 * @param <S> the Type of Sequence to be built.
 */
public interface SequenceBuilder <T, S extends Sequence<T>> extends Builder<S> , Iterable<T> {
	/**
     * Appends the given symbol to the end
     * of the builder's mutable sequence.
     * @param symbol a single residue to be appended
     * to the end our builder.
     * @throws NullPointerException if the symbol is null.
     */
	SequenceBuilder<T,S> append(T symbol);

	T get(int offset);
   
    /**
     * Get the current length of the mutable
     * sequence. 
     * @return the current length
     * of the nucleotide sequence.
     */
    long getLength();

    /**
     * Replace the residue at the given offset with a different nucleotide.
     * @param offset the gapped offset to modify.
     * @param replacement the new {@link Residue} to replace the old
     * {@link Residue} at that location.
     * @return this
     * @throws NullPointerException if replacement is null.
     * @throws IllegalArgumentException if offset is invalid.
     */
    SequenceBuilder<T,S> replace(int offset, T replacement);
    /**
     * Deletes the nucleotides from the given range of this 
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
    SequenceBuilder<T,S> delete(Range range);

    
   
    /**
     * Inserts the given {@link Residue} to the builder's mutable sequence
     * at the given offset.  If any nucleotides existed
     * downstream of this offset before this insert method
     * was executed, then those nucleotides will be shifted by 1
     * base.
     * @param offset the GAPPED offset into this mutable sequence
     * to begin insertion.
     * @param base the {@link Residue} to be 
     * inserted at the given offset.
     * @return this
     * @throws NullPointerException if base is null.
     * @throws IllegalArgumentException if offset <0 or > current sequence length.
     */
    SequenceBuilder<T,S> insert(int offset, T base);

    /**
    * {@inheritDoc}
    * <p>
    * Create a new {@link ResidueSequenceBuilder} instance
    * from the current mutable residues.  This method
    * does not destroy any temp data so this method
    * could be called multiple times each time 
    * creating a new {@link ResidueSequenceBuilder}.
    * @return a new residue Sequence never null
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
    SequenceBuilder<T,S> trim(Range range);
	/**
	 * Create a new deep copy instance of the Builder.
	 * Any downstream modifications to either this Builder or the returned one
     * are independent of each other.
	 */
    SequenceBuilder<T,S> copy();
    
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
    SequenceBuilder<T,S> reverse();
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
}
