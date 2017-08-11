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
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.ResidueSequence;
/**
 * {@code NucleotideSequence} an interface to abstract
 * how a {@link Sequence} of {@link Nucleotide}s are encoded in memory.  Nucleotide data
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
public interface NucleotideSequence extends ResidueSequence<Nucleotide, NucleotideSequence, NucleotideSequenceBuilder>, Serializable{
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
    default Stream<Range> findMatches(String regex){
        return findMatches(Pattern.compile(regex));
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
    default Stream<Range> findMatches(String regex, Range subSequenceRange){
        return findMatches(Pattern.compile(regex), subSequenceRange);
    }
    /**
     * Find all the Ranges in this sequence that match the given regular expression {@link Pattern}.
     * @param pattern the pattern to look for.  All bases must be in uppercase.
     * @return a {@link Stream} of {@link Range} objects of the matches on this sequence.
     * 
     * @since 5.3
     */
    Stream<Range> findMatches(Pattern pattern);
    /**
     * Find the Ranges in this sequence within the specified sub sequence range
     *  that match the given regular expression {@link Pattern}.  
     *   <strong>NOTE</strong> All the Range
     *  coordinates returned in the Stream will be relative to the entire sequence.
     *  @apiNote This should return the same result as :
     *  <pre>
     *  sut.findMatches(pattern)
     *     .filter(r-> r.isSubRangeOf(subSequenceRange))
                   
     *  </pre>
     *  But will be more efficient.
     *  
     * @param pattern the pattern to look for.  All bases must be in uppercase.
     * @param subSequenceRange the Range in the sequence to look for matches in.
     * @return a {@link Stream} of {@link Range} objects of the matches on this sequence.
     * 
     * @since 5.3
     */
    Stream<Range> findMatches(Pattern pattern, Range subSequenceRange);
   
    /**
     * Get the list of contiguous spans of Ns; the returned list
     * will be in sorted order.
     * @return a List which may be empty.
     * 
     * @since 5.3
     */
    List<Range> getRangesOfNs();
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
    static NucleotideSequence of(String sequence) {
        return new NucleotideSequenceBuilder(sequence)
                .turnOffDataCompression(true)
                .build();
    }

    boolean isDna();

    default boolean isRna(){
        return !isDna();
    }

}
