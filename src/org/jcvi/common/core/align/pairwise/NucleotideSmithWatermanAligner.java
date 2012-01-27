package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.NucleotideSequenceAlignment;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

public class NucleotideSmithWatermanAligner extends AbstractSmithWatermanAligner<Nucleotide, NucleotideSequence, NucleotideSequenceAlignment, NucleotidePairwiseSequenceAlignment>{

	public static NucleotidePairwiseSequenceAlignment align(Sequence<Nucleotide> query,
			Sequence<Nucleotide> subject, ScoringMatrix<Nucleotide> matrix,
			float openGapPenalty, float extendGapPenalty){
		NucleotideSmithWatermanAligner aligner = new NucleotideSmithWatermanAligner(query, subject, matrix, openGapPenalty, extendGapPenalty);
		return aligner.getPairwiseSequenceAlignment();
				
		}
	private NucleotideSmithWatermanAligner(Sequence<Nucleotide> seq1,
			Sequence<Nucleotide> seq2, ScoringMatrix<Nucleotide> matrix,
			float openGapPenalty, float extendGapPenalty) {
		super(seq1, seq2, matrix, openGapPenalty, extendGapPenalty,
				ResiduePairwiseStrategy.getNucleotideStrategy());
	}
	

}
