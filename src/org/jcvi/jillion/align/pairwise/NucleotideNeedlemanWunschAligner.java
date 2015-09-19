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
package org.jcvi.jillion.align.pairwise;

import org.jcvi.jillion.align.NucleotideSequenceAlignment;
import org.jcvi.jillion.align.SubstitutionMatrix;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code NucleotideNeedlemanWunschAligner} can perform 
 * a pair-wise alignment of two {@link NucleotideSequence}s
 * using the global alignment algorithm developed
 * by Needleman and Wunsch using improvements developed
 * by Gotoh.
 * @author dkatzel
 * 
 * @see <a href="http://dx.doi.org/10.1016/0022-2836(70)90057-4">
 Needleman, Saul B.; and Wunsch, Christian D.
 "A general method applicable to the search for similarities in the amino acid sequence of two proteins".
 Journal of Molecular Biology 48:443-53.</a>
 *   
 * @see <a href="http://dx.doi.org/10.1016/0022-2836(82)90398-9">
 Gotoh Osamu. An improved algorithm for matching biological sequences. 
 Journal of Molecular Biology 162:705-708</a>
 */
final class NucleotideNeedlemanWunschAligner extends AbstractNeedlemanWunschAligner<Nucleotide, NucleotideSequence, NucleotideSequenceAlignment, NucleotidePairwiseSequenceAlignment>{
	/**
	 * Align the given two {@link NucleotideSequence}s
	 * using the given {@link SubstitutionMatrix} by the Needleman-Wunsch
	 * global alignment algorithm.
	 * @param query the query {@link NucleotideSequence} to align;
	 * can not be null.
	 * @param subject the subject {@link NucleotideSequence} to align;
	 * can not be null.
	 * @param matrix the {@link SubstitutionMatrix} to use; can not be null.
	 * @param openGapPenalty the penalty value for opening a gap.
	 * @param extendGapPenalty the penalty for extending an already open gap.
	 * @return a new {@link NucleotidePairwiseSequenceAlignment} instance;
	 * representing the global alignment, will never be null.
	 * @throws NullPointerException if query, subject or matrix are null.
	 */
	public static NucleotidePairwiseSequenceAlignment align(NucleotideSequence query,
			NucleotideSequence subject, SubstitutionMatrix<Nucleotide> matrix,
			float openGapPenalty, float extendGapPenalty){
		NucleotideNeedlemanWunschAligner aligner = new NucleotideNeedlemanWunschAligner(query, subject, matrix, openGapPenalty, extendGapPenalty);
		return aligner.getPairwiseSequenceAlignment();
				
	}
	
	private NucleotideNeedlemanWunschAligner(NucleotideSequence query,
			NucleotideSequence subject, SubstitutionMatrix<Nucleotide> matrix,
			float openGapPenalty, float extendGapPenalty) {
		super(query, subject, matrix, openGapPenalty, extendGapPenalty,
				ResiduePairwiseStrategy.getNucleotideStrategy());
	}

}
