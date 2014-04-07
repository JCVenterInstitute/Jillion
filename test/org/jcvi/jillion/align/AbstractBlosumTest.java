/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.align;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.align.pairwise.ProteinPairwiseSequenceAlignment;
import org.jcvi.jillion.align.pairwise.PairwiseAlignmentBuilder;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
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
		ProteinSequence seq1 = new ProteinSequenceBuilder("LSGIREE*")
									.build();
		
		
		ProteinPairwiseSequenceAlignment alignment = PairwiseAlignmentBuilder.createProtienAlignmentBuilder(seq1, seq1, matrix)
															.gapPenalty(-1, -2)	
															.useGlobalAlignment()
															.build();
		assertEquals(seq1,alignment.getGappedQueryAlignment());
		assertEquals(seq1,alignment.getGappedSubjectAlignment());
	
	}
	@Test
	public void alignSimilarSequences(){
		ProteinSequence seq1 = new ProteinSequenceBuilder("LSGIREE*")
									.build();
		ProteinSequence seq2 = new ProteinSequenceBuilder("LSGVREE*")
									.build();
		
		ProteinPairwiseSequenceAlignment alignment = PairwiseAlignmentBuilder.createProtienAlignmentBuilder(seq1, seq2, matrix)
																	.gapPenalty(-1, -2)	
																	.useGlobalAlignment()
																	.build();
		
		assertEquals(seq1,alignment.getGappedQueryAlignment());
		assertEquals(seq2,alignment.getGappedSubjectAlignment());
	
	}
}
