package org.jcvi.jillion.core.residue.nt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
public class TestNucleotideSequenceBuilderIsEqualToIgnoringGaps {

	@Test
	public void shouldNeverEqualNull(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("ACGT");
		assertFalse(seq1.isEqualToIgnoringGaps(null));
	}
	
	@Test
	public void sameSequenceShouldBeEqual(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("ACGT");
		assertTrue(seq1.isEqualToIgnoringGaps(seq1.build()));
	}
	
	@Test
	public void differentSequenceShouldNotBeEqual(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("ACGT");
		NucleotideSequenceBuilder seq2 = new NucleotideSequenceBuilder("NNNN");
		
		assertFalse(seq1.isEqualToIgnoringGaps(seq2.build()));
		assertFalse(seq2.isEqualToIgnoringGaps(seq1.build()));
	}
	
	@Test
	public void subSequenceShouldNotBeEqual(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("ACGT");
		NucleotideSequenceBuilder seq2 = new NucleotideSequenceBuilder("ACGTNN");
		
		assertFalse(seq1.isEqualToIgnoringGaps(seq2.build()));
		assertFalse(seq2.isEqualToIgnoringGaps(seq1.build()));
	}
	@Test
	public void gappySubSequenceShouldNotBeEqual(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("A-CGT");
		NucleotideSequenceBuilder seq2 = new NucleotideSequenceBuilder("AC-GTN-N");
		
		assertFalse(seq1.isEqualToIgnoringGaps(seq2.build()));
		assertFalse(seq2.isEqualToIgnoringGaps(seq1.build()));
	}
	@Test
	public void sameSequenceWithGapsShouldBeEqual(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("ACGT");
		NucleotideSequenceBuilder seq2 = new NucleotideSequenceBuilder("A--CG-T");
		
		assertTrue(seq1.isEqualToIgnoringGaps(seq2.build()));
		assertTrue(seq2.isEqualToIgnoringGaps(seq1.build()));
	}
	
	@Test
	public void sameSequenceWithTrailingGapsShouldBeEqual(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("ACGT");
		NucleotideSequenceBuilder seq2 = new NucleotideSequenceBuilder("ACGT---");
		
		assertTrue(seq1.isEqualToIgnoringGaps(seq2.build()));
		assertTrue(seq2.isEqualToIgnoringGaps(seq1.build()));
	}
	
	@Test
	public void allGapsShouldBeEqual(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("--");
		NucleotideSequenceBuilder seq2 = new NucleotideSequenceBuilder("--------");
		
		assertTrue(seq1.isEqualToIgnoringGaps(seq2.build()));
		assertTrue(seq2.isEqualToIgnoringGaps(seq1.build()));
	}
	
	@Test
	public void allGapsEqualEmpty(){
		NucleotideSequenceBuilder seq1 = new NucleotideSequenceBuilder("");
		NucleotideSequenceBuilder seq2 = new NucleotideSequenceBuilder("--------");
		
		assertTrue(seq1.isEqualToIgnoringGaps(seq2.build()));
		assertTrue(seq2.isEqualToIgnoringGaps(seq1.build()));
	}
}
