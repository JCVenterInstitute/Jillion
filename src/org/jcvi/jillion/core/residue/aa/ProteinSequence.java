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
package org.jcvi.jillion.core.residue.aa;

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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.MatchableSequence;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.core.io.StreamUtil;

/**
 * {@code ProteinSequence} is a marker interface for
 * {@link org.jcvi.jillion.core.Sequence}s that contain {@link AminoAcid}s.
 * <br>
 * {@link ProteinSequence} is {@link Serializable} in a (hopefully)
 * forwards compatible way. However, there is no 
 * guarantee that the implementation will be the same
 * or even that the implementation class will be the same;
 * but the deserialized object should always be equal
 * to the sequence that was serialized.
 *
 * @author dkatzel
 */
public interface ProteinSequence extends ResidueSequence<AminoAcid, ProteinSequence, ProteinSequenceBuilder>, Serializable, MatchableSequence<AminoAcid, ProteinSequence, ProteinSequenceBuilder> {

	@Override
	default ProteinSequence getSelf(){
		return this;
	}

	@Override
	ProteinSequenceBuilder toBuilder();

	@Override
	ProteinSequenceBuilder toBuilder(Range trimRange);
	@Override
	ProteinSequenceBuilder toBuilder(List<Range> trimRanges);
	/**
	 * Convert this sequence into a String using the user defined function 
	 * to write out each AminoAcid.
	 * @param toStringFunction  The Function to convert each amino acid
	 * into a string.  If the function returns {@code null},
	 * then that amino acid is not included in the resulting output String.
	 * @return a new String; will never be null, but may be empty
	 * if either this sequence is empty or the provided function always returns null.
	 * 
	 * @throws NullPointerException if toStringFunction is null.
	 * 
	 * @apiNote for example, to print each amino acid by its 3 letter code
	 * instead of the one letter code:
	 * <pre>
	 * {@code sequence.toString(AminoAcid::get3LetterAbbreviation)}
	 * </pre>
	 * 
	 * @since 5.3
	 */
	@Override
	default String toString(Function<AminoAcid, String> toStringFunction){
	    return ResidueSequence.super.toString(toStringFunction);
	}
	@JsonCreator
	static ProteinSequence of(String seq) {
        return new ProteinSequenceBuilder(seq).turnOffDataCompression(true).build();
    }

	@Override
	default ProteinSequence trim(Range trimRange){
		Range currentRange = Range.ofLength(getLength());
		if(currentRange.isSubRangeOf(trimRange)){
			//no trimming needed?
			return this;
		}
		return toBuilder(trimRange).build();
	}
	/**
	 * Compute the percentage of Xs in the sequence.
	 * @apiImpl by default the implementation is {@code ((double)getNumberOfXs())/getUngappedLength()}.
	 * 
	 * @return the percentage as a double [0..1].
	 * @since 6.0
	 */
	default double computePercentX() {
		return ((double)getNumberOfXs())/getUngappedLength();
	}
	/**
	 * Get the number of Xs in the sequence.
	 * @return a number &ge; 0.
	 * 
	 * @since 6.0
	 */
	default long getNumberOfXs() {
		long count=0L;
		for(AminoAcid aa : this) {
			if(aa == AminoAcid.Unknown_Amino_Acid) {
				count++;
			}
		}
		return count;
	}



	@Override
	default Stream<Range> findMatches(String regex, Range subSequenceRange, boolean nested) {


		return findMatches(ProteinSequenceMatcherParameters.builder()
				.stringPattern(regex)
				.subSequenceRange(subSequenceRange)
				.nested(nested)
				.build());
	}


	@Override
	default Stream<Range> findMatches(Pattern pattern, Range subSequenceRange, boolean nested) {
		return findMatches(ProteinSequenceMatcherParameters.builder()
				.pattern(pattern)
				.subSequenceRange(subSequenceRange)
				.nested(nested)
				.build());
	}

	@Override
	default Stream<Range> findMatches(SequenceMatcherParameters<AminoAcid, ProteinSequence, ProteinSequenceBuilder> sequenceMatcherParameters) {
		return sequenceMatcherParameters.findMatches(this);
	}


	@EqualsAndHashCode(callSuper = true)
	@Data
	@SuperBuilder
	class ProteinSequenceMatcherParameters extends SequenceMatcherParameters<AminoAcid, ProteinSequence, ProteinSequenceBuilder>{

		@Override
		protected BiFunction<String, Boolean, Pattern> getRegexPatternFunction() {

			return (pattern, explode)->{
				if(explode){
					return ProteinSequencePattern.compile(pattern);
				}
				return Pattern.compile(pattern);
			};
		}

		@Override
		protected CharSequence toCharSequence(ProteinSequence sequence) {
			return new ProteinSeqCharSequence(sequence);
		}

		@Override
		protected ProteinSequenceBuilder createNewBuilder(int seqLength) {
			return new ProteinSequenceBuilder(seqLength);
		}
	}

	/**
	 * Protein "standardized" is just normal iteration.
	 * @return a new iterator.
	 * @implNote This is the same as {@link #iterator()}
	 *
	 */
	@Override
	default Iterator<AminoAcid> computeStandardizedIterator(){
		return iterator();
	}
}
