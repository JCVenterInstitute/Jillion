package org.jcvi.jillion.align;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.align.pairwise.AminoAcidPairwiseSequenceAlignment;
import org.jcvi.jillion.align.pairwise.PairwiseAlignmentBuilder;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequenceBuilder;
import org.junit.Test;

public abstract class AbstractBlosumTest {

	private final AminoAcidSubstitutionMatrix matrix;

	public AbstractBlosumTest(AminoAcidSubstitutionMatrix matrix) {
		this.matrix = matrix;
	}
	
	protected AminoAcidSubstitutionMatrix getMatrix() {
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
