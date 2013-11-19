/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion_experimental.align.pairwise;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.residue.aa.AminoAcidSequence;
import org.jcvi.jillion.core.residue.aa.AminoAcidSequenceBuilder;
import org.jcvi.jillion_experimental.align.AminoAcidSequenceAlignmentBuilder;
import org.jcvi.jillion_experimental.align.pairwise.blosom.BlosumMatrices;
import org.junit.Test;
public class TestAminoAcidSmithWaterman {

	@Test
	public void exampleFromBook(){
		AminoAcidScoringMatrix blosom50 = BlosumMatrices.blosum50();
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
		
		AminoAcidPairwiseSequenceAlignment actual = PairwiseAlignmentBuilder.createProtienAlignmentBuilder(query, subject, blosom50)
														.gapPenalty(-8, -6)														
														.build();
				
				assertEquals(expected, actual);
	}
}
