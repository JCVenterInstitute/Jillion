package org.jcvi.jillion.core.residue.nt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.ResidueSequence;

public interface INucleotideSequence<S extends INucleotideSequence<S,B>, B extends INucleotideSequenceBuilder<S, B>> extends ResidueSequence<Nucleotide, S, B>{

	/**
	 * Create an Iterator that iterates over the reverse complement
	 * of this sequence.
	 * @return a new Iterator.
	 * 
	 * @implNote the default implementation uses copies all the base's complements into a List
	 * then reverses it and returns the iterator to that list
	 * @since 6.0
	 */
	default Iterator<Nucleotide> reverseComplementIterator(){
		List<Nucleotide> list = new ArrayList<>((int) getLength());
		Iterator<Nucleotide> iter = iterator();
		while(iter.hasNext()) {
			list.add(iter.next().complement());
		}
		Collections.reverse(list);
		return list.iterator();
		
	}
	
	NucleotideSequence toNucleotideSequence();
	@Override
	S trim(Range trimRange);
	
	/**
	 * Construct a {@link NucleotideSequence} with the same sequence as this sequence,
	 * but without any gaps.
	 * 
	 * @return a NucleotideSequence which may be this or a new NucleotideSequence,
	 * will never be null but may be empty.
	 * 
	 * @implNote by default this is implemented as:
	 * 
	 * <pre>
	 * {@code 
	 * if(getNumberOfGaps()==0) {
	 *   return this;
	 * }
	 * return toBuilder().ungap().build();
	 * }
	 * </pre>
	 * 
	 * @since 6.0
	 */
	default S computeUngappedSequence() {
		if(getNumberOfGaps()==0) {
			return asSubtype();
		}
		return toBuilder().ungap().build();
	}
	
	/**
     * Create a new Builder object that is initialized
     * to the just the given Ranges of the current sequence.  Any changes made to the returned Builder
     * will <strong>NOT</strong> affect this immutable Sequence.
     * @param ranges list of Ranges to include.  If ranges overlap, then those bases will be included multiple times.
     * @return a new Builder instance, will never be null.
     * @since 6.0
     */
    B toBuilder(List<Range> ranges);
    
    /**
     * Is this sequence RNA.
     * @return {@code true} if DNA, otherwise false.
     *
     * @since 5.3
     * @see #isRna()
     */
    boolean isDna();

    /**
     * Is this sequence RNA.
     * @return {@code true} if RNA, otherwise false.
     *
     * @since 5.3
     * @see #isDna()
     */
    default boolean isRna(){
        return !isDna();
    }
	
}
