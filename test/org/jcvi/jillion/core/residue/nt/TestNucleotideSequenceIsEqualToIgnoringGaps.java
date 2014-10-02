package org.jcvi.jillion.core.residue.nt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
public class TestNucleotideSequenceIsEqualToIgnoringGaps {

	@Test
	public void shouldNeverEqualNull(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGT").build();
		assertFalse(seq1.isEqualToIgnoringGaps(null));
	}
	
	@Test
	public void sameSequenceShouldBeEqual(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGT").build();
		assertTrue(seq1.isEqualToIgnoringGaps(seq1));
	}
	
	@Test
	public void differentSequenceShouldNotBeEqual(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("NNNN").build();
		
		assertFalse(seq1.isEqualToIgnoringGaps(seq2));
		assertFalse(seq2.isEqualToIgnoringGaps(seq1));
	}
	
	@Test
	public void subSequenceShouldNotBeEqual(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("ACGTNN").build();
		
		assertFalse(seq1.isEqualToIgnoringGaps(seq2));
		assertFalse(seq2.isEqualToIgnoringGaps(seq1));
	}
	@Test
	public void gappySubSequenceShouldNotBeEqual(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("A-CGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("AC-GTN-N").build();
		
		assertFalse(seq1.isEqualToIgnoringGaps(seq2));
		assertFalse(seq2.isEqualToIgnoringGaps(seq1));
	}
	@Test
	public void sameSequenceWithGapsShouldBeEqual(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("A--CG-T").build();
		
		assertTrue(seq1.isEqualToIgnoringGaps(seq2));
		assertTrue(seq2.isEqualToIgnoringGaps(seq1));
	}
	
	@Test
	public void sameSequenceWithTrailingGapsShouldBeEqual(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("ACGT").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("ACGT---").build();
		
		assertTrue(seq1.isEqualToIgnoringGaps(seq2));
		assertTrue(seq2.isEqualToIgnoringGaps(seq1));
	}
	
	@Test
	public void allGapsShouldBeEqual(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("--").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("--------").build();
		
		assertTrue(seq1.isEqualToIgnoringGaps(seq2));
		assertTrue(seq2.isEqualToIgnoringGaps(seq1));
	}
	
	@Test
	public void allGapsEqualEmpty(){
		NucleotideSequence seq1 = new NucleotideSequenceBuilder("").build();
		NucleotideSequence seq2 = new NucleotideSequenceBuilder("--------").build();
		
		assertTrue(seq1.isEqualToIgnoringGaps(seq2));
		assertTrue(seq2.isEqualToIgnoringGaps(seq1));
	}
}
