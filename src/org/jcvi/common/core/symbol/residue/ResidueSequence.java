package org.jcvi.common.core.symbol.residue;

import java.util.List;

import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
/**
 * {@code ResidueSequence} is a {@link Sequence}
 * of {@link Residue} that may contain gaps.  There are extra
 * methods to get the gap locations and convert from gap offsets to 
 * ungapped offsets and vice versa.
 * @author dkatzel
 *
 * @param <R> the Type of {@link Residue} in this {@link Sequence}.
 */
public interface ResidueSequence<R extends Residue> extends Sequence<R> {

	 /**
     * Get a List of all the offsets into this
     * sequence which are gaps.  This list SHOULD be
     * sorted by offset in ascending order.  The size of the returned list should be
     * the same as the value returned by {@link #getNumberOfGaps()}.
     * @return a List of gap offsets as Integers.
     */
    List<Integer> getGapOffsets();    
    /**
     * Get the number of gaps in this sequence.
     * @return the number of gaps; will always be {@code >=0}.
     */
    int getNumberOfGaps();
   
    /**
     * Is the {@link Nucleotide} at the given gapped index a gap?
     * @param gappedOffset the gappedOffset to check.
     * @return {@code true} is it is a gap; {@code false} otherwise.
     */
    boolean isGap(int gappedOffset);
    /**
     * Get the number of {@link Nucleotide}s in this {@link NucleotideSequence} 
     * that are not gaps.
     * @return the number of non gaps as a long.
     */
    long getUngappedLength();
    /**
     * Decode only the ungapped bases and return them as a List of
     * {@link Nucleotide}s.
     * @return a List of {@link Nucleotide}s containing only the 
     * ungapped bases.
     */
    List<R> asUngappedList();
    /**
     * Get the number of gaps in the valid range until AND INCLUDING the given
     * gapped index.
     * @param gappedOffset the index to count the number of gaps until.
     * @return the number of gaps in the valid range until AND INCLUDING the given
     * gapped index.
     */
    int getNumberOfGapsUntil(int gappedOffset);
    /**
     * Get the corresponding ungapped offset into
     * this sequence for the given
     * gapped offset.
     * @param gappedOffset the offset into the gapped coordinate
     * system of the desired nucleotide.
     * @return the corresponding offset for the equivalent
     * location in the ungapped sequence.
     */
    int getUngappedOffsetFor(int gappedOffset);
    /**
     * Get the corresponding gapped offset into
     * this sequence for the given
     * ungapped offset.
     * @param ungappedOffset the offset into the ungapped coordinate
     * system of the desired nucleotide.
     * @return the corresponding offset for the equivalent
     * location in the gapped sequence.
     */
    int getGappedOffsetFor(int ungappedOffset);
    /**
     * Get this sequence as a single long string
     * of characters with no whitespace.
     * @return the full sequence as a long string.
     */
    @Override
    String toString();
    /**
     * Two {@link ResidueSequence}s are equal
     * if they contain the same residues 
     * in the same order. 
     * {@inheritDoc}
     */
    @Override
    boolean equals(Object o);
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    int hashCode();

}
