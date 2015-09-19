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

import org.jcvi.jillion.align.pairwise.NucleotidePairwiseSequenceAlignment;
import org.jcvi.jillion.align.pairwise.NucleotidePairwiseSequenceAlignmentImpl;
import org.jcvi.jillion.align.pairwise.NucleotideSmithWatermanAligner;
import org.jcvi.jillion.align.pairwise.PairwiseAlignmentBuilder;
import org.jcvi.jillion.align.pairwise.PairwiseSequenceAlignmentWrapper;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.align.NucleotideSequenceAlignmentBuilder;
import org.junit.Test;
public class TestNucleotideSmithWatermanAligner extends AbstractTestNucleotideAligner{

	
	@Test
	public void oneBase(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("A").build();
		
		NucleotidePairwiseSequenceAlignment actual = NucleotideSmithWatermanAligner.align(seq, seq, matrix, -2, 0);
		
		NucleotidePairwiseSequenceAlignment expected =
			new NucleotidePairwiseSequenceAlignmentImpl(PairwiseSequenceAlignmentWrapper.wrap(new NucleotideSequenceAlignmentBuilder()
												.addMatches(seq)
												.build(), 2));
		
		assertEquals(expected, actual);
	}
	@Test
	public void exactMatch(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("ACGTACGT").build();
		
		
		NucleotidePairwiseSequenceAlignment actual = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(seq, seq, matrix)
																				.gapPenalty(-2, 0)
																				.useLocalAlignment()
																				.build();
		NucleotidePairwiseSequenceAlignment expected =
				new NucleotidePairwiseSequenceAlignmentImpl(PairwiseSequenceAlignmentWrapper.wrap(new NucleotideSequenceAlignmentBuilder()
												.addMatches(seq)
												.build(), 16));
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void withAmbiguityCodes(){
		NucleotideSequence seq = new NucleotideSequenceBuilder(   "ACGTACGT").build();
		NucleotideSequence ambseq = new NucleotideSequenceBuilder("ACRTACGT").build();
	
		NucleotidePairwiseSequenceAlignment actual = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(seq, ambseq, matrix)
																				.gapPenalty(-2, 0)
																				.build();
		NucleotidePairwiseSequenceAlignment expected =
				new NucleotidePairwiseSequenceAlignmentImpl(PairwiseSequenceAlignmentWrapper.wrap(new NucleotideSequenceAlignmentBuilder()
												.addMatches("AC")
												.addMismatches("G", "R")
												.addMatches("TACGT")
												.build(), 13));
		assertEquals(expected, actual);
		
	}
	@Test
	public void oneSequenceLongerThanOtherShouldPickShorterLength(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGTACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("ACGTACGTNNNN").build();
	
		NucleotidePairwiseSequenceAlignment actual = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(seq1, seq2, matrix)
																		.gapPenalty(-2, 0)
																		.build();

		
		NucleotidePairwiseSequenceAlignment expected =
				new NucleotidePairwiseSequenceAlignmentImpl( PairwiseSequenceAlignmentWrapper.wrap(new NucleotideSequenceAlignmentBuilder()
											.addMatches(seq1)
											.build(), 16));
		assertEquals(expected, actual);
		
	}
	@Test
	public void oneIndel(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGTACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("ACG"+"ACGT").build();
		
		NucleotidePairwiseSequenceAlignment actual = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(seq1, seq2, matrix)
																.gapPenalty(-2, 0)
																.build();
		
		NucleotidePairwiseSequenceAlignment expected =
				new NucleotidePairwiseSequenceAlignmentImpl( PairwiseSequenceAlignmentWrapper.wrap(new NucleotideSequenceAlignmentBuilder()
							.addMatches("ACG")
							.addGap(Nucleotide.Thymine, Nucleotide.Gap)
							.addMatches("ACGT")
							.build(),
							12));
		
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void twoSeparateIndels(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGTACGTAA").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("ACGACGAA").build();

		
		NucleotidePairwiseSequenceAlignment actual = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(seq1, seq2, matrix)
																	.gapPenalty(-2, 0)
																	.build();
		
		NucleotidePairwiseSequenceAlignment expected =
				new NucleotidePairwiseSequenceAlignmentImpl(
				PairwiseSequenceAlignmentWrapper.wrap(
				new NucleotideSequenceAlignmentBuilder()
												.addMatches("ACG")
												.addGap(Nucleotide.Thymine, Nucleotide.Gap)
												.addMatches("ACG")
												.addGap(Nucleotide.Thymine, Nucleotide.Gap)
												.addMatches("AA")
												.build(),
												12));
		
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void testSubSequenceExactMatch(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGTACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder(  "GTACG").build();

		
		NucleotidePairwiseSequenceAlignment actual = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(seq1, seq2, matrix)
																.gapPenalty(-2, 0)
																.build();
		
		NucleotidePairwiseSequenceAlignment expected =
				new NucleotidePairwiseSequenceAlignmentImpl( 
				PairwiseSequenceAlignmentWrapper.wrap(
						new NucleotideSequenceAlignmentBuilder()
											.setAlignmentOffsets(2, 0)
											.addMatches(seq2)
											.build(),
											10));
		assertEquals(expected, actual);
	}
	
	
	
	
	
}
