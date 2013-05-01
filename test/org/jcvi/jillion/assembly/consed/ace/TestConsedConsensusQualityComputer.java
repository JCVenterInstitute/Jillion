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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.consed.ace.AceFileUtil;
import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
public class TestConsedConsensusQualityComputer {

	private Date phdDate = new Date(123456789);
	@Test
	public void oneXForwardCoverageShouldJustUseThatRead() throws DataStoreException{
		AceContig contig = new AceContigBuilder("contigId", "ACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read1", "read1.phd", phdDate), 4)
							.build();
		QualitySequence read1Qualities = new QualitySequenceBuilder(new byte[]{20,30,40,50}).build();
		QualitySequenceDataStore qualityDataStore = DataStoreUtil.adapt(QualitySequenceDataStore.class, Collections.singletonMap("read1",read1Qualities ));
		
		QualitySequence actualConsensusQualities = AceFileUtil.computeConsensusQualities(contig, qualityDataStore);
		
		assertEquals(read1Qualities, actualConsensusQualities);
	}
	
	@Test
	public void oneXReverseCoverageShouldJustUseThatRead() throws DataStoreException{
		AceContig contig = new AceContigBuilder("contigId", "ACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.REVERSE, Range.of(0,3), new PhdInfo("read1", "read1.phd", phdDate), 4)
							.build();
		QualitySequence read1Qualities = new QualitySequenceBuilder(new byte[]{20,30,40,50}).build();
		QualitySequenceDataStore qualityDataStore = DataStoreUtil.adapt(QualitySequenceDataStore.class, Collections.singletonMap("read1",read1Qualities ));
		
		QualitySequence actualConsensusQualities = AceFileUtil.computeConsensusQualities(contig, qualityDataStore);
		
		//since read is reversed, the qualities are reversed too
		QualitySequence expected = new QualitySequenceBuilder(read1Qualities)
										.reverse()
										.build();
		
		assertEquals(expected, actualConsensusQualities);
	}

	@Test
	public void oneXInEachDirectionShouldSumQualities() throws DataStoreException{
		AceContig contig = new AceContigBuilder("contigId", "ACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read1", "read1.phd", phdDate), 4)
							.addRead("read2", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.REVERSE, Range.of(0,3), new PhdInfo("read2", "read2.phd", phdDate), 4)
							
							.build();
		Map<String, QualitySequence> qualMap = new HashMap<String, QualitySequence>();
		qualMap.put("read1", new QualitySequenceBuilder(new byte[]{20,30,40,50}).build());
		qualMap.put("read2", new QualitySequenceBuilder(new byte[]{20,20,20,20}).build());

		QualitySequenceDataStore qualityDataStore = DataStoreUtil.adapt(QualitySequenceDataStore.class, qualMap);
		
		QualitySequence actualConsensusQualities = AceFileUtil.computeConsensusQualities(contig, qualityDataStore);
		QualitySequence expected = new QualitySequenceBuilder(new byte[]{40,50,60,70})
											.build();
		assertEquals(expected, actualConsensusQualities);
	}
	
	@Test
	public void consensusQualityShouldNotExceed90() throws DataStoreException{
		AceContig contig = new AceContigBuilder("contigId", "ACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read1", "read1.phd", phdDate), 4)
							.addRead("read2", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.REVERSE, Range.of(0,3), new PhdInfo("read2", "read2.phd", phdDate), 4)
							
							.build();
		Map<String, QualitySequence> qualMap = new HashMap<String, QualitySequence>();
		qualMap.put("read1", new QualitySequenceBuilder(new byte[]{20,30,40,50}).build());
		qualMap.put("read2", new QualitySequenceBuilder(new byte[]{50,50,50,50}).build());

		QualitySequenceDataStore qualityDataStore = DataStoreUtil.adapt(QualitySequenceDataStore.class, qualMap);
		
		QualitySequence actualConsensusQualities = AceFileUtil.computeConsensusQualities(contig, qualityDataStore);
		QualitySequence expected = new QualitySequenceBuilder(new byte[]{70,80,90,90})
											.build();
		assertEquals(expected, actualConsensusQualities);
	}
	
	@Test
	public void multipleXWithSameStartShouldNotGetBonus() throws DataStoreException{
		AceContigBuilder builder = new AceContigBuilder("contigId", "ACGT");
		Map<String, QualitySequence> qualMap = new HashMap<String, QualitySequence>();
		for(int i=0; i<50; i++){
			String id = "read"+i;
			builder.addRead(id, new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo(id, id+".phd", phdDate), 4);
			qualMap.put(id, new QualitySequenceBuilder(new byte[]{20,30,40,50}).build());
		}
		AceContig contig = builder.build();
		QualitySequenceDataStore qualityDataStore = DataStoreUtil.adapt(QualitySequenceDataStore.class, qualMap);
		
		QualitySequence actualConsensusQualities = AceFileUtil.computeConsensusQualities(contig, qualityDataStore);
		QualitySequence expected = new QualitySequenceBuilder(new byte[]{20,30,40,50})
											.build();
		assertEquals(expected, actualConsensusQualities);
	}
	
	@Test
	public void multipleReverseXWithSameStartShouldNotGetBonus() throws DataStoreException{
		AceContigBuilder builder = new AceContigBuilder("contigId", "ACGT");
		Map<String, QualitySequence> qualMap = new HashMap<String, QualitySequence>();
		for(int i=0; i<50; i++){
			String id = "read"+i;
			builder.addRead(id, new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.REVERSE, Range.of(0,3), new PhdInfo(id, id+".phd", phdDate), 4);
			qualMap.put(id, new QualitySequenceBuilder(new byte[]{20,30,40,50}).build());
		}
		AceContig contig = builder.build();
		QualitySequenceDataStore qualityDataStore = DataStoreUtil.adapt(QualitySequenceDataStore.class, qualMap);
		
		QualitySequence actualConsensusQualities = AceFileUtil.computeConsensusQualities(contig, qualityDataStore);
		
		QualitySequence expected = new QualitySequenceBuilder(new byte[]{20,30,40,50})
											.reverse()
											.build();
		assertEquals(expected, actualConsensusQualities);
	}
	
	@Test
	public void multipleXWithMultipleStartsGetsBonus() throws DataStoreException{
		AceContigBuilder builder = new AceContigBuilder("contigId", "ACGT");
		Map<String, QualitySequence> qualMap = new HashMap<String, QualitySequence>();
		
		builder.addRead("read1", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read1", "read1.phd", phdDate), 4);
		qualMap.put("read1", new QualitySequenceBuilder(new byte[]{20,30,40,50}).build());
		
		builder.addRead("read2", new NucleotideSequenceBuilder("GT").build(), 2, Direction.FORWARD, Range.of(0,1), new PhdInfo("read2", "read2.phd", phdDate), 2);
		qualMap.put("read2", new QualitySequenceBuilder(new byte[]{20,30}).build());
		
		
		AceContig contig = builder.build();
		QualitySequenceDataStore qualityDataStore = DataStoreUtil.adapt(QualitySequenceDataStore.class, qualMap);
		
		QualitySequence actualConsensusQualities = AceFileUtil.computeConsensusQualities(contig, qualityDataStore);
		QualitySequence expected = new QualitySequenceBuilder(new byte[]{20,30,45,55})
											.build();
		assertEquals(expected, actualConsensusQualities);
	}
	
	@Test
	public void multipleXInBothDirectionsOnlyGetsOneBonus() throws DataStoreException{
		AceContigBuilder builder = new AceContigBuilder("contigId", "ACGT");
		Map<String, QualitySequence> qualMap = new HashMap<String, QualitySequence>();
		
		builder.addRead("read1", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read1", "read1.phd", phdDate), 4);
		qualMap.put("read1", new QualitySequenceBuilder(new byte[]{20,30,40,50}).build());
		
		builder.addRead("read2", new NucleotideSequenceBuilder("GT").build(), 2, Direction.FORWARD, Range.of(0,1), new PhdInfo("read2", "read2.phd", phdDate), 2);
		qualMap.put("read2", new QualitySequenceBuilder(new byte[]{20,30}).build());
		
		builder.addRead("read3", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.REVERSE, Range.of(0,3), new PhdInfo("read3", "read3.phd", phdDate), 4);
		qualMap.put("read3", new QualitySequenceBuilder(new byte[]{20,30,40,50}).build());
		
		builder.addRead("read4", new NucleotideSequenceBuilder("GT").build(), 2, Direction.REVERSE, Range.of(0,1), new PhdInfo("read4", "read4.phd", phdDate), 2);
		qualMap.put("read4", new QualitySequenceBuilder(new byte[]{20,30}).build());
		
		
		AceContig contig = builder.build();
		QualitySequenceDataStore qualityDataStore = DataStoreUtil.adapt(QualitySequenceDataStore.class, qualMap);
		
		QualitySequence actualConsensusQualities = AceFileUtil.computeConsensusQualities(contig, qualityDataStore);
		QualitySequence expected = new QualitySequenceBuilder(new byte[]{70,70,75,75})
											.build();
		assertEquals(expected, actualConsensusQualities);
	}
	
	@Test
	public void TakeHighestQualityInEachDirection() throws DataStoreException{
		AceContig contig = new AceContigBuilder("contigId", "ACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read1", "read1.phd", phdDate), 4)
							.addRead("read2", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.REVERSE, Range.of(0,3), new PhdInfo("read2", "read2.phd", phdDate), 4)
							
							.addRead("read3", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read3", "read3.phd", phdDate), 4)
							.addRead("read4", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.REVERSE, Range.of(0,3), new PhdInfo("read4", "read4.phd", phdDate), 4)
							
							
							.build();
		Map<String, QualitySequence> qualMap = new HashMap<String, QualitySequence>();
		qualMap.put("read1", new QualitySequenceBuilder(new byte[]{20,30,40,50}).build());
		qualMap.put("read2", new QualitySequenceBuilder(new byte[]{20,20,20,20}).build());
		
		qualMap.put("read3", new QualitySequenceBuilder(new byte[]{10,10,10,10}).build());
		qualMap.put("read4", new QualitySequenceBuilder(new byte[]{10,10,10,10}).build());

		QualitySequenceDataStore qualityDataStore = DataStoreUtil.adapt(QualitySequenceDataStore.class, qualMap);
		
		QualitySequence actualConsensusQualities = AceFileUtil.computeConsensusQualities(contig, qualityDataStore);
		QualitySequence expected = new QualitySequenceBuilder(new byte[]{40,50,60,70})
											.build();
		assertEquals(expected, actualConsensusQualities);
	}
	
	@Test
	public void readsThatDontMatchWindowAreExcluded() throws DataStoreException{
		AceContig contig = new AceContigBuilder("contigId", "ACGT")
							.addRead("read1", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read1", "read1.phd", phdDate), 4)
							.addRead("read2", new NucleotideSequenceBuilder("CCTT").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read2", "read2.phd", phdDate), 4)
							.addRead("read3", new NucleotideSequenceBuilder("ACCG").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read3", "read3.phd", phdDate), 4)
							.addRead("read4", new NucleotideSequenceBuilder("ACTC").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read4", "read4.phd", phdDate), 4)
							
							
							
							.build();
		Map<String, QualitySequence> qualMap = new HashMap<String, QualitySequence>();
		qualMap.put("read1", new QualitySequenceBuilder(new byte[]{20,20,20,20}).build());
		qualMap.put("read2", new QualitySequenceBuilder(new byte[]{30,30,30,30}).build());
		
		qualMap.put("read3", new QualitySequenceBuilder(new byte[]{60,60,60,60}).build());
		qualMap.put("read4", new QualitySequenceBuilder(new byte[]{70,70,70,70}).build());
		
		QualitySequenceDataStore qualityDataStore = DataStoreUtil.adapt(QualitySequenceDataStore.class, qualMap);
		
		QualitySequence actualConsensusQualities = AceFileUtil.computeConsensusQualities(contig, qualityDataStore);
		
		assertEquals(qualMap.get("read1"), actualConsensusQualities);
	}
	
	@Test
	public void gapInConsensusExpandsWindow() throws DataStoreException{
		AceContig contig = new AceContigBuilder("contigId", "AC*GT")
							.addRead("read1", new NucleotideSequenceBuilder("AC*GT").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read1", "read1.phd", phdDate), 4)
							.addRead("read2", new NucleotideSequenceBuilder("CC*TT").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read2", "read2.phd", phdDate), 4)
							.addRead("read3", new NucleotideSequenceBuilder("AC*CG").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read3", "read3.phd", phdDate), 4)
							.addRead("read4", new NucleotideSequenceBuilder("AC*TC").build(), 0, Direction.FORWARD, Range.of(0,3), new PhdInfo("read4", "read4.phd", phdDate), 4)
							
							
							
							.build();
		Map<String, QualitySequence> qualMap = new HashMap<String, QualitySequence>();
		qualMap.put("read1", new QualitySequenceBuilder(new byte[]{20,20,20,20}).build());
		qualMap.put("read2", new QualitySequenceBuilder(new byte[]{30,30,30,30}).build());
		
		qualMap.put("read3", new QualitySequenceBuilder(new byte[]{60,60,60,60}).build());
		qualMap.put("read4", new QualitySequenceBuilder(new byte[]{70,70,70,70}).build());
		
		QualitySequenceDataStore qualityDataStore = DataStoreUtil.adapt(QualitySequenceDataStore.class, qualMap);
		
		QualitySequence actualConsensusQualities = AceFileUtil.computeConsensusQualities(contig, qualityDataStore);
		
		assertEquals(qualMap.get("read1"), actualConsensusQualities);
	}
}
