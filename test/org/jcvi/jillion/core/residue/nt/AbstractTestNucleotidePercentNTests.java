package org.jcvi.jillion.core.residue.nt;

import org.junit.Test;

import static org.junit.Assert.*;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;

public abstract class AbstractTestNucleotidePercentNTests {

	protected abstract NucleotideSequence create(NucleotideSequenceBuilder builder);
	
	@Test
	public void noNsPercentNShouldBeZero() {
		NucleotideSequence seq = create(new NucleotideSequenceBuilder("ACGTACGT"));
		assertEquals(0D,  seq.computePercentN(), 0.001D);
		assertFalse(seq.isAllNs());
	}
	
	@Test
	public void allNsPercentNShouldBeZero() {
		NucleotideSequence seq = create(new NucleotideSequenceBuilder("NNNN"));
		assertEquals(1D,  seq.computePercentN(), 0.001D);
		assertTrue(seq.isAllNs());
	}
	@Test
	public void emptyShouldHaveZeroPercent() {
		NucleotideSequence seq = create(new NucleotideSequenceBuilder(""));
		assertEquals(0D,  seq.computePercentN(), 0.001D);
		assertFalse(seq.isAllNs());
	}
	
	@Test
	public void allGapsShouldHaveZeroPercent() {
		NucleotideSequence seq = create(new NucleotideSequenceBuilder("----"));
		assertEquals(0D,  seq.computePercentN(), 0.001D);
		assertFalse(seq.isAllNs());
	}
	@Test
	public void someGapsShouldHavePercentNotCountGaps() {
		NucleotideSequence seq = create(new NucleotideSequenceBuilder("-ACGN-"));
		assertEquals(0.25D,  seq.computePercentN(), 0.001D);
		assertFalse(seq.isAllNs());
	}
	@Test
	public void onePercent() {
		NucleotideSequence seq = create(NucleotideSequenceTestUtil.createRandom(100).toBuilder()
															.replace(50, Nucleotide.Unknown));
		assertEquals(0.01D,  seq.computePercentN(), 0.001D);
		assertFalse(seq.isAllNs());
	}
	@Test
	public void twoPercentSeparate() {
		NucleotideSequence seq = create(NucleotideSequenceTestUtil.createRandom(100).toBuilder()
															.replace(50, Nucleotide.Unknown)
															.replace(70, Nucleotide.Unknown));
															
		assertEquals(0.02D,  seq.computePercentN(), 0.001D);
		assertFalse(seq.isAllNs());
	}
	@Test
	public void twoPercentTogether() {
		NucleotideSequence seq = create(NucleotideSequenceTestUtil.createRandom(100).toBuilder()
															.replace(new Range.Builder(2).shift(50).build(), "NN")
															);
		assertEquals(0.02D,  seq.computePercentN(), 0.001D);
		assertFalse(seq.isAllNs());
	}
	
	@Test
	public void twentyFivePercent() {
		NucleotideSequence seq = create(NucleotideSequenceTestUtil.create("ACNT", 25).toBuilder());
		assertEquals(0.25D,  seq.computePercentN(), 0.001D);
		assertFalse(seq.isAllNs());
	}
	
	@Test
	public void fiftyPercent() {
		NucleotideSequence seq = create(NucleotideSequenceTestUtil.create("ANNT", 25).toBuilder());
		assertEquals(0.5D,  seq.computePercentN(), 0.001D);
		assertFalse(seq.isAllNs());
	}
	@Test
	public void seventyFivePercent() {
		NucleotideSequence seq = create(NucleotideSequenceTestUtil.create("NNNT", 25).toBuilder());
		assertEquals(0.75D,  seq.computePercentN(), 0.001D);
		assertFalse(seq.isAllNs());
	}
	@Test
	public void ninetyNinePercent() {
		NucleotideSequence seq = create(NucleotideSequenceTestUtil.create("NNNN", 25).toBuilder()
										.replace(50, Nucleotide.Adenine)
										);
		assertEquals(0.99D,  seq.computePercentN(), 0.001D);
		assertFalse(seq.isAllNs());
	}
	@Test
	public void thousanthsPercent() {
		NucleotideSequence seq = create(NucleotideSequenceTestUtil.createRandom(1000).toBuilder()
										.replace(50, Nucleotide.Adenine)
										);
		assertEquals(0.001D,  seq.computePercentN(), 0.001D);
		assertFalse(seq.isAllNs());
	}
	@Test
	public void ninetyNinePointNinePercent() {
		NucleotideSequence seq = create(NucleotideSequenceTestUtil.create("NNNN", 250).toBuilder()
											.replace(50, Nucleotide.Adenine)
											);
		assertEquals(0.999D,  seq.computePercentN(), 0.0001D);
		assertFalse(seq.isAllNs());
	}
}
