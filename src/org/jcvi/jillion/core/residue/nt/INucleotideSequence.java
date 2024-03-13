package org.jcvi.jillion.core.residue.nt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PrimitiveIterator.OfInt;
import java.util.function.Consumer;

import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.internal.core.residue.nt.DefaultLeftFlankingNoGapIterator;
import org.jcvi.jillion.internal.core.residue.nt.DefaultLeftFlankingNucleotideIterator;
import org.jcvi.jillion.internal.core.residue.nt.DefaultRightFlankingNoGapIterator;
import org.jcvi.jillion.internal.core.residue.nt.DefaultRightFlankingNucleotideIterator;

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
	/**
	 * Create an Iterator that iterates over the reverse complement
	 * of this sequence.
	 * @param range the range to iterate over
	 * @return a new Iterator.
	 * 
	 * @implNote the default implementation uses copies all the base's complements into a List
	 * then reverses it and returns the iterator to that list
	 * @since 6.0
	 */
	default Iterator<Nucleotide> reverseComplementIterator(Range range){
		List<Nucleotide> list = new ArrayList<>((int)range.getLength());
		Iterator<Nucleotide> iter = iterator(range);
		while(iter.hasNext()) {
			list.add(iter.next().complement());
		}
		Collections.reverse(list);
		return list.iterator();
		
	}
	
	default Iterator<List<VariantTriplet>> getTriplets(Range range) {
	
		List<List<VariantTriplet>> list = new ArrayList<>((int)(range.getLength())/3);
		consumeTripletIterator(OffsetKnowingIterator.createFwd(iterator(range), (int) range.getBegin()), list::add);
		return list.iterator();
	}
	
	private void consumeTripletIterator(OffsetKnowingIterator iter, Consumer<List<VariantTriplet>> consumer) {
		while(iter.hasNext()) {
			
			Nucleotide a=null, b=null,c=null;
			int offsetA=0, offsetB=0, offsetC=0;
			while(a==null && iter.hasNext()) {
				Nucleotide n = iter.next();
				if(!n.isGap()) {
					a=n;
					offsetA=iter.getNextOffset()-1;
				}
			}
			while(b==null && iter.hasNext()) {
				Nucleotide n = iter.next();
				if(!n.isGap()) {
					b=n;
					offsetB=iter.getNextOffset()-1;
				}
			}
			while(c==null && iter.hasNext()) {
				Nucleotide n = iter.next();
				if(!n.isGap()) {
					c=n;
					offsetC=iter.getNextOffset()-1;
				}
			}
			if(c !=null) {
				consumer.accept(List.of(new VariantTriplet(Triplet.create(a, b, c), 1D, offsetA, offsetB, offsetC)));
			}
		}
	}
	
	default Iterator<List<VariantTriplet>> getReverseComplementTriplets(Range range) {

		List<List<VariantTriplet>> list = new ArrayList<>((int)(range.getLength())/3);
		consumeTripletIterator(OffsetKnowingIterator.createRev(reverseComplementIterator(range), (int) range.getEnd()), list::add);
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
	
    /**
     * Get the list of contiguous spans of Ns; the returned list
     * will be in sorted order.
     * @return a List which may be empty.
     * 
     * @since 5.3
     */
    List<Range> getRangesOfNs();
    
    /**
     * Get the first non-gap {@link org.jcvi.jillion.core.residue.nt.Nucleotide} from the left side of the given
     * gappedReadIndex on the given {@link NucleotideSequence}.  If the given base is not a gap, 
     * then that is the value returned.
     * @param gappedNucleotides the gapped nucleotides to search 
     * @param gappedReadIndex the gapped offset (0-based) to start the search from.
     * @return the first non-gap position on the sequence that is {@code <= gappedOffset}.
     * 
     * @since 6.0.2
     */
	default int getLeftFlankingNonGapOffsetFor(int gappedOffset) {
		return AssemblyUtil.getLeftFlankingNonGapIndex(toNucleotideSequence(), gappedOffset);
	}
	/**
	 * Create a new primitive int iterator {@link java.util.PrimitiveIterator.OfInt} 
	 * that will iterate offsets of the non-gap positions starting at the given gapped 
	 * start offset and move across the sequence towards the left (to lower numbers).
	 * 
	 * This will produce the same results as consecutive calls to {@link #getLeftFlankingNonGapOffsetFor(int)}
	 * but passing in the computed values each time.  Some sequence implementations
	 * may produce more efficient ways of computing that.
	 * @param startingGapOffset the offset to start iterating from.
	 * @return a new iterator; will never be null.
	 * 
	 * @since 6.0.2
	 */
	default OfInt createLeftFlankingNonGapIterator(int startingGapOffset) {
		if(this.hasGaps()) {
			return new DefaultLeftFlankingNucleotideIterator(this, startingGapOffset);
		}
		return new DefaultLeftFlankingNoGapIterator(startingGapOffset);
	}
	/**
	 * Create a new primitive int iterator {@link java.util.PrimitiveIterator.OfInt} 
	 * that will iterate offsets of the non-gap positions starting at the given gapped 
	 * start offset and move across the sequence towards the right (to higher numbers).
	 * 
	 * This will produce the same results as consecutive calls to {@link #getRightFlankingNonGapOffsetFor(int)}
	 * but passing in the computed values each time.  Some sequence implementations
	 * may produce more efficient ways of computing that.
	 * @param startingGapOffset the offset to start iterating from.
	 * @return a new iterator; will never be null.
	 * 
	 * @since 6.0.2
	 */
	default OfInt createRightFlankingNonGapIterator(int startingGapOffset) {
		if(this.hasGaps()) {
			return new DefaultRightFlankingNucleotideIterator(this, startingGapOffset);
		}
		return new DefaultRightFlankingNoGapIterator(startingGapOffset, (int) getLength()-1);
	}
	 /**
     * Get the first non-gap {@link org.jcvi.jillion.core.residue.nt.Nucleotide} from the right side of the given
     * gappedOffset on the given {@link NucleotideSequence}.  If the given base is not a gap, 
     * then that is the value returned.
     * @param sequence the gapped {@link NucleotideSequence} to search 
     * @param gappedOffset the gapped offset (0-based) to start the search from.
     * @return the first non-gap position on the sequence that is {@code >= gappedOffset}
     * 
     * @since 6.0.2
     */
	default int getRightFlankingNonGapOffsetFor(int gappedOffset) {
		return AssemblyUtil.getRightFlankingNonGapIndex(toNucleotideSequence(), gappedOffset);
	}
	/**
	 * Get the first non-gap {@link Nucleotide} coordinates expanding the given range.
	 * If the input coordinates are already non-gaps, then the coordinate will not change.
	 * @apiNote This should be identical but possibly more efficient than
	 * {@code Range.of(getLeftFlankingNonGapOffsetFor((int) gappedRange.getBegin()),
				getRightFlankingNonGapOffsetFor((int) gappedRange.getEnd()))}.
	 *
	 * @param gappedRange the gapped {@link Range} to expand; can not be null.
	 * @return a Range
	 * @throws NullPointerException if gappedRange is null.
	 * 
	 * @since 6.0.2
	 */
	default Range getExpandingFlankingNonGapRangeFor(Rangeable gappedRange) {
		return getExpandingFlankingNonGapRangeFor((int) gappedRange.getBegin(), (int) gappedRange.getEnd());
	}
	/**
	 * Get the first non-gap {@link Nucleotide} coordinates contracting the given range.
	 * If the input coordinates are already non-gaps, then the coordinate will not change.
	 * @apiNote This should be identical but possibly more efficient than
	 * {@code Range.of(getRightFlankingNonGapOffsetFor((int) gappedRange.getBegin()),
						getLeftFlankingNonGapOffsetFor((int) gappedRange.getEnd())
				)}.
	 *
	 * @param gappedRange the gapped {@link Range} to contract; can not be null.
	 * @return a Range
	 * @throws NullPointerException if gappedRange is null.
	 * 
	 * @since 6.0.2
	 */
	default Range getContractingFlankingNonGapRangeFor(Rangeable gappedRange) {
		return getContractingFlankingNonGapRangeFor((int) gappedRange.getBegin(),(int) gappedRange.getEnd());
	}
	/**
	 * Get the first non-gap {@link Nucleotide} coordinates expanding the given range.
	 * If the input coordinates are already non-gaps, then the coordinate will not change.
	 * 
	 * @param gappedBeginOffset the begin coordinate
	 * @param gappedEndOffset the end coordinate
	 * @return a Range
	 * 
	 * @apiNote This should be identical but more efficient than
	 * {{@link #getExpandingFlankingNonGapRangeFor(Range) getExpandingFlankingNonGapRangeFor(Range.of(gappedBeginOffset, gappedEndOffset))}.
	 * 
	 * 
	 * @since 6.0.2
	 */
	default Range getExpandingFlankingNonGapRangeFor(int gappedBeginOffset, int gappedEndOffset) {
		return Range.of(getLeftFlankingNonGapOffsetFor(gappedBeginOffset),
				getRightFlankingNonGapOffsetFor(gappedEndOffset)
		);
	}
	/**
	 * Get the first non-gap {@link Nucleotide} coordinates contracting the given range.
	 * If the input coordinates are already non-gaps, then the coordinate will not change.
	 * 
	 * @param gappedBeginOffset the begin coordinate
	 * @param gappedEndOffset the end coordinate
	 * @return a Range
	 * 
	 * @apiNote This should be identical but more efficient than
	 * {{@link #getContractingFlankingNonGapRangeFor(Range) getContractingFlankingNonGapRangeFor(Range.of(gappedBeginOffset, gappedEndOffset))}.
	 * 
	 * 
	 * @since 6.0.2
	 */
	default Range getContractingFlankingNonGapRangeFor(int gappedBeginOffset, int gappedEndOffset) {
		return Range.of(getRightFlankingNonGapOffsetFor(gappedBeginOffset),
						getLeftFlankingNonGapOffsetFor(gappedEndOffset)
				);
	}
}
