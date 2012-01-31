package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.AminoAcidSequenceAlignment;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;

public final class AminoAcidSmithWatermanAligner  extends AbstractSmithWatermanAligner<AminoAcid,AminoAcidSequence, AminoAcidSequenceAlignment, AminoAcidPairwiseSequenceAlignment>{

	public static AminoAcidPairwiseSequenceAlignment align(Sequence<AminoAcid> query,
			Sequence<AminoAcid> subject, ScoringMatrix<AminoAcid> matrix,
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
