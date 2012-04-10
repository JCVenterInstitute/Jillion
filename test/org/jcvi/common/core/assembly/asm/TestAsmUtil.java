package org.jcvi.common.core.assembly.asm;

import java.util.Arrays;
import java.util.Collections;

import org.jcvi.common.core.symbol.residue.nt.Nucleotides;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestAsmUtil {
	String ungappedSequence = "ACGTACGTACGT";
	@Test
	public void computeGappedSequenceWithNoGaps(){
		
		assertEquals(ungappedSequence,
				AsmUtil.computeGappedSequence(
						Nucleotides.parse(ungappedSequence), 
						Collections.<Integer>emptyList()));
	}
	
	@Test
	public void computeGappedSequenceWith1Gap(){
		assertEquals("ACGTA-CGTACGT",
				AsmUtil.computeGappedSequence(
						Nucleotides.parse(ungappedSequence), 
						Arrays.asList(5)));
	}
	@Test
	public void computeGappedSequenceWith2Gaps(){
		assertEquals("ACGTA-CG-TACGT",
				AsmUtil.computeGappedSequence(
						Nucleotides.parse(ungappedSequence), 
						Arrays.asList(5,8)));
	}
	@Test
	public void computeGappedSequenceWith2ConsecutiveGaps(){
		assertEquals("AC--GT",
				AsmUtil.computeGappedSequence(
						Nucleotides.parse("ACGT"), 
						Arrays.asList(2,2)));
	}
	
}
