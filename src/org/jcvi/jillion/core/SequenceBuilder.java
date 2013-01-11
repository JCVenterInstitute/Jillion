package org.jcvi.jillion.core;


import java.util.Iterator;

import org.jcvi.common.core.util.Builder;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;

public interface SequenceBuilder <T extends Symbol, S extends Sequence<T>> extends Builder<S> , Iterable<T> {
	/**
     * Appends the given symbol to the end
     * of the builder's mutable sequence.
     * @param symbol a single residue to be appended
     * to the end our builder.
     * @throws NullPointerException if the symbol is null.
     */
	SequenceBuilder<T,S> append(T symbol);

   
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
     * the sequence. 
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