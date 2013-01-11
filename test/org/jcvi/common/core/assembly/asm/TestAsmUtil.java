package org.jcvi.common.core.assembly.asm;

import java.util.Arrays;
import java.util.Collections;

import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestAsmUtil {
	String ungappedSequence = "ACGTACGTACGT";
	@Test
	public void computeGappedSequenceWithNoGaps(){
		
		assertEquals(ungappedSequence,
				AsmUtil.computeGappedSequence(
						asBuilder(ungappedSequence), 
						Collections.<Integer>emptyList())
						.toString()
						);
	}
	
	@Test
	public void computeGappedSequenceWith1Gap(){
		assertEquals("ACGTA-CGTACGT",
				AsmUtil.computeGappedSequence(
						asBuilder(ungappedSequence), 
						Arrays.asList(5))
						.toString());
	}
	@Test
	public void computeGappedSequenceWith2Gaps(){
		assertEquals("ACGTA-CG-TACGT",
				AsmUtil.computeGappedSequence(
						asBuilder(ungappedSequence), 
						Arrays.asList(5,8))
						.toString());
	}
	@Test
	public void computeGappedSequenceWith2ConsecutiveGaps(){
		assertEquals("AC--GT",
				AsmUtil.computeGappedSequence(
						asBuilder("ACGT"), 
						Arrays.asList(2,2))
						.toString());
	}
	
	
	private static NucleotideSequenceBuilder asBuilder(String s){
		return new NucleotideSequenceBuilder(s);
	}
}
