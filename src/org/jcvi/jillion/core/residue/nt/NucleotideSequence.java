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
/*
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.residue.nt;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.ResidueSequence;
/**
 * {@code NucleotideSequence} an interface to abstract
 * how a {@link org.jcvi.jillion.core.Sequence} of {@link Nucleotide}s are encoded in memory.  Nucleotide data
 * can be stored in many different ways depending
 * on the use case and size and composition of the sequence.
 * Different encoding implementations can take up more or less memory or require
 * more computations to decode.  This interface hides implementation details
 * regarding the decoding so users don't have to worry about it.
 * <br>
 * {@link NucleotideSequence} is {@link Serializable} in a (hopefully)
 * forwards compatible way. However, there is no 
 * guarantee that the implementation will be the same
 * or even that the implementation class will be the same;
 * but the deserialized object should always be equal
 * to the sequence that was serialized.
 * @author dkatzel
 */
public interface NucleotideSequence extends INucleotideSequence<NucleotideSequence, NucleotideSequenceBuilder>, Serializable, MatchableSequence {
	/**
     * Two {@link NucleotideSequence}s are equal
     * if they contain the same {@link Nucleotide}s 
     * in the same order.
     * <p>
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
    
    /**
     * Creates a new {@link NucleotideSequenceBuilder}
     * object. 
     * @return a new {@link NucleotideSequenceBuilder}
     * instance initialized to the this Sequence;
     * will never be null but may be empty.
     * @implSpec
     *  This is the same as
     * <pre>
     * return {@code new NucleotideSequenceBuilder(this)}
     * </pre>
     * @implNote
     * Implementations of this method should add
     * any additional settings or flags to optimize
     * the Builder to that
     * the final built Sequence should be the same
     * class with the same optimization characteristics
     * as this Sequence instance. For example,
     * a {@link ReferenceMappedNucleotideSequence}
     * should make a builder that uses the same reference.
     * @since 5.0
     */
    @Override
    NucleotideSequenceBuilder toBuilder();


    
    @Override
    default NucleotideSequenceBuilder newEmptyBuilder(){
        return new NucleotideSequenceBuilder();
    }
    @Override
	default NucleotideSequence toNucleotideSequence() {
		return this;
	}
    @Override
    default NucleotideSequenceBuilder newEmptyBuilder(int initialCapacity){
        return new NucleotideSequenceBuilder(initialCapacity);
    }
    /**
     * Find all the Ranges in this sequence that match the given regular expression {@link Pattern}.
     * @param regex the regular expression pattern to look for.  All bases must be in uppercase.
     * @return a {@link Stream} of {@link Range} objects of the matches on this sequence.
     * 
     * @apiNote this is the same as {@code  findMatches(Pattern.compile(regex)); }
     * 
     * @since 5.3
     * 
     * @see #findMatches(Pattern)
     */
    @Override
    default Stream<Range> findMatches(String regex){
        return findMatches(Pattern.compile(regex));
    }
    @Override
    default Stream<Range> findMatches(String regex, boolean nested){
    	return findMatches(Pattern.compile(regex),nested);
    }
    
    /**
     * Find the Ranges in this sequence within the specified sub sequence range
     *  that match the given regular expression.
     *  
     * @param regex the pattern to look for.  All bases must be in uppercase.
     * @param subSequenceRange the Range in the sequence to look for matches in.
     * @return a {@link Stream} of {@link Range} objects of the matches on this sequence.
     * 
     * @apiNote this is the same as {@code  findMatches(Pattern.compile(regex), subSequenceRange); }
     * 
     * @since 5.3
     * 
     * @see #findMatches(Pattern, Range)
     */
    @Override
    default Stream<Range> findMatches(String regex, Range subSequenceRange){
        return findMatches(Pattern.compile(regex), subSequenceRange);
    }
    @Override
    default Stream<Range> findMatches(String regex, Range subSequenceRange, boolean nested){
    	return findMatches(Pattern.compile(regex),subSequenceRange,nested);
    }

    @Override
    default Stream<Range> findMatches(Pattern pattern, boolean nested) {

        return findMatches(pattern, Range.ofLength(getLength()), nested);
    }

    @Override
    @SuppressWarnings("resource")
	default Stream<Range> findMatches(Pattern pattern, Range subSequenceRange, boolean nested) {

        Stream<Range> matches = findMatches(pattern, subSequenceRange);
        if (! nested) {
            return matches;
        }
        List<Range> matchList = matches.collect(Collectors.toList());

        Stream<Range> nestedOutput = matchList.stream();

        long start;
        long end;
        long matchCount = matchList.size();
        for (int i=0,j=1; i < matchCount; i++,j++) {
            start = matchList.get(i).getBegin();
            end = subSequenceRange.getEnd();
            if (j < matchCount) {
                // skip last to avoid getting next match again
                end = matchList.get(j).getEnd() -1;
            }
            if (end - start > 0) {
                nestedOutput = Stream.concat(nestedOutput,findMatches(pattern,Range.of(start + 1, end) , nested));
                nestedOutput = Stream.concat(nestedOutput,findMatches(pattern,Range.of(start, end -1 ), nested));
            }
            if (end - start  > 1)
            {
                nestedOutput = Stream.concat(nestedOutput,findMatches(pattern,Range.of(start + 1, end -1), nested));
            }
        }
        return nestedOutput;
    }
  
    /**
     * Create a new NucleotideSequence of the given sequence.
     * 
     * @param sequence the Sequence of Nucleotides to turn into a NucleotideSequence object;
     * can not be null and can not contain any null values.
     * @return a new NucleotideSequence object; may be empty.
     * 
     * @throws NullPointerException if sequence is null or any element is null.
     * 
     * @since 5.3
     */
    static NucleotideSequence of(Iterable<Nucleotide> sequence) {
        return new NucleotideSequenceBuilder(sequence)
                .turnOffDataCompression(true)
                .build();
    }
    
    /**
     * Create a new NucleotideSequence of the given sequence.
     * 
     * @param sequence the Sequence of Nucleotides to turn into a NucleotideSequence object;
     * can not be null.
     * @return a new NucleotideSequence object; may be empty.
     * 
     * @throws NullPointerException if sequence is null.
     * 
     * @since 5.3
     */
    @JsonCreator
    static NucleotideSequence of(String sequence) {
        return new NucleotideSequenceBuilder(sequence)
                .turnOffDataCompression(true)
                .build();
    }
    /**
     * Create a new NucleotideSequence of a single nucleotide.
     * 
     * @param n the Nucleotide to turn into a NucleotideSequence object;
     * can not be null.
     * @return a new NucleotideSequence object.
     * 
     * @throws NullPointerException if n is null.
     * 
     * @since 6.0
     */
    static NucleotideSequence of(Nucleotide n) {
    	return new NucleotideSequenceBuilder(n)
                .turnOffDataCompression(true)
                .build();
    }

    static NucleotideSequence wrap(Nucleotide[] array) {
    	return new SimpleNucleotideSequence(array);
    }
    static NucleotideSequence wrapACGTN(Nucleotide[] array) {
    	return new ACGTNOnlySimpleNucleotideSequence(array);
    }
    /**
     * Return a new NucleotideSequence that is the reverseComplement of 
     * this sequence.
     * @return a new NucleotideSequence; will never be null.
     * @since 6.0
     */
    default NucleotideSequence reverseComplement() {
    	return toBuilder().reverseComplement().build();
    }


    @Override
    default NucleotideSequence trim(Range trimRange) {
        return toBuilder(trimRange).build();
    }
    /**
     * Is this sequence only contain Ns and is not empty.
     * 
     * @since 6.0
     * @return {@code true} if this sequence is not empty and only contains Ns;
     * {@code false} otherwise.
     * 
     * @implNote the default implementation will iterate through the non-empty sequence 
     * and return {@code false}, if it encounters a non-N; otherwise returns {@code true}.
     */
	default boolean isAllNs() {
		if(isEmpty()) {
			return false;
		}
		for(Nucleotide n : this) {
			if(n != Nucleotide.Unknown) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Compute the percentage of Ns compared to other non-gapped bases in the sequence.
	 * @return a double in the range of {@code [0 .. 1]} inclusive.  If the sequence is empty it returns 0.
	 * 
	 * @since 6.0
	 * 
	 * @implNote the default implementation sums up the lengths of ranges returned by {@link #getRangesOfNs()}
	 * to compute the percentage so it doesn't need to iterate over the whole sequence counting Ns assuming getRangesOfNs is faster.
	 */
	default double computePercentN() {
		long ungappedLength = getUngappedLength();
		if(ungappedLength ==0L) {
			return 0D;
		}
		long numNs = getRangesOfNs().stream().mapToInt(r-> (int) r.getLength()).sum();
		return numNs/(double) ungappedLength;
	}
	
	/**
	 * Compute the GC content.
	 * @return a double in the range of {@code [0 .. 1]} inclusive.  
	 * If the sequence is empty it returns 0.
	 * 
	 * @since 6.0
	 * 
	 * @implNote the default implementation iterates
	 * over the sequence summing up the count of G, C and S bases. 
	 */
	default double computePercentGC() {
		long ungappedLength = getUngappedLength();
		if(ungappedLength ==0) {
			return 0F;
		}
		Iterator<Nucleotide> iter = iterator();
		long gc=0;
		while(iter.hasNext()) {
			Nucleotide n = iter.next();
			if( n== Nucleotide.Guanine || n==Nucleotide.Cytosine || n == Nucleotide.Strong) {
				gc++;
			}
		}
		return ((double)gc)/ungappedLength;
	}

	
}
