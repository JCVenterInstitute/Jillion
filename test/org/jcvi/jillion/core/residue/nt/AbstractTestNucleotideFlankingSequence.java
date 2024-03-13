package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.core.Range;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator.OfInt;

public abstract class AbstractTestNucleotideFlankingSequence {

	protected abstract NucleotideSequence create(String seq);
	
	private static void assertIteratorValues(OfInt actualIter, int...values) {
		for(int i=0; i< values.length; i++) {
			assertTrue("should have next for value "+values[i], actualIter.hasNext());
			assertEquals(actualIter.nextInt(), values[i]);
		}
		assertFalse(actualIter.hasNext());
		assertThrows(NoSuchElementException.class, ()->actualIter.nextInt());
	}
	@Test
	public void noGaps() {
		NucleotideSequence sut = create("ACGTACGT");
		
		assertEquals(4, sut.getLeftFlankingNonGapOffsetFor(4));
		assertEquals(4, sut.getRightFlankingNonGapOffsetFor(4));
		
		assertIteratorValues(sut.createLeftFlankingNonGapIterator(4), 4,3,2,1,0);
		
		assertEquals(Range.of(0,7), sut.getContractingFlankingNonGapRangeFor(Range.of(0,7)));
		assertEquals(Range.of(0,7), sut.getContractingFlankingNonGapRangeFor(0,7));
		
		assertEquals(Range.of(0,7), sut.getExpandingFlankingNonGapRangeFor(Range.of(0,7)));
		assertEquals(Range.of(0,7), sut.getExpandingFlankingNonGapRangeFor(0,7));
		
		assertIteratorValues(sut.createRightFlankingNonGapIterator(4), 4,5,6,7);
		
	}
	
	@Test
	public void gapUpStream() {
		NucleotideSequence sut = create("ACGTACGT-");
		
		assertEquals(4, sut.getLeftFlankingNonGapOffsetFor(4));
		assertEquals(4, sut.getRightFlankingNonGapOffsetFor(4));

		assertIteratorValues(sut.createLeftFlankingNonGapIterator(5), 5,4,3,2,1,0);
		
		assertEquals(Range.of(0,7), sut.getContractingFlankingNonGapRangeFor(Range.of(0,7)));
		assertEquals(Range.of(0,7), sut.getContractingFlankingNonGapRangeFor(0,7));
		
		assertEquals(Range.of(0,5), sut.getExpandingFlankingNonGapRangeFor(Range.of(0,5)));
		assertEquals(Range.of(0,5), sut.getExpandingFlankingNonGapRangeFor(0,5));
		
		assertEquals(Range.of(0,7), sut.getContractingFlankingNonGapRangeFor(Range.of(0,8)));
		assertEquals(Range.of(0,7), sut.getContractingFlankingNonGapRangeFor(0,8));
		
		assertIteratorValues(sut.createRightFlankingNonGapIterator(4), 4,5,6,7);
	}
	
	@Test
	public void rightFlankBeyondSequence() {
		NucleotideSequence sut = create("ACGTACGT-");
		assertEquals(9,sut.getRightFlankingNonGapOffsetFor(8));
	}
	@Test
	public void leftFlankBeyondSequence() {
		NucleotideSequence sut = create("ACGTACGT-");
		assertEquals(-1,sut.getRightFlankingNonGapOffsetFor(-1));
	}
	
	@Test
	public void gapDownStream() {
		NucleotideSequence sut = create("-ACGTACGT");
		
		assertEquals(4, sut.getLeftFlankingNonGapOffsetFor(4));
		assertEquals(4, sut.getRightFlankingNonGapOffsetFor(4));
		
		assertIteratorValues(sut.createLeftFlankingNonGapIterator(5), 5,4,3,2,1);
		assertIteratorValues(sut.createRightFlankingNonGapIterator(4), 4,5,6,7,8);
	}
	@Test
	public void oneGap() {
		NucleotideSequence sut = create("ACGT-CGT");
		
		assertEquals(3, sut.getLeftFlankingNonGapOffsetFor(4));
		assertEquals(5, sut.getRightFlankingNonGapOffsetFor(4));
		
		assertIteratorValues(sut.createLeftFlankingNonGapIterator(5), 5,3,2,1,0);
		
		assertIteratorValues(sut.createRightFlankingNonGapIterator(2), 2,3,5,6,7);
		assertIteratorValues(sut.createRightFlankingNonGapIterator(4), 5,6,7);
	}
	@Test
	public void twoConnectedGaps() {
		NucleotideSequence sut = create("ACGT--GT");
		
		assertEquals(3, sut.getLeftFlankingNonGapOffsetFor(4));
		assertEquals(6, sut.getRightFlankingNonGapOffsetFor(4));

		assertIteratorValues(sut.createLeftFlankingNonGapIterator(5), 3,2,1,0);
		assertIteratorValues(sut.createRightFlankingNonGapIterator(2), 2,3,6,7);
	}
	
	@Test
	public void twoSeparateGaps() {
		NucleotideSequence sut = create("ACGT-G-T");
		
		assertEquals(0, sut.getLeftFlankingNonGapOffsetFor(0));
		assertEquals(3, sut.getLeftFlankingNonGapOffsetFor(4));
		assertEquals(5, sut.getRightFlankingNonGapOffsetFor(4));
		assertEquals(5, sut.getRightFlankingNonGapOffsetFor(5));
		
		assertEquals(5, sut.getLeftFlankingNonGapOffsetFor(6));
		assertEquals(7, sut.getRightFlankingNonGapOffsetFor(6));
		
		assertEquals(7, sut.getLeftFlankingNonGapOffsetFor(7));
		assertEquals(7, sut.getRightFlankingNonGapOffsetFor(7));
		
		assertEquals(0, sut.getNumberOfGapsUntil(0));
		assertEquals(0, sut.getNumberOfGapsUntil(1));
		assertEquals(1, sut.getNumberOfGapsUntil(4));
		assertEquals(1, sut.getNumberOfGapsUntil(5));
		assertEquals(2, sut.getNumberOfGapsUntil(6));
		assertEquals(2, sut.getNumberOfGapsUntil(7));
		
		assertIteratorValues(sut.createLeftFlankingNonGapIterator(5), 5,3,2,1,0);
	}
	@Test
	public void longShift() {
		NucleotideSequence sut = create("AC----GT");
		
		assertEquals(1, sut.getLeftFlankingNonGapOffsetFor(4));
		assertEquals(6, sut.getRightFlankingNonGapOffsetFor(4));
		assertEquals(6, sut.getRightFlankingNonGapOffsetFor(2));
		
		assertIteratorValues(sut.createLeftFlankingNonGapIterator(5), 1,0);
	}
}
