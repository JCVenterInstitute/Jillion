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
package org.jcvi.jillion.core.residue;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.SequenceBuilder;

public interface ResidueSequenceBuilder<R extends Residue, S extends Sequence<R>> extends SequenceBuilder<R,S> {

	 /**
     * Appends the given residue to the end
     * of the builder's mutable sequence.
     * @param residue a single residue to be appended
     * to the end our builder.
     * @throws NullPointerException if base is null.
     */
	@Override
    ResidueSequenceBuilder<R,S> append(R residue);
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * @param sequence the residue sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if sequence is null.
     */
    ResidueSequenceBuilder<R,S>  append(Iterable<R> sequence);
    
    
    
   
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * @param sequence the residue sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if sequence is null.
     */
    ResidueSequenceBuilder<R,S> append(String sequence);
    /**
     * Inserts the given sequence to the builder's mutable sequence
     * starting at the given offset.  If any residues existed
     * downstream of this offset before this insert method
     * was executed, then those residues will be shifted by n
     * bases where n is the length of the given sequence to insert.
     * @param offset the GAPPED offset into this mutable sequence
     * to begin insertion.
     * @param sequence the nucleotide sequence to be 
     * inserted at the given offset.
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if offset is invalid.
     */
    ResidueSequenceBuilder<R,S> insert(int offset, String sequence);
   
    /**
     * Get the current length of the mutable
     * sequence. 
     * @return the current length
     * of the residue sequence.
     */
    @Override
    long getLength();
    
    /**
     * Get the current length of the mutable
     * sequence not counting gaps in the sequence. 
     * @return the current length
     * of the nucleotide sequence.
     */
    long getUngappedLength();
    /**
     * Replace the residue at the given offset with a different nucleotide.
     * @param offset the gapped offset to modify.
     * @param replacement the new {@link Residue} to replace the old
     * {@link Residue} at that location.
     * @return this
     * @throws NullPointerException if replacement is null.
     * @throws IllegalArgumentException if offset is invalid.
     */
    @Override
    ResidueSequenceBuilder<R,S> replace(int offset, R replacement);
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
    @Override
    ResidueSequenceBuilder<R,S> delete(Range range);
    /**
     * Get the number of gaps currently in this sequence.
     * @return the number of gaps; will always be >= 0.
     */
    int getNumGaps();
    
    
    /**
     * Inserts the given sequence the beginning
     * of the builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, String) insert(0,sequence)}
     * @param sequence the residue sequence to be 
     * inserted at the beginning.
     * @return this.
     * @throws NullPointerException if sequence is null.
     * @see #insert(int, String)
     */
    ResidueSequenceBuilder<R,S> prepend(String sequence);
    /**
     * Inserts the given sequence to the builder's mutable sequence
     * starting at the given offset.  If any residues existed
     * downstream of this offset before this insert method
     * was executed, then those nucleotides will be shifted by n
     * bases where n is the length of the given sequence to insert.
     * @param offset the <strong>gapped</strong> offset into this mutable sequence
     * to begin insertion.  If the offset = the current length then this insertion
     * is treated as an append.
     * @param sequence the residue sequence to be 
     * inserted at the given offset.
     * @return this
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if offset <0 or > current sequence length.
     */
    ResidueSequenceBuilder<R,S> insert(int offset, Iterable<R> sequence);
    /**
     * Inserts the contents of the given other  {@link ResidueSequenceBuilder}
     *  into this builder's mutable sequence
     * starting at the given offset.  If any residues existed
     * downstream of this offset before this insert method
     * was executed, then those residues will be shifted by n
     * bases where n is the length of the given sequence to insert.
     * Any further modifications to the passed in builder
     * will not be reflected in this builder.  This is an equivalent but more efficient operation
     * as {@code this.insert(offset, otherBuilder.build())}
     * 
     * @param offset the <strong>gapped</strong> offset into this mutable sequence
     * to begin insertion.
     * @param otherBuilder the {@link ResidueSequenceBuilder} whose current
     * residues are to be inserted at the given offset.
     * @return this
     * @throws NullPointerException if otherBuilder is null.
     * @throws IllegalArgumentException if offset <0 or > current sequence length.
     */
    ResidueSequenceBuilder<R,S> insert(int offset, ResidueSequenceBuilder<R,S> otherBuilder);
    
   
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
    @Override
    ResidueSequenceBuilder<R,S> insert(int offset, R base);
    /**
     * Inserts the given sequence the beginning
     * of the builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, Iterable) insert(0,sequence)}
     * @param sequence the residue sequence to be 
     * inserted at the beginning.
     * @return this.
     * @throws NullPointerException if sequence is null.
     * @see #insert(int, Iterable)
     */
    ResidueSequenceBuilder<R,S> prepend(Iterable<R> sequence);
    
    /**
     * Inserts the current contents of the given {@link ResidueSequenceBuilder}
     * to the beginning
     * of this builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, ResidueSequenceBuilder) insert(0,otherBuilder)}
     * @param otherBuilder {@link ResidueSequenceBuilder} whose current
     * nucleotides are to be inserted at the beginning.
     * @return this.
     * @throws NullPointerException if otherBuilder is null.
     * @see #insert(int, ResidueSequenceBuilder)
     */
    ResidueSequenceBuilder<R,S> prepend(ResidueSequenceBuilder<R,S> otherBuilder);
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
     * Create a new {@link ResidueSequenceBuilder} instance
     * from containing only current mutable residues
     * in the given range.
     * @param range the range of residues to build (gapped).
     * @return a new {@link ResidueSequenceBuilder} never null
     * but may be empty.
     */
    S build(Range range);
   
	
	
	/**
     * Modify the current sequence to keep only that the sub sequence
     * within the given Range.  
     * If the range extends beyond the current
     * sequence, then this will keep all the residues until the end of
     * the sequence. 
     * 
     * @param range the range of residues to keep (gapped).
     * @return this.
     */
    @Override
	ResidueSequenceBuilder<R,S> trim(Range range);
	/**
	 * Create a new deep copy instance of the Builder.
	 * Any downstream modifications to either this Builder or the returned one
     * are independent of each other.
	 */
    @Override
	ResidueSequenceBuilder<R,S> copy();

    
    /**
     * Get the current Residues as a String.
     */
    @Override
    String toString();
    
    
    /**
     * Reverse <strong>but not complement</strong> all the residues currently in this builder.
     * Calling this method will only reverse residues that 
     * already exist in this builder; any additional operations
     * to insert residues will not be affected.
     * </p>
     * For example if this builder had the sequence
     * "AAGG" then after this method is called the resulting
     * sequence would be "GGAA".
     * @return this.
     */
    @Override
    ResidueSequenceBuilder<R,S> reverse();
    /**
     * Remove all gaps currently present in this builder.
     * @return this.
     */
    ResidueSequenceBuilder<R,S> ungap();
}
