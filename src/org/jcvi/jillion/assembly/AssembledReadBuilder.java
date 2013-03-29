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
package org.jcvi.jillion.assembly;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.Builder;

/**
 * {@code AssembledReadBuilder} is a {@link Builder}
 * for {@link AssembledRead}s for a specific contig.
 * Methods in this interface can change the bases
 * of this read or shift where on the reference (or contig consensus)
 * this read lands.
 * @author dkatzel
 *
 *
 */
public interface AssembledReadBuilder<R extends AssembledRead> extends Rangeable, Builder<R>{
	
	
    /**
     * Change the reference that this read aligns to and its new
     * gapped starting offset on this new reference.
     * @param reference the new reference (or consensus) to align this
     * read to.
     * @param newOffset the new gapped start offset of this read
     * against the new reference in reference coordinate space.
     * @return this.
     * @throws NullPointerException if reference is null.
     */
    AssembledReadBuilder<R> reference(NucleotideSequence reference, int newOffset);
    /**
     * 
    * Get the gapped start offset of this read
     * against the new reference in reference coordinate space.
     */
    long getBegin();
    /**
     * Get the read id.
     * @return this
     */
    String getId();
    /**
     * Change the gapped start offset of this read to a new
     * value on the same reference.
     * @param newOffset the new gapped start offset.
     * @return this.
     */
    AssembledReadBuilder<R> setStartOffset(int newOffset);
    /**
     * Change the gapped start offset of this read
     * by shifting it to the given number of gapped
     * bases.
     * @param numberOfBases the number of gapped bases
     * this read should get shifted by. A positive value
     * will increase this read's gapped start offset'
     * a negative value will decrease this read's
     * gapped start offset.  A value of 0 will 
     * cause no change.
     * @return this.
     */
    AssembledReadBuilder<R> shift(int numberOfBases);

    /**
     * @return the clearRange
     */
    Range getClearRange();

    /**
     * Get the {@link Direction} of this read.
     * @return the {@link Direction} will never be null.
     */
    Direction getDirection();

    /**
     * @return the ungappedFullLength
     */
    int getUngappedFullLength();
    /**
     * 
    * {@inheritDoc}
    * <p/>
    * Creates a new {@link AssembledRead} instance using the current
    * values given to this builder.
     */
    @Override
    R build();
    /**
     * Modify the gapped basecall sequence of this read
     * to change <strong>only the gaps</strong> of the given subsequence.
     * Sometimes, assembly errors or new alignments mean that parts of underlying 
     * reads may have to get re-gapped to make better alignments.  This method
     * allows partial sequences to get modified so that their gapped sequence
     * can be modified without changing the non-gap bases.
     * @param gappedValidRangeToChange the subsequence of the read to change
     * in gapped <strong>valid range</strong> coordinate system 
     * (only has long as the length of the read).
     * @param newBasecalls the new gapped {@link NucleotideSequence} to replace the bases previously 
     * existing in the {@code gappedValidRangeToChange} range.  The new basecalls
     * do not have to have the same length as the old values but must have the same 
     * ungapped sequence.
     * @return this.
     * @throws IllegalArgumentException if the ungapped version of the newBasecalls 
     * does not match the ungapped version of the bases to be replaced.
     */
    AssembledReadBuilder<R> reAbacus(Range gappedValidRangeToChange,
            NucleotideSequence newBasecalls);
    /**
    * Get the gapped length of this read that
    * aligns to the reference.
     */
    long getLength();
    /**
    * Get the gapped end coordinate of this read that
    * aligns to the reference.
     */
    long getEnd();
    /**
     * 
    * {@inheritDoc}
    * <p/>
    * Get the gapped start and end offsets of this
    * read against this reference as a Range.
     */
    Range asRange();

    /**
     * @return the basesBuilder
     */
    NucleotideSequenceBuilder getNucleotideSequenceBuilder();
    /**
     * Get the current gapped bases of this read 
     * as a NucleotideSequence.  This sequence is immutable
     * and not backed by this builder so any if future calls
     * to this class modify the basecalls of this
     * read, then the NucleotideSequence that was
     * previously returned by this method will be out
     * of sync.
     * @return a NucleotideSequence of the current (possibly modified)
     * basecalls of this read; never null.
     */
    NucleotideSequence getCurrentNucleotideSequence();
    
    
    
    /**
     * Appends the given base to the end
     * of the builder's mutable sequence.
     * @param base a single nucleotide sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if base is null.
     * @return this
     */
    AssembledReadBuilder<R> append(Nucleotide base);
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * @param sequence the nucleotide sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if sequence is null.
     * @return this.
     */
    AssembledReadBuilder<R> append(Iterable<Nucleotide> sequence);
    
	
   
    
    /**
     * Appends the given sequence to the end
     * of the builder's mutable sequence.
     * Any whitespace in the input string will be ignored.
     *  This method is able to parse both
     * '*' (consed) and '-' (TIGR) as gap characters. 
     * @param sequence the nucleotide sequence to be appended
     * to the end our builder.
     * @throws NullPointerException if sequence is null.
     * @return this.
     */
    AssembledReadBuilder<R> append(String sequence);
    /**
     * Inserts the given sequence to the builder's mutable sequence
     * starting at the given offset.  If any nucleotides existed
     * downstream of this offset before this insert method
     * was executed, then those nucleotides will be shifted by n
     * bases where n is the length of the given sequence to insert.
     * Any whitespace in the input string will be ignored.
     *  This method is able to parse both
     * '*' (consed) and '-' (TIGR) as gap characters. 
     * @param offset the GAPPED offset into this mutable sequence
     * to begin insertion.
     * @param sequence the nucleotide sequence to be 
     * inserted at the given offset.
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if offset is invalid.
     * return this.
     */
    AssembledReadBuilder<R> insert(int offset, String sequence);
    
    
    /**
     * Replace the Nucleotide at the given offset with a different nucleotide.
     * @param offset the gapped offset to modify.
     * @param replacement the new {@link Nucleotide} to replace the old
     * {@link Nucleotide} at that location.
     * @return this
     * @throws NullPointerException if replacement is null.
     * @throws IllegalArgumentException if offset is invalid.
     */
    AssembledReadBuilder<R> replace(int offset, Nucleotide replacement);

    /**
     * Deletes the nucleotides from the given range of this 
     * partially constructed NucleotideSequence.  If the given
     * range is empty, then the nucleotideSequence will not
     * be modified. If the range extends beyond the currently
     * built sequence, then this will delete until the end of
     * the sequence.
     * @param range the range to delete can not be null.
     * @return this.
     * @throws NullPointerException if range is null.
     * @throws IllegalArgumentException if range's start is negative
     * or greater than this nucleotide sequence's current length.
     */
    AssembledReadBuilder<R> delete(Range range);
    
    
    int getNumGaps();
    
    int getNumNs();
    int getNumAmbiguities();
    
    /**
     * Inserts the given sequence the beginning
     * of the builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, String) insert(0,sequence)}
     * @param sequence the nucleotide sequence to be 
     * inserted at the beginning.
     * @return this.
     * @throws NullPointerException if sequence is null.
     * @see #insert(int, String)
     */
    AssembledReadBuilder<R> prepend(String sequence);
    /**
     * Inserts the given sequence to the builder's mutable sequence
     * starting at the given offset.  If any nucleotides existed
     * downstream of this offset before this insert method
     * was executed, then those nucleotides will be shifted by n
     * bases where n is the length of the given sequence to insert.
     * @param offset the <strong>gapped</strong> offset into this mutable sequence
     * to begin insertion.  If the offset = the current length then this insertion
     * is treated as an append.
     * @param sequence the nucleotide sequence to be 
     * inserted at the given offset.
     * @return this
     * @throws NullPointerException if sequence is null.
     * @throws IllegalArgumentException if offset <0 or > current sequence length.
     */
    AssembledReadBuilder<R> insert(int offset, Iterable<Nucleotide> sequence);
    
    /**
     * Inserts the given {@link Nucleotide} to the builder's mutable sequence
     * at the given offset.  If any nucleotides existed
     * downstream of this offset before this insert method
     * was executed, then those nucleotides will be shifted by 1
     * base.
     * @param offset the GAPPED offset into this mutable sequence
     * to begin insertion.
     * @param base the {@link Nucleotide} to be 
     * inserted at the given offset.
     * @return this
     * @throws NullPointerException if base is null.
     * @throws IllegalArgumentException if offset <0 or > current sequence length.
     */
    AssembledReadBuilder<R> insert(int offset, Nucleotide base);
    /**
     * Inserts the given sequence the beginning
     * of the builder's mutable sequence.
     * This is the same as calling 
     * {@link #insert(int, Iterable) insert(0,sequence)}
     * @param sequence the nucleotide sequence to be 
     * inserted at the beginning.
     * @return this.
     * @throws NullPointerException if sequence is null.
     * @see #insert(int, Iterable)
     */
    AssembledReadBuilder<R> prepend(Iterable<Nucleotide> sequence);

    
    /**
     * Trim this read to only 
     * contain the  sequence of the given gapped
     * range.  This read's valid range will now be modified
     * to contain only the bases in the trim range.
     * @param trimRange the <strong>gapped</strong> range to modify
     * the valid bases.
     * @return this
     * @throws NullPointerException if range is null.
     * @throws IllegalArgumentException if range is beyond the
     * current sequence valid range.
     */
    AssembledReadBuilder<R> trim(Range trimRange);
    
    /**
     * Create a new {@link AssembledReadBuilder}
     * instance whose data is a copy of this 
     * object.  Any modifications to this object
     * or the returned copy will not
     * be reflected in the other.
     * @return a new {@link AssembledReadBuilder}
     * instance; never null.
     */
    AssembledReadBuilder<R> copy();
}
