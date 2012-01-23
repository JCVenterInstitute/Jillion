package org.jcvi.common.core.align.pairwise;

import static org.junit.Assert.assertEquals;

import org.jcvi.common.core.align.NucleotideSequenceAlignment;
import org.jcvi.common.core.align.NucleotideSequenceAlignmentBuilder;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.junit.Test;

public class TestNucleotideNeedlemanWunschAligner extends AbstractTestNucleotideAligner{

	
	
	@Test
	public void oneBase(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("A").build();
		
		NucleotideSequenceAlignment actual = NucleotideNeedlemanWunschAligner.align(seq, seq, matrix, 0, 0);
		
		NucleotideSequenceAlignment expected = new NucleotideSequenceAlignmentBuilder()
												.addMatches(seq)
												.build();
		
		assertEquals(expected, actual);
	}
	@Test
	public void twoBases(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("AC").build();
		
		NucleotideSequenceAlignment actual = NucleotideNeedlemanWunschAligner.align(seq, seq, matrix, 0, 0);
		
		NucleotideSequenceAlignment expected = new NucleotideSequenceAlignmentBuilder()
												.addMatches(seq)
												.build();
		
		assertEquals(expected, actual);
	}
	@Test
	public void exactMatch(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("ACGTACGT").build();
		
		NucleotideSequenceAlignment actual = NucleotideNeedlemanWunschAligner.align(seq, seq, matrix, 0, 0);
		
		NucleotideSequenceAlignment expected = new NucleotideSequenceAlignmentBuilder()
									.addMatches(seq)
									.build();
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void oneSequenceLongerThanOtherAddGapsToShorterSeq(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGTACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("ACGTACGTNNNN").build();
		NucleotideSequenceAlignment actual = NucleotideNeedlemanWunschAligner.align(seq1, seq2, matrix, -2, 0);
		
		NucleotideSequenceAlignment expected = this.createExpectedAlignment("ACGTACGT----", "ACGTACGTNNNN");
		assertEquals(expected, actual);
		
	}
}
