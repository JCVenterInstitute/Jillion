package org.jcvi.jillion.align.pairwise;

import org.jcvi.jillion.align.AminoAcidSequenceAlignmentBuilder;
import org.jcvi.jillion.align.pairwise.AminoAcidPairwiseSequenceAlignment;
import org.jcvi.jillion.align.pairwise.AminoAcidPairwiseSequenceAlignmentImpl;
import org.jcvi.jillion.align.pairwise.AminoAcidScoringMatrix;
import org.jcvi.jillion.align.pairwise.AminoAcidSmithWatermanAligner;
import org.jcvi.jillion.align.pairwise.PairwiseSequenceAlignmentWrapper;
import org.jcvi.jillion.align.pairwise.blosom.BlosomMatrices;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequenceBuilder;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestAminoAcidSmithWaterman {

	@Test
	public void exampleFromBook(){
		AminoAcidScoringMatrix blosom50 = BlosomMatrices.getMatrix(50);
		AminoAcidSequence subject = new AminoAcidSequenceBuilder("HEAGAWGHEE")
									.build();
		AminoAcidSequence query = new AminoAcidSequenceBuilder("PAWHEAE")
										.build();
		AminoAcidPairwiseSequenceAlignment expected = 
				new AminoAcidPairwiseSequenceAlignmentImpl(PairwiseSequenceAlignmentWrapper.wrap(new AminoAcidSequenceAlignmentBuilder()
												.addMatches("AW")
												.addGap('-', 'G')
												.addMatches("HE")
												.setAlignmentOffsets(1, 4)
												.build(),
												28));
		
		AminoAcidPairwiseSequenceAlignment actual = AminoAcidSmithWatermanAligner.align(query, subject, blosom50, -8, -6);
	
		assertEquals(expected, actual);
	}
}
