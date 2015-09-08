/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.align.pairwise;

import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.align.NucleotideSequenceAlignmentBuilder;
import org.junit.Test;

public class TestNucleotideNeedlemanWunschAligner extends AbstractTestNucleotideAligner{

	
	
	@Test
	public void oneBase(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("A").build();
		
		
		NucleotidePairwiseSequenceAlignment actual = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(seq, seq, matrix)
															.gapPenalty(0, 0)
															.useGlobalAlignment()
															.build();

		
		NucleotidePairwiseSequenceAlignment expected =
				new NucleotidePairwiseSequenceAlignmentImpl(
				PairwiseSequenceAlignmentWrapper.wrap(new NucleotideSequenceAlignmentBuilder()
												.addMatches(seq)
												.build(),
												2));
		
		assertEquals(expected, actual);
	}
	@Test
	public void twoBases(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("AC").build();
		
		NucleotidePairwiseSequenceAlignment actual = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(seq, seq, matrix)
														.gapPenalty(0, 0)
														.useGlobalAlignment()
														.build();

		NucleotidePairwiseSequenceAlignment expected =
				new NucleotidePairwiseSequenceAlignmentImpl(
				PairwiseSequenceAlignmentWrapper.wrap(new NucleotideSequenceAlignmentBuilder()
												.addMatches(seq)
												.build(),
												4));
		
		assertEquals(expected, actual);
	}
	@Test
	public void exactMatch(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("ACGTACGT").build();
		
		
		NucleotidePairwiseSequenceAlignment actual = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(seq, seq, matrix)
															.gapPenalty(0, 0)
															.useGlobalAlignment()
															.build();
		
		NucleotidePairwiseSequenceAlignment expected =
				new NucleotidePairwiseSequenceAlignmentImpl(
				PairwiseSequenceAlignmentWrapper.wrap(new NucleotideSequenceAlignmentBuilder()
												.addMatches(seq)
												.build(),
												16));
		assertEquals(expected, actual);
		
	}
	@Test
	public void shouldIgnoreInputGaps(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("ACGTACGT").build();
		NucleotideSequence gappedseq = new NucleotideSequenceBuilder("AC-GTA-CGT").build();
		
		
		NucleotidePairwiseSequenceAlignment actual = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(seq, gappedseq, matrix)
															.gapPenalty(0, 0)
															.useGlobalAlignment()
															.build();
		
		NucleotidePairwiseSequenceAlignment expected =
				new NucleotidePairwiseSequenceAlignmentImpl(
				PairwiseSequenceAlignmentWrapper.wrap(new NucleotideSequenceAlignmentBuilder()
												.addMatches(seq)
												.build(),
												16));
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void oneSequenceLongerThanOtherAddGapsToShorterSeq(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGTACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("ACGTACGTNNNN").build();
		
		NucleotidePairwiseSequenceAlignment actual = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(seq1, seq2, matrix)
																.gapPenalty(-2, 0)
																.useGlobalAlignment()
																.build();
	
		NucleotidePairwiseSequenceAlignment expected = this.createExpectedAlignment("ACGTACGT----", "ACGTACGTNNNN",14);
		assertEquals(expected, actual);
		
	}
}
