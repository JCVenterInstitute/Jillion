package org.jcvi.common.core.align.pairwise;

import static org.junit.Assert.assertEquals;

import org.jcvi.common.core.align.NucleotideSequenceAlignmentBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;

public class TestNucleotideNeedlemanWunschAligner extends AbstractTestNucleotideAligner{

	
	
	@Test
	public void oneBase(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("A").build();
		
		NucleotidePairwiseSequenceAlignment actual = NucleotideNeedlemanWunschAligner.align(seq, seq, matrix, 0, 0);
		
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
		
		NucleotidePairwiseSequenceAlignment actual = NucleotideNeedlemanWunschAligner.align(seq, seq, matrix, 0, 0);
		
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
		
		NucleotidePairwiseSequenceAlignment actual = NucleotideNeedlemanWunschAligner.align(seq, seq, matrix, 0, 0);
		
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
		
		NucleotidePairwiseSequenceAlignment actual = NucleotideNeedlemanWunschAligner.align(seq1, seq2, matrix, -2, 0);
		
	
		NucleotidePairwiseSequenceAlignment expected = this.createExpectedAlignment("ACGTACGT----", "ACGTACGTNNNN",14);
		assertEquals(expected, actual);
		
	}
}
