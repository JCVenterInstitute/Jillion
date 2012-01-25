package org.jcvi.common.core.align.pairwise;

import static org.junit.Assert.assertEquals;

import org.jcvi.common.core.align.NucleotideSequenceAlignment;
import org.jcvi.common.core.align.NucleotideSequenceAlignmentBuilder;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.junit.Test;

public class TestNucleotideNeedlemanWunschAligner extends AbstractTestNucleotideAligner{

	
	
	@Test
	public void oneBase(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("A").build();
		
		PairwiseSequenceAlignment<Nucleotide, NucleotideSequence> actual = NucleotideNeedlemanWunschAligner.align(seq, seq, matrix, 0, 0);
		
		PairwiseSequenceAlignment<Nucleotide, NucleotideSequence> expected = 
				PairwiseSequenceAlignmentWrapper.wrap(new NucleotideSequenceAlignmentBuilder()
												.addMatches(seq)
												.build(),
												2);
		
		assertEquals(expected, actual);
	}
	@Test
	public void twoBases(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("AC").build();
		
PairwiseSequenceAlignment<Nucleotide, NucleotideSequence> actual = NucleotideNeedlemanWunschAligner.align(seq, seq, matrix, 0, 0);
		
		PairwiseSequenceAlignment<Nucleotide, NucleotideSequence> expected = 
				PairwiseSequenceAlignmentWrapper.wrap(new NucleotideSequenceAlignmentBuilder()
												.addMatches(seq)
												.build(),
												4);
		
		assertEquals(expected, actual);
	}
	@Test
	public void exactMatch(){
		NucleotideSequence seq = new NucleotideSequenceBuilder("ACGTACGT").build();
		
PairwiseSequenceAlignment<Nucleotide, NucleotideSequence> actual = NucleotideNeedlemanWunschAligner.align(seq, seq, matrix, 0, 0);
		
		PairwiseSequenceAlignment<Nucleotide, NucleotideSequence> expected = 
				PairwiseSequenceAlignmentWrapper.wrap(new NucleotideSequenceAlignmentBuilder()
												.addMatches(seq)
												.build(),
												16);
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void oneSequenceLongerThanOtherAddGapsToShorterSeq(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGTACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("ACGTACGTNNNN").build();
		PairwiseSequenceAlignment<Nucleotide, NucleotideSequence> actual = NucleotideNeedlemanWunschAligner.align(seq1, seq2, matrix, -2, 0);
		
	
		PairwiseSequenceAlignment<Nucleotide, NucleotideSequence> expected = this.createExpectedAlignment("ACGTACGT----", "ACGTACGTNNNN",14);
		assertEquals(expected, actual);
		
	}
}
