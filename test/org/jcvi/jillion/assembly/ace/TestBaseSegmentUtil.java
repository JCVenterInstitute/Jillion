/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.ace;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jcvi.jillion.assembly.ace.AceBaseSegment;
import org.jcvi.jillion.assembly.ace.AceContig;
import org.jcvi.jillion.assembly.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.ace.BaseSegmentUtil;
import org.jcvi.jillion.assembly.ace.DefaultAceBaseSegment;
import org.jcvi.jillion.assembly.ace.PhdInfo;
import org.jcvi.jillion.assembly.ace.BaseSegmentUtil.NoReadMatchesConsensusException;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
public class TestBaseSegmentUtil {

	PhdInfo phdInfo = new PhdInfo("traceName","phdName",new Date());
	@Test
	public void oneReadCoversConsensusExactly(){
		AceContig contig = new AceContigBuilder("contig", "ACGTACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGTACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), phdInfo, 8)
							.build();
		
		List<AceBaseSegment> actual =BaseSegmentUtil.computeBestSegmentsFor(contig);
		List<AceBaseSegment> expected = Arrays.<AceBaseSegment>asList(
				new DefaultAceBaseSegment("read1", Range.of(0,7)));
		
		assertEquals(expected,actual);
	}
	@Test
	public void firstReadCoversConsensusExactlyBestSegmentShouldOnlyHaveFirstRead(){
		AceContig contig = new AceContigBuilder("contig", "ACGTACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGTACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), phdInfo, 8)
								.addRead("read2", new NucleotideSequenceBuilder("ACGTACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), phdInfo, 8)
							.build();
		
		List<AceBaseSegment> actual =BaseSegmentUtil.computeBestSegmentsFor(contig);
		List<AceBaseSegment> expected = Arrays.<AceBaseSegment>asList(
				new DefaultAceBaseSegment("read1", Range.of(0,7)));
		
		assertEquals(expected,actual);
	}
	@Test
	public void twoReadsSpanConsensusExactlyShouldHave2BestSegments(){
		AceContig contig = new AceContigBuilder("contig", "ACGTACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGTA").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,4), phdInfo, 5)
								.addRead("read2", new NucleotideSequenceBuilder("ACGT").build(),
									4,
									Direction.FORWARD, 
									Range.of(0,3), phdInfo, 4)
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
		AceContig contig = new AceContigBuilder("contig", "ACGTACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACG-ACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), phdInfo, 8)
								.addRead("read2", new NucleotideSequenceBuilder("ACGTACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), phdInfo, 8)
							.build();
		
		List<AceBaseSegment> actual =BaseSegmentUtil.computeBestSegmentsFor(contig);
		List<AceBaseSegment> expected = Arrays.<AceBaseSegment>asList(
				new DefaultAceBaseSegment("read1", Range.of(0,2)),
				new DefaultAceBaseSegment("read2", Range.of(3,7)));
		
		assertEquals(expected,actual);
	}
	@Test
	public void shouldRollOverBackToPreviousReadsThatStillCoverIfLastReadInSliceMismatches(){
		AceContig contig = new AceContigBuilder("contig", "ACGTACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACG-ACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), phdInfo, 8)
								.addRead("read2", new NucleotideSequenceBuilder("ACGTAC-T").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), phdInfo, 8)
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
		AceContig contig = new AceContigBuilder("contig", "ACGWACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGTACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), phdInfo, 8)
								.addRead("read2", new NucleotideSequenceBuilder("ACGAACGT").build(),
									0,
									Direction.FORWARD, 
									Range.of(0,7), phdInfo, 8)
							.build();
		
		BaseSegmentUtil.computeBestSegmentsFor(contig);
		
	}
}
