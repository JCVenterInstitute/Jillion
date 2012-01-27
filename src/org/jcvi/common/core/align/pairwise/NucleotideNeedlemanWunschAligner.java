package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.NucleotideSequenceAlignment;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;

public class NucleotideNeedlemanWunschAligner extends AbstractNeedlemanWunschAligner<Nucleotide, NucleotideSequence, NucleotideSequenceAlignment, NucleotidePairwiseSequenceAlignment>{

	public static NucleotidePairwiseSequenceAlignment align(Sequence<Nucleotide> query,
			Sequence<Nucleotide> subject, ScoringMatrix<Nucleotide> matrix,
			float openGapPenalty, float extendGapPenalty){
		NucleotideNeedlemanWunschAligner aligner = new NucleotideNeedlemanWunschAligner(query, subject, matrix, openGapPenalty, extendGapPenalty);
		return aligner.getPairwiseSequenceAlignment();
				
		}
	
	private NucleotideNeedlemanWunschAligner(Sequence<Nucleotide> query,
			Sequence<Nucleotide> subject, ScoringMatrix<Nucleotide> matrix,
			float openGapPenalty, float extendGapPenalty) {
		super(query, subject, matrix, openGapPenalty, extendGapPenalty,
				ResiduePairwiseStrategy.getNucleotideStrategy());
	}

}
