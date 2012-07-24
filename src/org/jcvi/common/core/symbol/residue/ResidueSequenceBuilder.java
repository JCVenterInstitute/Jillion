package org.jcvi.common.core.symbol.residue;

import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.util.Builder;

public interface ResidueSequenceBuilder<R extends Residue, S extends Sequence<R>> extends Builder<S> {

	 /**
     * Appends the given residue to the end
     * of the builder's mutable sequence.
     * @param residue a single residue to be appended
     * to the end our builder.
     * @throws NullPointerException if base is null.
     */
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
     * Appends the current contents of the given {@link ResidueSequenceBuilder} to the end
     * of the builder's mutable sequence.  Any further modifications to the passed in builder
     * will not be reflected in this builder.  This is an equivalent but more efficient way operation
     * as {@code this.append(otherBuilder.build())}
     * 
     * @param otherBuilder the {@link ResidueSequenceBuilder} whose current
     * nucleotides are to be appended.
     * @throws NullPointerException if otherBuilder is null.
     */
    ResidueSequenceBuilder<R,S>  append(ResidueSequenceBuilder<R,S>  otherBuilder);
    
   
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
     * of the nucleotide sequence.
     */
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
    ResidueSequenceBuilder<R,S> delete(Range range);
    
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
     * @param otherBuilderthe {@link ResidueSequenceBuilder} whose current
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
     * @param otherBuilder{@link ResidueSequenceBuilder} whose current
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
     * Get a sublist of the current residue sequence as a list
     * of Nucleotide objects.
     * @param range the  range of the sublist to generate.
     * @return a new List of Residues.
     * @throws NullPointerException if range is null.
     * @throws IllegalArgumentException if range is not a sublist of the current
     * sequence.
     */
    List<R> asList(Range range);
    /**
     * Create a new Builder instance using sub sequence
     * from this Builder.  The returned instance is a deep
     * copy of the relevant portions of this Builder so any downstream
     * modifications to either this Builder or the returned one
     * are independent of each other.
     */
	ResidueSequenceBuilder<R,S> subSequence(Range range);
	/**
	 * Create a new deep copy instance of the Builder.
	 * Any downstream modifications to either this Builder or the returned one
     * are independent of each other.
	 */
	ResidueSequenceBuilder<R,S> copy();
    /**
     * Get the entire current residue sequence as a list
     * of Nucleotide objects.
     * @return a new List of Residues.
     */
    List<R> asList();
    
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
    ResidueSequenceBuilder<R,S> reverse();
    /**
     * Remove all gaps currently present in this builder.
     * @return this.
     */
    ResidueSequenceBuilder<R,S> ungap();
}
