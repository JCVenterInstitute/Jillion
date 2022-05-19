package org.jcvi.jillion.core.residue.nt;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestNucleotideSequenceComputeGC {

	@Test
	public void emptySeqShouldHave0GC() {
		assertGC(0, "");
	}
	
	private void assertGC(double expected, String seq) {
		assertEquals(expected, NucleotideSequence.of(seq).computePercentGC(), 0.01D);
	}
	
	@Test
	public void nonEmptySeqShouldHave0GC() {
		assertGC(0, "AT");
	}
	
	@Test
	public void allGsShouldBe1() {
		assertGC(1, "GGGGG");
	}
	
	@Test
	public void allCsShouldBe1() {
		assertGC(1, "CCCCCC");
	}
	@Test
	public void allSsShouldBe1() {
		assertGC(1, "SSSSS");
	}
	
	@Test
	public void allGsAndCsShouldBe1() {
		assertGC(1, "CCGGGGGCCCCGGGG");
	}
	
	@Test
	public void gapsDontAffectGCCalculation() {
		assertGC(1, "CC-GGGGGC-CCCGG--GG");
	}
	
	@Test
	public void mixOfGCandAT() {
		assertGC(.5D, "ACGT");
	}
	
	@Test
	public void mixOfGCandATAndGap() {
		assertGC(.5D, "AC-GT");
	}
	
	@Test
	public void highGCandAT() {
		assertGC(.6D, "TTACGTGGGC");
		assertGC(.8D, "GSACGTGGGC");
	}
	
	@Test
	public void lowGCandAT() {
		assertGC(.3D, "TTATGTGGAA");
	}
}
