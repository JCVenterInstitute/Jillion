package org.jcvi.jillion.core.residue.nt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

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
}
