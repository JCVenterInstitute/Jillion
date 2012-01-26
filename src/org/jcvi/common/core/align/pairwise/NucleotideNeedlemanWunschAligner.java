package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.NucleotideSequenceAlignment;
import org.jcvi.common.core.align.NucleotideSequenceAlignmentBuilder;
import org.jcvi.common.core.align.SequenceAlignmentBuilder;
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
		super(query, subject, matrix, openGapPenalty, extendGapPenalty);
	}

	@Override
	protected Nucleotide getGap() {
		return Nucleotide.Gap;
	}

	@Override
	protected Nucleotide getResidueFromOrdinal(int ordinal) {
		return Nucleotide.values()[ordinal];
	}

	@Override
	protected SequenceAlignmentBuilder<Nucleotide, NucleotideSequence, NucleotideSequenceAlignment> createSequenceAlignmentBuilder(boolean builtFromTraceback) {
		return new NucleotideSequenceAlignmentBuilder(builtFromTraceback);
	}

	@Override
	protected NucleotidePairwiseSequenceAlignment wrapPairwiseAlignment(
			PairwiseSequenceAlignment<Nucleotide, NucleotideSequence> alignment) {
		return new NucleotidePairwiseSequenceAlignmentImpl(alignment);
	}
	
	

}
