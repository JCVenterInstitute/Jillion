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
import org.jcvi.jillion.internal.align.ProteinSequenceAlignmentBuilder;

/**
 * {@code ProteinSmithWatermanAligner} can perform 
 * a pair-wise alignment of two {@link ProteinSequence}s
 * using the local alignment algorithm developed
 * by Smith and Waterman using improvements developed
 * by Gotoh.
 * @author dkatzel
 * 
 * @see <a href="http://dx.doi.org/10.1016/0022-2836(81)90087-5">
 Smith, T.F. and Waterman, M.S. 1981. 
 Identification of common molecular subsequences.
 Journal of Molecular Biology 147:195-197.</a>
 *   
 * @see <a href="http://dx.doi.org/10.1016/0022-2836(82)90398-9">
 Gotoh Osamu. An improved algorithm for matching biological sequences. 
 Journal of Molecular Biology 162:705-708</a>
 */
final class ProteinSmithWatermanAligner  extends AbstractSmithWatermanAligner<AminoAcid,ProteinSequence, ProteinSequenceBuilder, ProteinSequenceAlignment, ProteinPairwiseSequenceAlignment>{
	/**
	 * Align the given two {@link ProteinSequence}s
	 * using the given {@link SubstitutionMatrix} by the Smith-Waterman
	 * local alignment algorithm.
	 * @param query the query {@link ProteinSequence} to align;
	 * can not be null.
	 * @param subject the subject {@link ProteinSequence} to align;
	 * can not be null.
	 * @param matrix the {@link SubstitutionMatrix} to use; can not be null.
	 * @param openGapPenalty the penalty value for opening a gap.
	 * @param extendGapPenalty the penalty for extending an already open gap.
	 * @return a new {@link NucleotidePairwiseSequenceAlignment} instance;
	 * representing the global alignment, will never be null.
	 * @throws NullPointerException if query, subject or matrix are null.
	 */
	public static ProteinPairwiseSequenceAlignment align(ProteinSequence query,
			ProteinSequence subject, SubstitutionMatrix<AminoAcid> matrix,
			float openGapPenalty, float extendGapPenalty){
		ProteinSmithWatermanAligner aligner = new ProteinSmithWatermanAligner(query, subject, matrix, openGapPenalty, extendGapPenalty);
		return aligner.getPairwiseSequenceAlignment();
	}
	private ProteinSmithWatermanAligner(ProteinSequence query,
			ProteinSequence subject, SubstitutionMatrix<AminoAcid> matrix,
			float openGapPenalty, float extendGapPenalty) {
		super(query, subject, matrix, openGapPenalty, extendGapPenalty,
				ResiduePairwiseStrategy.getAminoAcidStrategy());
	}

}
