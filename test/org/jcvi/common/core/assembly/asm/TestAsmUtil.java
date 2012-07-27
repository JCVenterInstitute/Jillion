package org.jcvi.common.core.assembly.asm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestAsmUtil {
	String ungappedSequence = "ACGTACGTACGT";
	@Test
	public void computeGappedSequenceWithNoGaps(){
		
		assertEquals(ungappedSequence,
				AsmUtil.computeGappedSequence(
						asList(ungappedSequence), 
						Collections.<Integer>emptyList()));
	}
	
	@Test
	public void computeGappedSequenceWith1Gap(){
		assertEquals("ACGTA-CGTACGT",
				AsmUtil.computeGappedSequence(
						asList(ungappedSequence), 
						Arrays.asList(5)));
	}
	@Test
	public void computeGappedSequenceWith2Gaps(){
		assertEquals("ACGTA-CG-TACGT",
				AsmUtil.computeGappedSequence(
						asList(ungappedSequence), 
						Arrays.asList(5,8)));
	}
	@Test
	public void computeGappedSequenceWith2ConsecutiveGaps(){
		assertEquals("AC--GT",
				AsmUtil.computeGappedSequence(
						asList("ACGT"), 
						Arrays.asList(2,2)));
	}
	
	
	private static List<Nucleotide> asList(String s){
		return new NucleotideSequenceBuilder(s)
						.asList();
	}
}
