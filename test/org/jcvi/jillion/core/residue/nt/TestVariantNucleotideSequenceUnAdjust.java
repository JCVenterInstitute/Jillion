package org.jcvi.jillion.core.residue.nt;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;

import java.util.Iterator;
import java.util.List;

import org.easymock.IAnswer;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.UnderlyingCoverage.UnderlyingCoverageParameters;
import org.jcvi.jillion.core.util.UnAdjustedCoordinateMapper;

import static org.junit.Assert.*;

public class TestVariantNucleotideSequenceUnAdjust {

	private UnderlyingCoverage mockUnderlyingCoverage;
	
	@Before
	public void setup() {
		mockUnderlyingCoverage = createMock(UnderlyingCoverage.class);
		
		expect(mockUnderlyingCoverage.map(isA(UnAdjustedCoordinateMapper.class))).andAnswer(new IAnswer<UnderlyingCoverage>() {

			@Override
			public UnderlyingCoverage answer() throws Throwable {
				UnAdjustedCoordinateMapper mapper = getCurrentArgument(0);
				return p -> mockUnderlyingCoverage.getCoverageFor(p.map(mapper));
			}
			
		}).anyTimes();
	}
	@After
	public void teardown() {
		verify(mockUnderlyingCoverage);
	}
	@Test
	public void noAdjustmentsNoVariants() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("ACT")
				
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), 1, 0, 1, 2));
		//only ask underlying coverage if there are variants
//		expect(mockUnderlyingCoverage.getCoverageFor(0, 1, 2, Direction.FORWARD)).andReturn(null);
		
		replay(mockUnderlyingCoverage);

		VariantNucleotideSequence adjusted = sut.computeUngappedSequence();
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(0,2));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
	@Test
	public void noAdjustmentsNoVariants2Triplets() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("ACTGGC")
				
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		List<List<VariantTriplet>> expectedVariantList = List.of(List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), 1, 0, 1, 2)),
				List.of(new VariantTriplet(Triplet.create('G', 'G', 'C'), 1, 3,4,5)));
		//only ask underlying coverage if there are variants
//		expect(mockUnderlyingCoverage.getCoverageFor(0, 1, 2, Direction.FORWARD)).andReturn(null);
		
		replay(mockUnderlyingCoverage);

		VariantNucleotideSequence adjusted = sut.computeUngappedSequence();
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(0,5));
		assertTrue(iter.hasNext());
		
		assertEquals(expectedVariantList.get(0), iter.next());
		assertTrue(iter.hasNext());
		assertEquals(expectedVariantList.get(1), iter.next());
		assertFalse(iter.hasNext());
	}
	@Test
	public void noAdjustmentsUngap() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("ACT")
				.variant(0, Nucleotide.parse('C'), .2D)
				.variant(1, Nucleotide.parse('T'), .2D)
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), .8D, 0, 1, 2),
				new VariantTriplet(Triplet.create('C', 'T', 'T'), .2D, 0, 1, 2));
		
		expect(mockUnderlyingCoverage.getCoverageFor(UnderlyingCoverageParameters.builder()
				.gappedOffsets(0, 1, 2)
				.variant1(sut.getVariants().get(0))
				.variant2(sut.getVariants().get(1))
				.refs(Nucleotide.parse('A'), Nucleotide.parse('C'), Nucleotide.parse('T'))
				.build())).andReturn(expectedVariantList);
		replay(mockUnderlyingCoverage);

		VariantNucleotideSequence adjusted = sut.computeUngappedSequence();
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(0,2));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
	@Test
	public void noAdjustmentsTrim() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("ACTAAAAA")
				.variant(0, Nucleotide.parse('C'), .2D)
				.variant(1, Nucleotide.parse('T'), .2D)
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), .8D, 0, 1, 2),
				new VariantTriplet(Triplet.create('C', 'T', 'T'), .2D, 0, 1, 2));
		expect(mockUnderlyingCoverage.getCoverageFor(UnderlyingCoverageParameters.builder()
				.gappedOffsets(0, 1, 2)
				.variant1(sut.getVariants().get(0))
				.variant2(sut.getVariants().get(1))
				.refs(Nucleotide.parse('A'), Nucleotide.parse('C'), Nucleotide.parse('T'))
				.build())).andReturn(expectedVariantList);
		
		
		replay(mockUnderlyingCoverage);

		VariantNucleotideSequence adjusted = sut.trim(Range.of(0,3));
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(0,2));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
	@Test
	public void ungapOnlyDownStreamChangesSoNoAdjustment() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("ACT--")
				.variant(0, Nucleotide.parse('C'), .2D)
				.variant(1, Nucleotide.parse('T'), .2D)
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), .8D, 0, 1, 2),
				new VariantTriplet(Triplet.create('C', 'T', 'T'), .2D, 0, 1, 2));
		
		expect(mockUnderlyingCoverage.getCoverageFor(UnderlyingCoverageParameters.builder()
				.gappedOffsets(0, 1, 2)
				.variant1(sut.getVariants().get(0))
				.variant2(sut.getVariants().get(1))
				.refs(Nucleotide.parse('A'), Nucleotide.parse('C'), Nucleotide.parse('T'))
				.build())).andReturn(expectedVariantList);
		
		
		replay(mockUnderlyingCoverage);
		VariantNucleotideSequence adjusted = sut.computeUngappedSequence();
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(0,2));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
	@Test
	public void ungapUpstreamChangeSingleBaseUngapAdjustment() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("-ACT")
				.variant(1, Nucleotide.parse('C'), .2D)
				.variant(2, Nucleotide.parse('T'), .2D)
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), .8D, 1, 2, 3),
				new VariantTriplet(Triplet.create('C', 'T', 'T'), .2D, 1, 2,3));
		expect(mockUnderlyingCoverage.getCoverageFor(UnderlyingCoverageParameters.builder()
				.unadjustedGappedOffsets(1,2,3)
				.gappedOffsets(0, 1, 2)
				.variant1(sut.getVariants().get(1))
				.variant2(sut.getVariants().get(2))
				.refs(Nucleotide.parse('A'), Nucleotide.parse('C'), Nucleotide.parse('T'))
				.build())).andReturn(expectedVariantList);
		
		
		replay(mockUnderlyingCoverage);
		VariantNucleotideSequence adjusted = sut.computeUngappedSequence();
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(0,2));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
	
	@Test
	public void ungapChangeInMiddleOfTriplet() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("A-CT")
				.variant(0, Nucleotide.parse('C'), .2D)
				.variant(2, Nucleotide.parse('T'), .2D)
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), .8D, 0, 2, 3),
				new VariantTriplet(Triplet.create('C', 'T', 'T'), .2D, 0, 2,3));
		
		expect(mockUnderlyingCoverage.getCoverageFor(UnderlyingCoverageParameters.builder()
				.unadjustedGappedOffsets(0,2,3)
				.gappedOffsets(0, 1, 2)
				.variant1(sut.getVariants().get(0))
				.variant2(sut.getVariants().get(2))
				.refs(Nucleotide.parse('A'), Nucleotide.parse('C'), Nucleotide.parse('T'))
				.build())).andReturn(expectedVariantList);
		
		replay(mockUnderlyingCoverage);
		VariantNucleotideSequence adjusted = sut.computeUngappedSequence();
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(0,2));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
	@Test
	public void ungapChangeInMiddleOfTripletConsecutiveMultipleGaps() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("A--CT")
				.variant(0, Nucleotide.parse('C'), .2D)
				.variant(3, Nucleotide.parse('T'), .2D)
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), .8D, 0, 3, 4),
				new VariantTriplet(Triplet.create('C', 'T', 'T'), .2D, 0, 3,4));
		
		expect(mockUnderlyingCoverage.getCoverageFor(UnderlyingCoverageParameters.builder()
				.unadjustedGappedOffsets(0,3,4)
				.gappedOffsets(0, 1, 2)
				.variant1(sut.getVariants().get(0))
				.variant2(sut.getVariants().get(3))
				.refs(Nucleotide.parse('A'), Nucleotide.parse('C'), Nucleotide.parse('T'))
				.build())).andReturn(expectedVariantList);
		
		
		replay(mockUnderlyingCoverage);
		VariantNucleotideSequence adjusted = sut.computeUngappedSequence();
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(0,2));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
	@Test
	public void ungapChangeInMiddleOfTripletMultipleGaps() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("A--C-T")
				.variant(0, Nucleotide.parse('C'), .2D)
				.variant(3, Nucleotide.parse('T'), .2D)
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), .8D, 0, 3, 5),
				new VariantTriplet(Triplet.create('C', 'T', 'T'), .2D, 0, 3,5));
		
		expect(mockUnderlyingCoverage.getCoverageFor(UnderlyingCoverageParameters.builder()
				.unadjustedGappedOffsets(0,3,5)
				.gappedOffsets(0, 1, 2)
				.variant1(sut.getVariants().get(0))
				.variant2(sut.getVariants().get(3))
				.refs(Nucleotide.parse('A'), Nucleotide.parse('C'), Nucleotide.parse('T'))
				.build())).andReturn(expectedVariantList);
		
		
		replay(mockUnderlyingCoverage);
		VariantNucleotideSequence adjusted = sut.computeUngappedSequence();
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(0,2));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
	
	@Test
	public void trimShiftsCoordinates() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("GGACTAAAAA")
				.variant(2, Nucleotide.parse('C'), .2D)
				.variant(3, Nucleotide.parse('T'), .2D)
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), .8D, 2,3,4),
				new VariantTriplet(Triplet.create('C', 'T', 'T'), .2D, 2,3,4));
		expect(mockUnderlyingCoverage.getCoverageFor(UnderlyingCoverageParameters.builder()
				.unadjustedGappedOffsets(2,3,4)
				.gappedOffsets(0,1,2)
				.variant1(sut.getVariants().get(2))
				.variant2(sut.getVariants().get(3))
				.refs(Nucleotide.parse('A'), Nucleotide.parse('C'), Nucleotide.parse('T'))
				.build())).andReturn(expectedVariantList);
		
		
		replay(mockUnderlyingCoverage);

		VariantNucleotideSequence adjusted = sut.trim(Range.of(2,4));
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(0,2));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
	@Test
	public void trimThenUngap() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("GGA-CTAAAAA")
				.variant(2, Nucleotide.parse('C'), .2D)
				.variant(4, Nucleotide.parse('T'), .2D)
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), .8D, 2,4,5),
				new VariantTriplet(Triplet.create('C', 'T', 'T'), .2D, 2,4,5));
		
		expect(mockUnderlyingCoverage.getCoverageFor(UnderlyingCoverageParameters.builder()
				.unadjustedGappedOffsets(2,4,5)
				.gappedOffsets(0, 1, 2)
				.variant1(sut.getVariants().get(2))
				.variant2(sut.getVariants().get(4))
				.refs(Nucleotide.parse('A'), Nucleotide.parse('C'), Nucleotide.parse('T'))
				.build())).andReturn(expectedVariantList);
		
		
		replay(mockUnderlyingCoverage);

		VariantNucleotideSequence adjusted = sut.toBuilder(Range.of(2,5))
													.ungap()
													.build();
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(0,2));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
	
	@Test
	public void appendDoesNothing() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("ACT")
				.variant(0, Nucleotide.parse('C'), .2D)
				.variant(1, Nucleotide.parse('T'), .2D)
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), .8D, 0, 1, 2),
				new VariantTriplet(Triplet.create('C', 'T', 'T'), .2D, 0, 1, 2));
		
		expect(mockUnderlyingCoverage.getCoverageFor(UnderlyingCoverageParameters.builder()
				.unadjustedGappedOffsets(0,1,2)
				.gappedOffsets(0, 1, 2)
				.variant1(sut.getVariants().get(0))
				.variant2(sut.getVariants().get(1))
				.refs(Nucleotide.parse('A'), Nucleotide.parse('C'), Nucleotide.parse('T'))
				.build())).andReturn(expectedVariantList);
		
		
		replay(mockUnderlyingCoverage);

		VariantNucleotideSequence adjusted = sut.toBuilder().append("A").build();
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(0,2));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
	@Test
	public void insertAsAppendDownstreamDoesNothing() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("ACT")
				.variant(0, Nucleotide.parse('C'), .2D)
				.variant(1, Nucleotide.parse('T'), .2D)
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), .8D, 0, 1, 2),
				new VariantTriplet(Triplet.create('C', 'T', 'T'), .2D, 0, 1, 2));
		
		expect(mockUnderlyingCoverage.getCoverageFor(UnderlyingCoverageParameters.builder()
				.unadjustedGappedOffsets(0,1,2)
				.gappedOffsets(0, 1, 2)
				.variant1(sut.getVariants().get(0))
				.variant2(sut.getVariants().get(1))
				.refs(Nucleotide.parse('A'), Nucleotide.parse('C'), Nucleotide.parse('T'))
				.build())).andReturn(expectedVariantList);
		
		
		replay(mockUnderlyingCoverage);

		VariantNucleotideSequence adjusted = sut.toBuilder().insert(3, "A").build();
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(0,2));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
	@Test
	public void insertDownstreamDoesNothing() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("ACTN")
				.variant(0, Nucleotide.parse('C'), .2D)
				.variant(1, Nucleotide.parse('T'), .2D)
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), .8D, 0, 1, 2),
				new VariantTriplet(Triplet.create('C', 'T', 'T'), .2D, 0, 1, 2));
		expect(mockUnderlyingCoverage.getCoverageFor(UnderlyingCoverageParameters.builder()
				.unadjustedGappedOffsets(0,1,2)
				.gappedOffsets(0, 1, 2)
				.variant1(sut.getVariants().get(0))
				.variant2(sut.getVariants().get(1))
				.refs(Nucleotide.parse('A'), Nucleotide.parse('C'), Nucleotide.parse('T'))
				.build())).andReturn(expectedVariantList);
		
		
		replay(mockUnderlyingCoverage);

		VariantNucleotideSequence adjusted = sut.toBuilder().insert(3, "A").build();
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(0,2));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
	@Test
	public void insertUpstream() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("ACT")
				.variant(0, Nucleotide.parse('C'), .2D)
				.variant(1, Nucleotide.parse('T'), .2D)
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), .8D, 0, 1, 2),
				new VariantTriplet(Triplet.create('C', 'T', 'T'), .2D, 0, 1, 2));
		expect(mockUnderlyingCoverage.getCoverageFor(UnderlyingCoverageParameters.builder()
				.unadjustedGappedOffsets(0,1,2)
				.gappedOffsets(1,2,3)
				.variant1(sut.getVariants().get(0))
				.variant2(sut.getVariants().get(1))
				.refs(Nucleotide.parse('A'), Nucleotide.parse('C'), Nucleotide.parse('T'))
				.build())).andReturn(expectedVariantList);
		
		
		replay(mockUnderlyingCoverage);

		VariantNucleotideSequence adjusted = sut.toBuilder().insert(0, "G").build();
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(1,3));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
	@Test
	public void insertInsideCodonbutDownstreamOfVariant() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("ACT")
				.variant(0, Nucleotide.parse('C'), .2D)
				.variant(1, Nucleotide.parse('T'), .2D)
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'G'), .8D, 0, 1, 2),
				new VariantTriplet(Triplet.create('C', 'T', 'G'), .2D, 0, 1, 2));
		
		expect(mockUnderlyingCoverage.getCoverageFor(UnderlyingCoverageParameters.builder()
				.unadjustedGappedOffsets(0,1,2)
				.gappedOffsets(0, 1, 2)
				.variant1(sut.getVariants().get(0))
				.variant2(sut.getVariants().get(1))
				.refs(Nucleotide.parse('A'), Nucleotide.parse('C'), Nucleotide.parse('G'))
				.build())).andReturn(expectedVariantList);
		
		
		replay(mockUnderlyingCoverage);

		VariantNucleotideSequence adjusted = sut.toBuilder().insert(2, "G").build();
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(0,2));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
	@Test
	public void prepend() {
		VariantNucleotideSequence sut = VariantNucleotideSequence.builder().append("ACT")
				.variant(0, Nucleotide.parse('C'), .2D)
				.variant(1, Nucleotide.parse('T'), .2D)
				.build();
		sut.setUnderlyingCoverage(mockUnderlyingCoverage);
		
		
		List<VariantTriplet> expectedVariantList = List.of(new VariantTriplet(Triplet.create('A', 'C', 'T'), .8D, 0, 1, 2),
				new VariantTriplet(Triplet.create('C', 'T', 'T'), .2D, 0, 1, 2));
		expect(mockUnderlyingCoverage.getCoverageFor(UnderlyingCoverageParameters.builder()
				.unadjustedGappedOffsets(0,1,2)
				.gappedOffsets(1,2,3)
				.variant1(sut.getVariants().get(0))
				.variant2(sut.getVariants().get(1))
				.refs(Nucleotide.parse('A'), Nucleotide.parse('C'), Nucleotide.parse('T'))
				.build())).andReturn(expectedVariantList);
		
		
		replay(mockUnderlyingCoverage);

		VariantNucleotideSequence adjusted = sut.toBuilder().prepend( "G").build();
		
		Iterator<List<VariantTriplet>> iter = adjusted.getTriplets(Range.of(1,3));
		assertTrue(iter.hasNext());
		List<VariantTriplet> triplet = iter.next();
		assertFalse(iter.hasNext());
		assertEquals(expectedVariantList, triplet);
	}
}
