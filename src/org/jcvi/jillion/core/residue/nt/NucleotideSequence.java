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
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.MatchableSequence;
import org.jcvi.jillion.internal.core.io.StreamUtil;

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
public interface NucleotideSequence extends INucleotideSequence<NucleotideSequence, NucleotideSequenceBuilder>, Serializable, MatchableSequence<Nucleotide, NucleotideSequence, NucleotideSequenceBuilder> {
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

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    class NucleotideSequenceMatcherParameters extends SequenceMatcherParameters<Nucleotide, NucleotideSequence, NucleotideSequenceBuilder>{

        @Override
        protected BiFunction<String, Boolean, Pattern> getRegexPatternFunction() {
            return (pattern, explode)->{
                if(explode){
                    return NucleotideSequencePattern.compile(pattern);
                }
                return Pattern.compile(pattern);
            };
        }

        @Override
        protected CharSequence toCharSequence(NucleotideSequence sequence) {
            return new NucleotideSeqCharSequence(sequence);
        }

        @Override
        protected NucleotideSequenceBuilder createNewBuilder(int seqLength) {
            return new NucleotideSequenceBuilder(seqLength);
        }


    }





    @Override
    default Stream<Range> findMatches(String regex, Range subSequenceRange, boolean nested) {
        return findMatches(NucleotideSequenceMatcherParameters.builder()
                .stringPattern(regex)
                .subSequenceRange(subSequenceRange)
                .nested(nested)
                .build());
    }

    @Override
    default Stream<Range> findMatches(Pattern pattern, Range subSequenceRange, boolean nested) {
        return findMatches(NucleotideSequenceMatcherParameters.builder()
                .pattern(pattern)
                .subSequenceRange(subSequenceRange)
                .nested(nested)
                .build());
    }

    @Override
    default Stream<Range> findMatches(SequenceMatcherParameters<Nucleotide, NucleotideSequence, NucleotideSequenceBuilder> sequenceMatcherParameters) {
        return sequenceMatcherParameters.findMatches(this);
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
