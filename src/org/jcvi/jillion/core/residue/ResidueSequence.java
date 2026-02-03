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
package org.jcvi.jillion.core.residue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.SequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.IntList;
import org.jcvi.jillion.core.util.streams.ThrowingIntIndexedConsumer;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * {@code ResidueSequence} is a {@link Sequence}
 * of {@link Residue} that may contain gaps.  There are extra
 * methods to get the gap locations and convert from gap offsets to 
 * ungapped offsets and vice versa.
 * @author dkatzel
 *
 * @param <R> the Type of {@link Residue} in this {@link Sequence}.
 * @param <T> the ResidueSequence implementation, needed for some of the return types to make sure it returns "this" type.
 */
public interface ResidueSequence<R extends Residue<R>, T extends ResidueSequence<R, T, B>, B extends ResidueSequenceBuilder<R, T,B>> extends Sequence<R>, Comparable<T> {

	 /**
     * Get a List of all the offsets into this
     * sequence which are gaps.  This list SHOULD be
     * sorted by offset in ascending order.  The size of the returned list should be
     * the same as the value returned by {@link #getNumberOfGaps()}.
     * @return a List of gap offsets as Integers.
     */
     @JsonIgnore
    IntList getGapOffsets();
    /**
     * Get a List of all the offsets into this
     * sequence which are gaps.  This list SHOULD be
     * sorted by offset in ascending order.  The size of the returned list should be
     * the same as the value returned by {@link #getNumberOfGaps()}.
     * @return the gap offsets as IntStream.
     * @implSpec this should return the same as {@code getGapOffsets().stream().mapToInt(Integer::intValue)}
     * but implementations may be more efficient.
     * 
     * @since 6.0
     */
    default IntStream gaps() {
    	return getGapOffsets().stream().mapToInt(Integer::intValue);
    }
    /**
     * Get the list of contiguous spans of gapss; the returned list
     * will be in sorted order.
     * @return a List which may be empty.
     * 
     * @since 6.0
     */
    List<Range> getRangesOfGaps();

    /**
     * Get the first non-gap {@link org.jcvi.jillion.core.residue.nt.Nucleotide} from the left side of the given
     * gappedReadIndex on the given {@link NucleotideSequence}.  If the given base is not a gap,
     * then that is the value returned.
     * @param gappedOffset the gapped offset (0-based) to start the search from.
     * @return the first non-gap position on the sequence that is {@code <= gappedOffset}.
     *
     * @since 6.0.2
     */
    default int getLeftFlankingNonGapOffsetFor(int gappedOffset) {
        return AssemblyUtil.getLeftFlankingNonGapIndex(this, gappedOffset);
    }

    /**
     * Get the first non-gap {@link org.jcvi.jillion.core.residue.nt.Nucleotide} from the right side of the given
     * gappedOffset on the given {@link NucleotideSequence}.  If the given base is not a gap,
     * then that is the value returned.
     * @param gappedOffset the gapped offset (0-based) to start the search from.
     * @return the first non-gap position on the sequence that is {@code >= gappedOffset}
     *
     * @since 6.0.2
     */
    default int getRightFlankingNonGapOffsetFor(int gappedOffset) {
        return AssemblyUtil.getRightFlankingNonGapIndex(this, gappedOffset);
    }

    /**
     * Get the number of Gaps that start this sequence.
     * @return the number of gaps; will never be &lt; 0.
     * @since 6.1.1
     *
     * @implNote by default, this is the same as {@code getGappedOffsetFor(0)}.
     */
    default int getNumberOfLeadingGaps(){
        return getGappedOffsetFor(0);
    }

    /**
     * Get the number of Gaps that start this sequence.
     * @return the number of gaps; will never be &lt; 0.
     * @since 6.1.1
     *
     * @implNote by default, this is the same as {@code getGappedOffsetFor(0)}.
     */
    default int getNumberOfTrailingGaps(){
        PrimitiveIterator.OfInt iter = getGapOffsets().reverseIntIterator();
        int count=0;
        int currentOffset = (int)( getLength()-1);
        while(iter.hasNext()){
            int i = iter.nextInt();
            if(i==currentOffset){
                currentOffset--;
                count++;
            }else{
                break;
            }
        }
        return count;
    }
    /**
     * Get the list of contiguous spans of the Unknown Residue (i.e. 'N' or 'X' etc); the returned list
     * will be in sorted order.
     * @return a List which may be empty.
     *
     * @since 6.1
     */
    List<Range> getRangesOfUnknowns();
    /**
     * Get the number of gaps in this sequence.
     * @return the number of gaps; will always be {@code >=0}.
     */
    int getNumberOfGaps();
   
    /**
     * Is the {@link Residue} at the given gapped index a gap?
     * @param gappedOffset the gappedOffset to check.
     * @return {@code true} is it is a gap; {@code false} otherwise.
     */
    boolean isGap(int gappedOffset);
    /**
     * Get the number of {@link Residue}s in this sequence 
     * that are not gaps.
     * @return the number of non gaps as a long.
     */
    long getUngappedLength();
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
     * system of the desired nucleotide.  This value must be
     * a non-negative value that is less than the sequence length.
     * @return the corresponding offset for the equivalent
     * location in the ungapped sequence.
     * @throws IndexOutOfBoundsException if the gappedOffset
     * is negative or beyond the sequence length.
     */
    int getUngappedOffsetFor(int gappedOffset);
    
    /**
     * Get the corresponding ungapped offset into
     * this sequence but if the offset is out of bounds, return the closest valid value 
     * and not throw an exception. For example
     * calling this method passing in a value beyond the length
     * of this sequence will return the last ungapped offset.
     * @param gappedOffset gappedOffset the offset into the gapped coordinate
     * system of the desired nucleotide.
     * 
     * @return the corresponding offset for the equivalent
     * location in the ungapped sequence.
     * 
     * @since 6.0.2
     * 
     * @see #getUngappedOffsetFor(int)
     */
    int getUngappedOffsetForSafe(int gappedOffset);
    /**
     * Get the corresponding gapped offset into
     * this sequence for the given
     * ungapped offset. For example
     * calling this method passing in a value of {@code 0}
     * will return the number of leading gaps in this sequence.
     * @param ungappedOffset the offset into the ungapped coordinate
     * system of the desired nucleotide.  This value must be
     * a non-negative value that is less than the sequence ungapped length.
     * @return the corresponding offset for the equivalent
     * location in the gapped sequence.
     * @throws IndexOutOfBoundsException if the ungappedOffset
     * is negative or if the computed
     * gapped offset would extend beyond the sequence length.
     */
    int getGappedOffsetFor(int ungappedOffset);
    
   
    /**
     * Get the corresponding ungapped Range (where the start and end values
     * of the range are in ungapped coordinate space) for the given
     * gapped {@link Range}.
     * @param gappedRange the Range of gapped coordinates; can not be null.
     * @return a new Range never null.
     * @throws NullPointerException if the gappedRange is null.
     * @throws IndexOutOfBoundsException if the given Range goes beyond
     * the gapped sequence.
     * 
     * @since 5.2
     */
    default Range toUngappedRange(Range gappedRange){
       
        if(gappedRange ==null){
            throw new NullPointerException("gappedRange can not be null");
        }
        return Range.of(
                getUngappedOffsetFor((int)gappedRange.getBegin()),
                getUngappedOffsetFor((int)gappedRange.getEnd())
                );
    }
    /**
     * Get the corresponding ungapped Range (where the start and end values
     * of the range are in ungapped coordinate space) for the given
     * gapped {@link Range}.
     * @param gappedRange the Range of gapped coordinates; can not be null.
     * @return a new Range never null.
     * @throws NullPointerException if the gappedRange is null.
     * 
     * @since 6.0.2
     */
    default Range toUngappedRangeSafe(Range gappedRange){
       
        if(gappedRange ==null){
            throw new NullPointerException("gappedRange can not be null");
        }
        return Range.of(
                getUngappedOffsetForSafe((int)gappedRange.getBegin()),
                getUngappedOffsetForSafe((int)gappedRange.getEnd())
                );
    }
    
    /**
     * Get the corresponding gapped Range (where the start and end values
     * of the range are in gapped coordinate space) for the given
     * ungapped {@link Range}.
     * @param ungappedRange the Range of ungapped coordinates; can not be null.
     * @return a new Range never null.
     * @throws NullPointerException if the gappedRange is null.
     * 
     * @since 5.2
     */
    default Range toGappedRange(Range ungappedRange){
       
        if(ungappedRange ==null){
            throw new NullPointerException("ungappedRange can not be null");
        }
        return Range.of(
                getGappedOffsetFor((int)ungappedRange.getBegin()),
                getGappedOffsetFor((int)ungappedRange.getEnd())
                );
    }
    /**
     * Is this Sequence only gaps or blank.
     * @return {@code true} if the sequence is only gaps (or blank);
     * {@code false} otherwise.
     * 
     * @since 6.0
     */
    default boolean isAllGapsOrBlank() {
    	return getNumberOfGaps()== getLength();
    }
    /**
     * Get this sequence as a single long string
     * of characters with no whitespace.
     * @return the full sequence as a long string.
     */
    @Override
    @JsonValue
    String toString();
    /**
     * Two {@link ResidueSequence}s are equal
     * if they contain the same residues 
     * in the same order. 
     * {@inheritDoc}.
     * 
     * @see #isEqualToIgnoringGaps(ResidueSequence)
     */
    @Override
    boolean equals(Object o);
    
    /**
     * Two {@link ResidueSequence}s are considered
     * equal to ignoring gaps
     * if their ungapped versions contain the same residues 
     * in the same order. 
     * 
     */
    default boolean isEqualToIgnoringGaps(ResidueSequence<? extends R, T, B> other){
    	if(other ==null){
    		return false;
    	}
    	if(getUngappedLength() != other.getUngappedLength()){
    		return false;
    	}
    	Iterator<R> iter = iterator();
    	Iterator<? extends R> otherIter = other.iterator(); 
    	while(iter.hasNext()){
    		//have to duplicate get non-gap
    		//code because can't use private helper method
    		//inside a default method.
    		R nextNonGap;
    		do{
    			nextNonGap =iter.next();
    		}while(nextNonGap.isGap() && iter.hasNext());
    		
    		R nextOtherNonGap=null;
    		
    		if(!nextNonGap.isGap()){    			
    			//haven't reached the end of our sequence
    			//yet so check the other sequence for equality
	    		do{
	    			nextOtherNonGap =otherIter.next();
	    		}while(nextOtherNonGap.isGap() && otherIter.hasNext());
	    		
	    		//if we get this far,
	    		//then the our next base is NOT a gap
	    		//so the other seq better equal
	    		if(!nextNonGap.equals(nextOtherNonGap)){
	    			return false;
	    		}
    		}
    		
    	}
    	//if we get this far then our entire sequences
    	//matched. because we previously
    	//checked that the ungapped lengths matched
    	//so if either iterator still has elements
    	//they must all be gaps.
    	return true;
    }
    /**
     * The HashCode of a {@link ResidueSequence}
     * is computed by summing the hashcodes
     * of the residues of this sequence
     * in sequential order. 
     */
    @Override
    int hashCode();
    
    /**
     * Create a new Builder object that is initialized
     * to the current sequence.  Any changes made to the returned Builder
     * will <strong>NOT</strong> affect this immutable Sequence.
     * @return a new Builder instance, will never be null.
     * @since 5.0
     */
    B toBuilder();
    /**
     * Create a new Builder object that is initialized
     * to the just the given Range of the current sequence.  Any changes made to the returned Builder
     * will <strong>NOT</strong> affect this immutable Sequence.
     * @return a new Builder instance, will never be null.
     * @since 5.3
     */
    B toBuilder(Range range);

    /**
     * Create a new Builder object that is initialized
     * to the just the given Ranges of the current sequence.  Any changes made to the returned Builder
     * will <strong>NOT</strong> affect this immutable Sequence.
     *
     * @param ranges the list of Ranges to use, if the Ranges overlap, then that part of the
     *               sequence will be repeated; can not be null or contain null values.
     * @return a new Builder instance, will never be null.
     * @since 6.1
     */
    B toBuilder(List<Range> ranges);
    /**
     * Create a new EMPTY Builder object with the default capacity.
     * 
     * @return a new Builder instance, will never be null.
     * @since 5.3
     * 
     * @see #newEmptyBuilder(int)
     */
    B newEmptyBuilder();
    /**
     * Create a new EMPTY Builder object with the given capacity.
     * 
     * @param initialCapacity the initial capacity; can not be &le; 0.
     * 
     * @return a new Builder instance, will never be null.
     * @since 5.3
     * 
     * @throws IllegalArgumentException if initialCapacity is less than 1.
     * 
     */
    B newEmptyBuilder(int initialCapacity);
    /**
     * Creates a new Builder
     * object with the capacity set either this sequence's length
     * or the given capacity whichever is larger.
     *
     * @param initialCapacity the initial capacity; can not be &le; 0.
     * @return a new Builder
     * instance initialized to this Sequence which may have
     * additional capacity.;
     * will never be null but may be empty.
     * @implSpec
     *  The default implementation is the same as
     * <pre>
     * return {@code newEmptyBuilder(initialCapacity).append(this)}
     * but classes should implement more memory efficient versions if desired.
     * </pre>
     *
     * @since 6.0.3
     *
     * @throws IllegalArgumentException if initialCapacity is less than 1.
     */
    default B toBuilder(int initialCapacity){
        return newEmptyBuilder(initialCapacity)
                .append(this);
    }
    /**
     * Get the actual subtype of this implementation.
     * Ideally, this method should not have been public
     * but was required for internal methods to function properly
     * and it was deemed better to add this method than use reflection to figure it out.
     * 
     * @return the type of this instance.
     * @since 5.3
     */
    T asSubtype();
    /**
     * Create a new {@link Stream} of {@link Kmer}s
     * for all the k-mers of this entire sequence of the given kmer size.
     * 
     * @param k the size of each kmer.  For example a 3-mer would have k=3.
     * @return a new Stream of Kmers which will never be null but may be empty
     * if the sequence length is less than k.
     * 
     * @throws IllegalArgumentException if k is less than 1.
     * @since 5.3
     * 
     * @see #kmers(int, Range)
     */
    default Stream<Kmer<T>> kmers(int k){
        return kmers(k, Range.ofLength(getLength()));
    }
    /**
     * Create a new {@link Stream} of {@link Kmer}s
     * for all the k-mers in the specified sub range of this sequence of the given kmer size.
     * 
     * @param k the size of each kmer.  For example a 3-mer would have k=3.
     * @param range the sub range to use; can not be null or out of range of the sequence.
     * 
     * @return a new Stream of Kmers which will never be null but may be empty
     * if the subrange sequence length is less than k.
     * 
     * @throws IllegalArgumentException if k is less than 1.
     * 
     * @throws IndexOutOfBoundsException if Range contains
     * values outside of the possible sequence offsets.
     * 
     * 
     * @since 5.3
     * 
     */
    default Stream<Kmer<T>> kmers(int k, Range range){
       return StreamSupport.stream(new KmerSpliterator<>(k, asSubtype(), range), false);
    }

    /**
     * Iterate over all the ungapped residues in this sequence.
     * @return a new Iterator that only returns ungapped {@link Residue}s;
     * will never be {@code null} but could be empty.
     *
     * @implNote By default, this method wraps the normal {@link Iterator}
     * and filters out the gaps.  Implementations should override this method
     * to provide more efficient implementations.
     *
     * @since 5.3.2
     */
    default Iterator<R> ungappedIterator(){
        return new Iterator<R>(){

            private Iterator<R> iter = iterator();
            private R nextUngapped;
            {
                getNext();
            }

            private void getNext(){
                nextUngapped=null;
                while(iter.hasNext()){
                    R n = iter.next();

                    if(!n.isGap()){
                        nextUngapped = n;
                        break;
                    }
                }
            }
            @Override
            public boolean hasNext() {
                return nextUngapped!=null;
            }

            @Override
            public R next() {
                if(!hasNext()){
                    throw new NoSuchElementException();
                }
                R ret = nextUngapped;
                getNext();
                return ret;
            }


        };
    }

    /**
     * Iterate over all the ungapped residues in this sequence.
     * @return a new Iterator that only returns ungapped {@link Residue}s;
     * will never be {@code null} but could be empty.
     *
     * @since 5.3.2
     */
    default Iterable<R> ungappedIterable(){
        return this::ungappedIterator;
    }
    /**
     * Compare another residue sequences to another of the same type.
     * 
     * @apiNote this implementation compares the results of toString().
     * 
     * @implNote This is the same as {@code return this.toString().compareTo(other.toString())}.
     * 
     * @returns the comparison int value to determine sort order.
     * 
     * @since 6.0
     */
    default int compareTo(T other) {
    	return this.toString().compareTo(other.toString());
    }
    /**
     * Does this Sequence contain any ambiguous residues.
     * @return {@code true} if it does; {@code false} otherwise.
     * 
     * @since 6.0
     * 
     * @implNote by default this method iterates over each residue until it finds
     * an ambiguous residue but some implementations may override this for a more efficient method.
     */
    default boolean hasAmbiguities() {
    	for(R r: this) {
    		if(r.isAmbiguity()) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Does this Sequence contain any gaps.
     * @return {@code true} if it does; {@code false} otherwise.
     * 
     * @since 6.0
     * 
     * @implNote by default this returns {@code getNumberOfGaps()>0} but some implementations may override this for a more efficient method.
     */
    default boolean hasGaps() {
    	return getNumberOfGaps()>0;
    }

    /**
     * Does this Sequence contain any gaps within the given range.
     *
     * @param range the Range to check; must be within bounds
     * @return {@code true} if it does; {@code false} otherwise.
     *
     * @since 6.1
     *
     * @implNote by default this returns {@code gaps().anyMatch(range::intersects)} but some implementations may override this for a more efficient method.
     */
    default boolean hasGaps(Range range) {
        return gaps().anyMatch(range::intersects);
    }
    
    @Override
    T trim(Range range);


    /**
     * Create an Iterator that iterates over the reverse
     * of this sequence.
     * @return a new Iterator.
     *
     * @implNote the default implementation uses copies all the residues into a List
     * then reverses it and returns the iterator to that list
     * @since 6.0.3
     */
    default Iterator<R> reverseIterator(){
        List<R> list = new ArrayList<>((int) getLength());
        Iterator<R> iter = iterator();
        while(iter.hasNext()) {
            list.add(iter.next());
        }
        Collections.reverse(list);
        return list.iterator();

    }

    /**
     * Construct a {@link ResidueSequence} with the same sequence as this sequence,
     * but without any gaps.
     *
     * @return a ResidueSequence which may be this or a new ResidueSequence,
     * will never be null but may be empty.
     *
     * @implNote by default this is implemented as:
     *
     * <pre>
     * {@code
     * if(getNumberOfGaps()==0) {
     *   return asSubtype();
     * }
     * return toBuilder().ungap().build();
     * }
     * </pre>
     *
     * @since 6.0.3
     */
    default T computeUngappedSequence() {
        if(getNumberOfGaps()==0) {
            return asSubtype();
        }
        return toBuilder().ungap().build();
    }

    /**
     * Create a new Sequence that is only the part of this
     * sequence within the given trim range.
     *
     * @param trimRange the subRange to use; can not be null.
     * @return a new Builder instance, will never be null.
     *
     * @param consumerOfExcess a Consumer that takes a SequenceBuilder
     *                         and the number of bases extra that were beyond
     *                         the current sequence to trim.  Users can use this information
     *                         to append gaps or Ns or throw an exception to handle
     *                         the usecase of too big an input Range.
     * @since 6.1
     *
     * @throws NullPointerException if trimRange is null.
     * @throws E the exception thrown by consumer (if any)
     */
    default <E extends Throwable > T trim(Range trimRange, ThrowingIntIndexedConsumer<B,E> consumerOfExcess) throws E{
        if(trimRange.getBegin() <0){
            throw new IndexOutOfBoundsException(trimRange +" starts before offset 0");
        }
        Range intersection = Range.ofLength(getLength()).intersection(trimRange);

        B builder = toBuilder(intersection);
        int extraBasesAtEnd = (int)(trimRange.getEnd() - intersection.getEnd());

        if(extraBasesAtEnd > 0){
            if(consumerOfExcess==null){
                throw new IndexOutOfBoundsException(trimRange +" extends beyond sequence length " + getLength());
            }
            consumerOfExcess.accept(extraBasesAtEnd, builder);
        }
        return builder.build();
    }

    /**
     * Trim to the given Range, if the range is larger than the current sequence,
     * append the returned sequence with the appropriate number of gaps.
     *
     * @param trimRange the range to trim; can not be {@code null}.
     * @return a new Sequence
     * @since 6.1
     * @implNote  this is the same as {@code return trim(trimRange, (size, builder) ->{
     *             builder.appendGap(size);
     *         });}.
     *
     */
    default T trimAndPaddWithGaps(Range trimRange){
        return trim(trimRange, (size, builder) ->{
            builder.appendGap(size);
        });
    }

    /**
     * Compute a new Iterator that will
     * iterate over this sequence
     * in a standardized way.
     * For example, for Nucleotide Sequences,
     * this will return an iterator that will iterate
     * through the sequence the same order of residues
     * for both the sequence and its reverse complement.
     * @return a new iterator will never be null but may be empty.
     *
     * @implNote This may be computationally expensive as it may require
     * iterating through the sequence multiple times in multiple directions
     * to find the "standard".
     * @since 6.1.2
     */
    Iterator<R> computeStandardizedIterator();
}
