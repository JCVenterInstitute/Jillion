package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.NucleotideSequenceAlignmentBuilder;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.junit.Test;

import static org.junit.Assert.*;
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
		
		NucleotidePairwiseSequenceAlignment actual = NucleotideSmithWatermanAligner.align(seq, seq, matrix, -2, 0);
		
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
		NucleotidePairwiseSequenceAlignment actual = NucleotideSmithWatermanAligner.align(seq, ambseq, matrix, -2, 0);
		
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
		PairwiseSequenceAlignment<Nucleotide, NucleotideSequence> actual = NucleotideSmithWatermanAligner.align(seq1, seq2, matrix, -2, 0);
		
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
		
		
		NucleotidePairwiseSequenceAlignment actual = NucleotideSmithWatermanAligner.align(
				seq1, seq2, matrix, -2, 0);
		
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
		
		
		NucleotidePairwiseSequenceAlignment actual = NucleotideSmithWatermanAligner.align(
				seq1, seq2, matrix, -2, 0);
		
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
		
		
		NucleotidePairwiseSequenceAlignment actual = NucleotideSmithWatermanAligner.align(
				seq1, seq2, matrix, -2, 0);
		
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
