package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.NucleotideSequenceAlignment;
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
		
		NucleotideSequenceAlignment actual = NucleotideSmithWatermanAligner.align(seq, seq, matrix, -2, 0);
		
		NucleotideSequenceAlignment expected = new NucleotideSequenceAlignmentBuilder()
												.addMatches(seq)
												.build();
		
		assertEquals(expected, actual);
	}
	@Test
	public void exactMatch(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("ACGTACGT").build();
		
		NucleotideSequenceAlignment actual = NucleotideSmithWatermanAligner.align(seq, seq, matrix, -2, 0);
		
		NucleotideSequenceAlignment expected = new NucleotideSequenceAlignmentBuilder()
									.addMatches(seq)
									.build();
		assertEquals(expected, actual);
		
	}
	@Test
	public void oneSequenceLongerThanOtherShouldPickShorterLength(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGTACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("ACGTACGTNNNN").build();
		NucleotideSequenceAlignment actual = NucleotideSmithWatermanAligner.align(seq1, seq2, matrix, -2, 0);
		
		NucleotideSequenceAlignment expected = new NucleotideSequenceAlignmentBuilder()
											.addMatches(seq1)
											.build();
		assertEquals(expected, actual);
		
	}
	@Test
	public void oneIndel(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGTACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("ACG"+"ACGT").build();
		
		
		NucleotideSequenceAlignment actual = NucleotideSmithWatermanAligner.align(
				seq1, seq2, matrix, -2, 0);
		
		NucleotideSequenceAlignment expected = new NucleotideSequenceAlignmentBuilder()
							.addMatches("ACG")
							.addGap(Nucleotide.Thymine, Nucleotide.Gap)
							.addMatches("ACGT")
							.build();
		
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void twoSeparateIndels(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGTACGTAA").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("ACGACGAA").build();
		
		
		NucleotideSequenceAlignment actual = NucleotideSmithWatermanAligner.align(
				seq1, seq2, matrix, -2, 0);
		
		NucleotideSequenceAlignment expected = new NucleotideSequenceAlignmentBuilder()
												.addMatches("ACG")
												.addGap(Nucleotide.Thymine, Nucleotide.Gap)
												.addMatches("ACG")
												.addGap(Nucleotide.Thymine, Nucleotide.Gap)
												.addMatches("AA")
												.build();
		
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void testSubSequenceExactMatch(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGTACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder(  "GTACG").build();
		
		
		NucleotideSequenceAlignment actual = NucleotideSmithWatermanAligner.align(
				seq1, seq2, matrix, -2, 0);
		
		NucleotideSequenceAlignment expected = new NucleotideSequenceAlignmentBuilder()
											.addMatches(seq2)
											.build();
		assertEquals(expected, actual);
	}
	
	
	
	
	
}
