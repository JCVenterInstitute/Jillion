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
import java.util.function.Function;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.residue.ResidueSequence;

/**
 * {@code ProteinSequence} is a marker interface for
 * {@link Sequence}s that contain {@link AminoAcid}s.
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
public interface ProteinSequence extends ResidueSequence<AminoAcid, ProteinSequence, ProteinSequenceBuilder>, Serializable {

	@Override
	ProteinSequenceBuilder toBuilder();

	@Override
	ProteinSequenceBuilder toBuilder(Range trimRange);
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
     static ProteinSequence of(String seq) {
        return new ProteinSequenceBuilder(seq).turnOffDataCompression(true).build();
    }

	@Override
	default ProteinSequence trim(Range trimRange){
		return toBuilder(trimRange).build();
	}
	
	default double computePercentX() {
		long length = getUngappedLength();
		double count=0D;
		for(AminoAcid aa : this) {
			if(aa == AminoAcid.Unknown_Amino_Acid) {
				count++;
			}
		}
		return count/length;
	}
}
