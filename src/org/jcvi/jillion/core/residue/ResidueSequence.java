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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Sequence;
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
public interface ResidueSequence<R extends Residue, T extends ResidueSequence<R, T, B>, B extends ResidueSequenceBuilder<R, T,B>> extends Sequence<R>, Comparable<T> {

	 /**
     * Get a List of all the offsets into this
     * sequence which are gaps.  This list SHOULD be
     * sorted by offset in ascending order.  The size of the returned list should be
     * the same as the value returned by {@link #getNumberOfGaps()}.
     * @return a List of gap offsets as Integers.
     */
    List<Integer> getGapOffsets();  
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
       return StreamSupport.stream(new KmerSpliterator<R, T, B>(k, asSubtype(), range), false);
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
    
    @Override
    T trim(Range range);
}
