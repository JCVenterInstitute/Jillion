package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.AminoAcidSequenceAlignment;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

/**
 * {@code AminoAcidSmithWatermanAligner} can perform 
 * a pair-wise alignment of two {@link AminoAcidSequence}s
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
public final class AminoAcidSmithWatermanAligner  extends AbstractSmithWatermanAligner<AminoAcid,AminoAcidSequence, AminoAcidSequenceAlignment, AminoAcidPairwiseSequenceAlignment>{
	/**
	 * Align the given two {@link AminoAcidSequence}s
	 * using the given {@link ScoringMatrix} by the Smith-Waterman
	 * local alignment algorithm.
	 * @param query the query {@link AminoAcidSequence} to align;
	 * can not be null.
	 * @param subject the subject {@link NucleotideSequence} to align;
	 * can not be null.
	 * @param matrix the {@link ScoringMatrix} to use; can not be null.
	 * @param openGapPenalty the penalty value for opening a gap.
	 * @param extendGapPenalty the penalty for extending an already open gap.
	 * @return a new {@link NucleotidePairwiseSequenceAlignment} instance;
	 * representing the global alignment, will never be null.
	 * @throws NullPointerException if query, subject or matrix are null.
	 */
	public static AminoAcidPairwiseSequenceAlignment align(AminoAcidSequence query,
			AminoAcidSequence subject, ScoringMatrix<AminoAcid> matrix,
			float openGapPenalty, float extendGapPenalty){
		AminoAcidSmithWatermanAligner aligner = new AminoAcidSmithWatermanAligner(query, subject, matrix, openGapPenalty, extendGapPenalty);
		return aligner.getPairwiseSequenceAlignment();
	}
	private AminoAcidSmithWatermanAligner(Sequence<AminoAcid> query,
			Sequence<AminoAcid> subject, ScoringMatrix<AminoAcid> matrix,
			float openGapPenalty, float extendGapPenalty) {
		super(query, subject, matrix, openGapPenalty, extendGapPenalty,
				ResiduePairwiseStrategy.getAminoAcidStrategy());
	}

}
