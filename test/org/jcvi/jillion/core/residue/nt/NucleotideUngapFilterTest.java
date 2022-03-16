package org.jcvi.jillion.core.residue.nt;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicBoolean;


public class NucleotideUngapFilterTest {

	@Test
	public void removeAllGaps() {
		assertEquals("ACGTACGTGTTG",
				new NucleotideSequenceBuilder("ACGT-ACGT--GTTG")
				.ungap(r-> true)
				.toString());
	}
	@Test
	public void nullPredicateShouldThrowNPE() {
		assertThrows(NullPointerException.class, ()->
				new NucleotideSequenceBuilder("ACGT-ACGT--GTTG")
				.ungap(null)
				.toString()
				);
				
	}
	
	@Test
	public void noGapsShouldNotCallPredicate() {
		assertEquals("ACGTACGTGTTG",
				new NucleotideSequenceBuilder("ACGTACGTGTTG")
				.ungap(r-> {throw new IllegalStateException("fail");})
				.toString());
	}
	
	@Test
	public void removeNonTriplets() {
		assertEquals("ACGTACGTGTT---GTGTG------GT",
				new NucleotideSequenceBuilder("ACGT-ACGT--GTT---GTGTG------G-T")
				.ungap(r-> r.getLength()% 3 !=0)
				.toString());
	}
	
	@Test
	public void filterReturnsFalseDonotRemoveAnyGaps() {
		assertEquals("ACGT-ACGT--GTT---GTGTG------G-T",
				new NucleotideSequenceBuilder("ACGT-ACGT--GTT---GTGTG------G-T")
				.ungap(r-> false)
				.toString());
	}
	
	@Test
	public void onlyRemoveFirstGap() {
		AtomicBoolean flag= new AtomicBoolean(true);
		assertEquals("ACGTACGT--GTT---GTGTG------G-T",
				new NucleotideSequenceBuilder("ACGT-ACGT--GTT---GTGTG------G-T")
				.ungap(r-> flag.compareAndSet(true, false))
				.toString());
	}
	
	@Test
	public void OnlyRemoveGapsBeforeSpecifiedOffset() {
		assertEquals("ACGTACGTGTT---GTGTG------G-T",
				new NucleotideSequenceBuilder("ACGT-ACGT--GTT---GTGTG------G-T")
				.ungap(r-> r.getEnd() < 15)
				.toString());
	}
	
}
