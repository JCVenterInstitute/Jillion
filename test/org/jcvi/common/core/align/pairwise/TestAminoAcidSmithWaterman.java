package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.AminoAcidSequenceAlignment;
import org.jcvi.common.core.align.AminoAcidSequenceAlignmentBuilder;
import org.jcvi.common.core.align.pairwise.blosom.BlosomMatrices;
import org.jcvi.common.core.symbol.residue.aa.AminoAcid;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequence;
import org.jcvi.common.core.symbol.residue.aa.AminoAcidSequenceBuilder;
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
		PairwiseSequenceAlignment<AminoAcid, AminoAcidSequence> expected = 
				PairwiseSequenceAlignmentWrapper.wrap(new AminoAcidSequenceAlignmentBuilder()
												.addMatches("AW")
												.addGap('-', 'G')
												.addMatches("HE")
												.setAlignmentOffsets(1, 4)
												.build(),
												28);
		
		PairwiseSequenceAlignment<AminoAcid, AminoAcidSequence> actual = AminoAcidSmithWatermanAligner.align(query, subject, blosom50, -8, -6);
	
		assertEquals(expected, actual);
	}
}
