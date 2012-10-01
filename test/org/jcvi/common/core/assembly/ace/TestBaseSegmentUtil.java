package org.jcvi.common.core.assembly.ace;

import java.util.Arrays;
import java.util.List;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.ace.BaseSegmentUtil.NoReadMatchesConsensusException;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestBaseSegmentUtil {

	PhdInfo mockPhdInfo = createMock(PhdInfo.class);
	@Test
	public void oneReadCoversConsensusExactly(){
		AceContig contig = DefaultAceContig.createBuilder("contig", "ACGTACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGTACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), mockPhdInfo, 8)
							.build();
		
		List<AceBaseSegment> actual =BaseSegmentUtil.computeBestSegmentsFor(contig);
		List<AceBaseSegment> expected = Arrays.<AceBaseSegment>asList(
				new DefaultAceBaseSegment("read1", Range.of(0,7)));
		
		assertEquals(expected,actual);
	}
	@Test
	public void firstReadCoversConsensusExactlyBestSegmentShouldOnlyHaveFirstRead(){
		AceContig contig = DefaultAceContig.createBuilder("contig", "ACGTACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGTACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), mockPhdInfo, 8)
								.addRead("read2", new NucleotideSequenceBuilder("ACGTACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), mockPhdInfo, 8)
							.build();
		
		List<AceBaseSegment> actual =BaseSegmentUtil.computeBestSegmentsFor(contig);
		List<AceBaseSegment> expected = Arrays.<AceBaseSegment>asList(
				new DefaultAceBaseSegment("read1", Range.of(0,7)));
		
		assertEquals(expected,actual);
	}
	@Test
	public void twoReadsSpanConsensusExactlyShouldHave2BestSegments(){
		AceContig contig = DefaultAceContig.createBuilder("contig", "ACGTACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGTA").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,4), mockPhdInfo, 5)
								.addRead("read2", new NucleotideSequenceBuilder("ACGT").build(),
									4,
									Direction.FORWARD, 
									Range.of(0,3), mockPhdInfo, 4)
							.build();
		
		List<AceBaseSegment> actual =BaseSegmentUtil.computeBestSegmentsFor(contig);
		List<AceBaseSegment> expected = Arrays.<AceBaseSegment>asList(
				new DefaultAceBaseSegment("read1", Range.of(0,4)),
				new DefaultAceBaseSegment("read2", Range.of(5,7))
				);
		
		assertEquals(expected,actual);
	}
	
	@Test
	public void whenReadMismatchesShouldMoveToNextRead(){
		AceContig contig = DefaultAceContig.createBuilder("contig", "ACGTACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACG-ACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), mockPhdInfo, 8)
								.addRead("read2", new NucleotideSequenceBuilder("ACGTACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), mockPhdInfo, 8)
							.build();
		
		List<AceBaseSegment> actual =BaseSegmentUtil.computeBestSegmentsFor(contig);
		List<AceBaseSegment> expected = Arrays.<AceBaseSegment>asList(
				new DefaultAceBaseSegment("read1", Range.of(0,2)),
				new DefaultAceBaseSegment("read2", Range.of(3,7)));
		
		assertEquals(expected,actual);
	}
	@Test
	public void shouldRollOverBackToPreviousReadsThatStillCoverIfLastReadInSliceMismatches(){
		AceContig contig = DefaultAceContig.createBuilder("contig", "ACGTACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACG-ACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), mockPhdInfo, 8)
								.addRead("read2", new NucleotideSequenceBuilder("ACGTAC-T").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), mockPhdInfo, 8)
							.build();
		
		List<AceBaseSegment> actual =BaseSegmentUtil.computeBestSegmentsFor(contig);
		List<AceBaseSegment> expected = Arrays.<AceBaseSegment>asList(
				new DefaultAceBaseSegment("read1", Range.of(0,2)),
				new DefaultAceBaseSegment("read2", Range.of(3,5)),
				new DefaultAceBaseSegment("read1", Range.of(6,7)));
		
		assertEquals(expected,actual);
	}
	
	@Test(expected = NoReadMatchesConsensusException.class)
	public void noReadsMatchConsensusShouldThrowException(){
		AceContig contig = DefaultAceContig.createBuilder("contig", "ACGWACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGTACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), mockPhdInfo, 8)
								.addRead("read2", new NucleotideSequenceBuilder("ACGAACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), mockPhdInfo, 8)
							.build();
		
		BaseSegmentUtil.computeBestSegmentsFor(contig);
		
	}
}
