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
import java.util.Set;

import org.jcvi.jillion.core.residue.Frame;
import org.jcvi.jillion.core.residue.aa.IupacTranslationTables.DefaultVisitor;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.Triplet;
import org.jcvi.jillion.core.residue.nt.VariantNucleotideSequence;

public interface TranslationTable {

	void translate(NucleotideSequence sequence, Frame frame, TranslationVisitor visitor);
	/**
	 * Convenience method for {@link #translate(NucleotideSequence, Frame)}
	 * using {@link Frame#ONE}.
	 * 
	 * @param sequence the sequence to translate; can not be null and can not contain gaps.
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
	 * @param substituteStart should the start codons be substituted with the translated amino acid.
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
	 * @param substituteStart should the start codons be substituted with the translated amino acid.
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

	
	/**
	 * Translate the given <strong>ungapped</strong> {@link NucleotideSequence} into
	 * an {@link ProteinSequence} using the given {@link Frame}.  If the sequence
	 * in the given frame is not a multiple of 3, then this method will
	 * translate as many bases as possible, any "left over" bases will not be translated.
	 * 
	 * 
	 * @param sequence the sequence to translate; can not be null.
	 * 
	 * 
	 * @param options the {@link TranslationOptions} to use; can not be {@code null}.
	 * 
	 * 
	 * @return a new ProteinSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp after
	 * frame is taken into account.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the sequence contains gaps.
	 * @since 6.0
	 */
	ProteinSequence translate(NucleotideSequence sequence, TranslationOptions options);
	
	/**
	 * Translate the given {@link NucleotideSequence} into
	 * an {@link ProteinSequence} using the given {@link Frame}.  If the sequence
	 * in the given frame is not a multiple of 3, then this method will
	 * translate as many bases as possible, any "left over" bases will not be translated.
	 * 
	 * 
	 * @param sequence the sequence to translate; can not be null.
	 * 
	 * 
	 * @param options the {@link TranslationOptions} to use; can not be {@code null}.
	 * 
	 * @param visitor the TranslationVisitor to use; can not be {@code null}.
	 * 
	 * @return a new ProteinSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp after
	 * frame is taken into account.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the sequence contains gaps.
	 * 
	 * @implNote default implementation is {@code translate(sequence, (int)sequence.getLength(), options, visitor)}.
	 * @since 6.0
	 */
	void translate(NucleotideSequence sequence, TranslationOptions options, TranslationVisitor visitor);
	
	/**
	 * Translate the given <strong>ungapped</strong> {@link VariantNucleotideSequence} 
	 * using the given TranslationOptions and call the appropriate methods
	 * on the given TranslationVisitor.
	 * 
	 * 
	 * @param sequence the sequence to translate; can not be null.
	 * 
	 * @param options the {@link TranslationOptions} to use; can not be {@code null}.
	 * 
	 * @param visitor the TranslationVisitor to use; can not be {@code null}.
	 * 
	 * @return a new ProteinSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp after
	 * frame is taken into account.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the sequence contains gaps.
	 * @implNote default implementation is {@code translate(sequence, (int)sequence.getLength(), options, visitor)}.
	 * 
	 * @since 6.0
	 */
	void translate(VariantNucleotideSequence sequence,TranslationOptions options, TranslationVisitor visitor);
	/**
	 * Translate the given {@link VariantNucleotideSequence} 
	 * using default TranslationOptions.
	 * 
	 * 
	 * @param sequence the sequence to translate; can not be null.
	 * 
	 * 
	 * @return a new ProteinSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp after
	 * frame is taken into account.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the sequence contains gaps.
	 * @implNote default implementation is {@code translate(sequence, (int)sequence.getLength(), options, visitor)}.
	 * 
	 * @since 6.0
	 */
	default VariantProteinSequence translate(VariantNucleotideSequence sequence) {
		return translate(sequence, 
				TranslationOptions.builder().mergeCodons(false).build());
	}
	/**
	 * Translate the given {@link VariantNucleotideSequence} 
	 * using given TranslationOptions.
	 * 
	 * 
	 * @param sequence the sequence to translate; can not be null.
	 * 
	 * @param options the {@link TranslationOptions} to use; can not be {@code null}.
	 * 
	 * 
	 * @return a new VariantProteinSequence, will never be null,
	 * but may be empty if the sequence is empty or less than 3 bp after
	 * frame is taken into account.
	 * @throws NullPointerException if either parameter is null.
	 * @throws IllegalArgumentException if the sequence contains gaps.
	 * 
	 * @since 6.0
	 */
	default VariantProteinSequence translate(VariantNucleotideSequence sequence, TranslationOptions options) {
		if(sequence ==null){
			throw new NullPointerException("sequence can not be null");
		}
		if(options ==null){
			throw new NullPointerException("frame can not be null");
		}
		int length = options.getNumberOfBasesToTranslate() ==null? (int)sequence.getLength(): options.getNumberOfBasesToTranslate();
		
		DefaultVisitor visitor = new DefaultVisitor(options, length/3);
		translate(sequence, options,visitor);
		return visitor.builder.build();
	}
	
	Map<Frame,List<Long>> findStops(NucleotideSequence sequence);
	/**
	 * Get the set of all {@link Triplet}s that translate to the given AminoAcid
	 * for this translation table.
	 * @param aa the AminoAcid to get the triplets for; can not be null.
	 * @return a Set that will never be null.
	 * 
	 * @throws NullPointerException if aa is null.
	 * @since 6.0
	 */
	Set<Triplet> getTripletsFor(AminoAcid aa);
}
