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

import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.residue.Frame;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public interface TranslationTable {

        void translate(NucleotideSequence sequence, Frame frame, TranslationVisitor visitor);
	/**
	 * Convenience method for {@link #translate(NucleotideSequence, Frame)}
	 * using {@link Frame#ONE}.
	 * 
	 * @param sequence the sequence to translate; can not be null and can not contain gaps.
	 * @param substituteStarts should the start codons be substituted with the translated amino acid.
	 * 
	 * @return a new ProteinSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp.
	 */
	ProteinSequence translate(NucleotideSequence sequence);
	
	/**
	 * Convenience method for {@link #translate(NucleotideSequence, Frame)}
	 * using {@link Frame#ONE}.
	 * 
	 * @param sequence the sequence to translate; can not be null and can not contain gaps.
	 * @param substituteStarts should the start codons be substituted with the translated amino acid.
	 * 
	 * @return a new ProteinSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp.
	 */
	ProteinSequence translate(NucleotideSequence sequence, boolean substituteStart);
	/**
	 * Translate the given <strong>ungapped</strong> {@link NucleotideSequence} into
	 * an {@link ProteinSequence} using the given {@link Frame}.  If the sequence
	 * in the given frame is not a multiple of 3, then this method will
	 * translate as many bases as possible, any "left over" bases will not be translated.
	 * 
	 * @param sequence the sequence to translate; can not be null and can not contain gaps.
	 * @param frame the Frame to use; can not be null.
	 * @param substituteStarts should the start codons be substituted with the translated amino acid.
	 * 
	 * @return a new ProteinSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp after
	 * frame is taken into account.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the sequence contains gaps.
	 */
	ProteinSequence translate(NucleotideSequence sequence, Frame frame, boolean substituteStart);
	
	/**
	 * Translate the given <strong>ungapped</strong> {@link NucleotideSequence} into
	 * an {@link ProteinSequence} using the given {@link Frame}.  If the sequence
	 * in the given frame is not a multiple of 3, then this method will
	 * translate as many bases as possible, any "left over" bases will not be translated.
	 * 
	 * @param sequence the sequence to translate; can not be null and can not contain gaps.
	 * @param frame the Frame to use; can not be null.
	 * @param substituteStarts should the start codons be substituted with the translated amino acid.
	 * 
	 * @return a new ProteinSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp after
	 * frame is taken into account.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the sequence contains gaps.
	 */
	ProteinSequence translate(NucleotideSequence sequence, Frame frame);
	
	/**
	 * Translate the given <strong>ungapped</strong> {@link NucleotideSequence} into
	 * an {@link ProteinSequence} using the given {@link Frame}.  If the sequence
	 * in the given frame is not a multiple of 3, then this method will
	 * translate as many bases as possible, any "left over" bases will not be translated.
	 * 
	 * @param sequence the sequence to translate; can not be null and can not contain gaps.
	 * @param frame the Frame to use; can not be null.
	 * @param length the number of elements in the given sequence.
	 * @param substituteStarts should the start codons be substituted with the translated amino acid.
	 * 
	 * @return a new ProteinSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp after
	 * frame is taken into account.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the sequence contains gaps.
	 */
	ProteinSequence translate(NucleotideSequence sequence, Frame frame, int length);
	
	/**
	 * Translate the given <strong>ungapped</strong> {@link NucleotideSequence} into
	 * an {@link ProteinSequence} using the given {@link Frame}.  If the sequence
	 * in the given frame is not a multiple of 3, then this method will
	 * translate as many bases as possible, any "left over" bases will not be translated.
	 * 
	 * 
	 * @param sequence the sequence to translate; can not be null and can not contain gaps.
	 * 
	 * @param frame the Frame to use; can not be null.
	 * 
	 * @param length the number of elements in the given sequence.
	 * 
	 * @param substituteStarts should the start codons be substituted with the translated amino acid.
	 * 
	 * 
	 * @return a new ProteinSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp after
	 * frame is taken into account.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the sequence contains gaps.
	 */
	ProteinSequence translate(NucleotideSequence sequence, Frame frame, int length, boolean substituteStarts);
	
	Map<Frame,List<Long>> findStops(NucleotideSequence sequence);
}
