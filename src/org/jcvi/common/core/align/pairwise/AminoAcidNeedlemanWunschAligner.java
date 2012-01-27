package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.AminoAcidSequenceAlignment;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;


public final class AminoAcidNeedlemanWunschAligner extends AbstractNeedlemanWunschAligner<AminoAcid, AminoAcidSequence, AminoAcidSequenceAlignment, AminoAcidPairwiseSequenceAlignment>{
	/**
	 * Align the given two {@link AminoAcidSequence}s
	 * using the given {@link ScoringMatrix} by the Needleman-Wunsch
	 * global alignment algorithm.
	 * @param query the query {@link AminoAcidSequence} to align;
	 * can not be null.
	 * @param subject the subject {@link AminoAcidSequence} to align;
	 * can not be null.
	 * @param matrix the {@link ScoringMatrix} to use; can not be null.
	 * @param openGapPenalty the penalty value for opening a gap.
	 * @param extendGapPenalty the penalty for extending an already open gap.
	 * @return a new {@link AminoAcidPairwiseSequenceAlignment} instance;
	 * representing the global alignment, will never be null.
	 * @throws NullPointerException if query, subject or matrix are null.
	 */
	public static AminoAcidPairwiseSequenceAlignment align(AminoAcidSequence query,
			AminoAcidSequence subject, ScoringMatrix<AminoAcid> matrix,
			float openGapPenalty, float extendGapPenalty){
		AminoAcidNeedlemanWunschAligner aligner = new AminoAcidNeedlemanWunschAligner(query, subject, matrix, openGapPenalty, extendGapPenalty);
		return aligner.getPairwiseSequenceAlignment();
	}
	
	private AminoAcidNeedlemanWunschAligner(Sequence<AminoAcid> query,
			Sequence<AminoAcid> subject, ScoringMatrix<AminoAcid> matrix,
			float openGapPenalty, float extendGapPenalty) {
		super(query, subject, matrix, openGapPenalty, extendGapPenalty,
				ResiduePairwiseStrategy.getAminoAcidStrategy());
	}
	

}
