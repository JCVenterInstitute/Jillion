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

import org.jcvi.jillion.align.ProteinSequenceAlignment;
import org.jcvi.jillion.align.SubstitutionMatrix;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;

/**
 * {@code ProteinNeedlemanWunschAligner} can perform 
 * a pair-wise alignment of two {@link ProteinSequence}s
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
final class ProteinNeedlemanWunschAligner extends AbstractNeedlemanWunschAligner<AminoAcid, ProteinSequence, ProteinSequenceBuilder,ProteinSequenceAlignment, ProteinPairwiseSequenceAlignment>{
	/**
	 * Align the given two {@link ProteinSequence}s
	 * using the given {@link SubstitutionMatrix} by the Needleman-Wunsch
	 * global alignment algorithm.
	 * @param query the query {@link ProteinSequence} to align;
	 * can not be null.
	 * @param subject the subject {@link ProteinSequence} to align;
	 * can not be null.
	 * @param matrix the {@link SubstitutionMatrix} to use; can not be null.
	 * @param openGapPenalty the penalty value for opening a gap.
	 * @param extendGapPenalty the penalty for extending an already open gap.
	 * @return a new {@link ProteinPairwiseSequenceAlignment} instance;
	 * representing the global alignment, will never be null.
	 * @throws NullPointerException if query, subject or matrix are null.
	 */
	public static ProteinPairwiseSequenceAlignment align(ProteinSequence query,
			ProteinSequence subject, SubstitutionMatrix<AminoAcid> matrix,
			float openGapPenalty, float extendGapPenalty){
		ProteinNeedlemanWunschAligner aligner = new ProteinNeedlemanWunschAligner(query, subject, matrix, openGapPenalty, extendGapPenalty, null,null);
		return aligner.getPairwiseSequenceAlignment();
	}
	public static ProteinPairwiseSequenceAlignment align(ProteinSequence query,
			ProteinSequence subject, SubstitutionMatrix<AminoAcid> matrix,
			float openGapPenalty, float extendGapPenalty,
			Integer subjectShift, Integer queryShift){
		ProteinNeedlemanWunschAligner aligner = new ProteinNeedlemanWunschAligner(query, subject, matrix, openGapPenalty, extendGapPenalty, subjectShift, queryShift);
		return aligner.getPairwiseSequenceAlignment();
	}
	
	private ProteinNeedlemanWunschAligner(ProteinSequence query,
	        ProteinSequence subject, SubstitutionMatrix<AminoAcid> matrix,
			float openGapPenalty, float extendGapPenalty,
			Integer subjectShift, Integer queryShift) {
		super(query, subject, matrix, openGapPenalty, extendGapPenalty,
				ResiduePairwiseStrategy.getAminoAcidStrategy(), subjectShift, queryShift);
	}
	

}
