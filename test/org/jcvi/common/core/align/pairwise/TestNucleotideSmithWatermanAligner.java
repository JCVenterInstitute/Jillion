package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.NucleotideSequenceAlignment;
import org.jcvi.common.core.align.NucleotideSequenceAlignmentBuilder;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestNucleotideSmithWatermanAligner {

	private final NucleotideScoringMatrix matrix;
	public TestNucleotideSmithWatermanAligner(){
		DefaultNucleotideScoringMatrix.Builder builder = new DefaultNucleotideScoringMatrix.Builder(-4F);
		
		for(Nucleotide n : Nucleotide.values()){
			builder.set(n, n, 4);
		}
		matrix = builder.build();
	}
	@Test
	public void oneBase(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("A").build();
		
		NucleotideSmithWatermanAligner aligner = new NucleotideSmithWatermanAligner(seq, seq, matrix, 0, 0);
		
		NucleotideSequenceAlignment expected = new NucleotideSequenceAlignmentBuilder()
												.addMatches(seq)
												.build();
		
		assertEquals(expected, aligner.getSequenceAlignment());
	}
	@Test
	public void exactMatch(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("ACGTACGT").build();
		
		NucleotideSmithWatermanAligner aligner = new NucleotideSmithWatermanAligner(seq, seq, matrix, 0, 0);
		
		NucleotideSequenceAlignment expected = new NucleotideSequenceAlignmentBuilder()
									.addMatches(seq)
									.build();
		assertEquals(expected, aligner.getSequenceAlignment());
		
	}
	@Test
	public void oneSequenceLongerThanOtherShouldPickShorterLength(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGTACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("ACGTACGTNNNN").build();
		NucleotideSmithWatermanAligner aligner = new NucleotideSmithWatermanAligner(seq1, seq2, matrix, 0, 0);
		
		NucleotideSequenceAlignment expected = new NucleotideSequenceAlignmentBuilder()
											.addMatches(seq1)
											.build();
		assertEquals(expected, aligner.getSequenceAlignment());
		
	}
	@Test
	public void oneIndel(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGTACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("ACG"+"ACGT").build();
		
		
		NucleotideSmithWatermanAligner aligner = new NucleotideSmithWatermanAligner(
				seq1, seq2, matrix, 0, 0);
		
		NucleotideSequenceAlignment expected = new NucleotideSequenceAlignmentBuilder()
							.addMatches("ACG")
							.addGap(Nucleotide.Thymine, Nucleotide.Gap)
							.addMatches("ACGT")
							.build();
		
		assertEquals(expected, aligner.getSequenceAlignment());
		
	}
	
	@Test
	public void twoSeparateIndels(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGTACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("ACGACT").build();
		
		
		NucleotideSmithWatermanAligner aligner = new NucleotideSmithWatermanAligner(
				seq1, seq2, matrix, 0, 0);
		
		NucleotideSequenceAlignment expected = new NucleotideSequenceAlignmentBuilder()
												.addMatches("ACG")
												.addGap(Nucleotide.Thymine, Nucleotide.Gap)
												.addMatches("AC")
												.addGap(Nucleotide.Guanine, Nucleotide.Gap)
												.addMatches("T")
												.build();
		
		assertEquals(expected, aligner.getSequenceAlignment());
		
	}
	
	@Test
	public void testSubSequenceExactMatch(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGTACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder(  "GTACG").build();
		
		
		NucleotideSmithWatermanAligner aligner = new NucleotideSmithWatermanAligner(
				seq1, seq2, matrix, 0, 0);
		
		NucleotideSequenceAlignment expected = new NucleotideSequenceAlignmentBuilder()
											.addMatches(seq2)
											.build();
		assertEquals(expected, aligner.getSequenceAlignment());
	}
}
