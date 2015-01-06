package org.jcvi.jillion.testutils;

import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestNucleotideSequenceTestUtil {

	@Test
	public void createFromString(){
		String seq = "ACGTACGT"	;
		assertEquals(new NucleotideSequenceBuilder(seq).build(), NucleotideSequenceTestUtil.create(seq));
	}
	
	@Test
	public void empty(){
		assertTrue(NucleotideSequenceTestUtil.emptySeq().getLength() ==0);
	}
}
