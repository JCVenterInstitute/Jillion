package org.jcvi.jillion_experimental.align.pairwise.blosom;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequenceBuilder;
import org.jcvi.jillion_experimental.align.pairwise.AminoAcidPairwiseSequenceAlignment;
import org.jcvi.jillion_experimental.align.pairwise.AminoAcidScoringMatrix;
import org.jcvi.jillion_experimental.align.pairwise.PairwiseAlignmentBuilder;
import org.junit.Test;

public abstract class AbstractBlosumTest {

	private final AminoAcidScoringMatrix matrix;

	public AbstractBlosumTest(AminoAcidScoringMatrix matrix) {
		this.matrix = matrix;
	}
	
	protected AminoAcidScoringMatrix getMatrix() {
		return matrix;
	}

	@Test
	public abstract void spotCheck();
	
	@Test
	public void hasSequencesHaveStopCodon(){
		AminoAcidSequence seq1 = new AminoAcidSequenceBuilder("LSGIREE*")
									.build();
		
		
		AminoAcidPairwiseSequenceAlignment alignment = PairwiseAlignmentBuilder.createProtienAlignmentBuilder(seq1, seq1, matrix)
															.gapPenalty(-1, -2)	
															.useGlobalAlignment()
															.build();
		assertEquals(seq1,alignment.getGappedQueryAlignment());
		assertEquals(seq1,alignment.getGappedSubjectAlignment());
	
	}
	@Test
	public void alignSimilarSequences(){
		AminoAcidSequence seq1 = new AminoAcidSequenceBuilder("LSGIREE*")
									.build();
		AminoAcidSequence seq2 = new AminoAcidSequenceBuilder("LSGVREE*")
									.build();
		
		AminoAcidPairwiseSequenceAlignment alignment = PairwiseAlignmentBuilder.createProtienAlignmentBuilder(seq1, seq2, matrix)
																	.gapPenalty(-1, -2)	
																	.useGlobalAlignment()
																	.build();
		
		assertEquals(seq1,alignment.getGappedQueryAlignment());
		assertEquals(seq2,alignment.getGappedSubjectAlignment());
	
	}
}
