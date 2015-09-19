/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
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
