package org.jcvi.jillion.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
public class TestNucleotideSequenceTestUtil {

	@Test
	public void createFromString(){
		String seq = "ACGTACGT"	;
		assertEquals(new NucleotideSequenceBuilder(seq).build(), NucleotideSequenceTestUtil.create(seq));
	}
	
	@Test
	public void createWithTimes(){		
		assertEquals("ACGTACGTACGTACGTACGT", NucleotideSequenceTestUtil.create("ACGT", 5).toString());
	}
	
	@Test
	public void empty(){
		assertTrue(NucleotideSequenceTestUtil.emptySeq().getLength() ==0);
	}
	
	@Test
	public void randomSeq(){
		int length = 30;
		Set<NucleotideSequence> seqs = new HashSet<>();
		for(int i=0; i<10; i++){
			NucleotideSequence seq = NucleotideSequenceTestUtil.createRandom(length);
			
			assertEquals(length, seq.getLength());
			onlyContainsACGT(seq);
			seqs.add(seq);
		}
		
		assertTrue("random seqs returns are not random",seqs.size() >1);
	}

	private void onlyContainsACGT(NucleotideSequence seq) {
		for(Nucleotide n : seq){
			switch (n){
			case Adenine:
			case Guanine:
			case Cytosine:
			case Thymine: break;
			default : throw new AssertionError("seq contains non ACGT : " + n);
			}
		}
		
	}
}
