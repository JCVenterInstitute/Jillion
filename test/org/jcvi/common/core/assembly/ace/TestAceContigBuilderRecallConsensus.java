package org.jcvi.common.core.assembly.ace;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.util.slice.GapQualityValueStrategies;
import org.jcvi.common.core.assembly.util.slice.consensus.ConicConsensusCaller;
import org.jcvi.common.core.assembly.util.slice.consensus.MostFrequentBasecallConsensusCaller;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.qual.QualitySequenceDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
public class TestAceContigBuilderRecallConsensus {
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private PhdInfo phdInfo = new DefaultPhdInfo("traceName", "phdName", new Date());
	
	@Test
	public void recallingConsensusNowWithoutSettingConsensusCallerShouldThrowException(){
		DefaultAceContigBuilder sut = new DefaultAceContigBuilder("id","ACGTACGTACGT");
		
		exception.expect(IllegalStateException.class);
		sut.recallConsensusNow();
		
	}
	
	@Test
	public void recallConsensusNowWithoutQualityData(){
		DefaultAceContigBuilder sut = new DefaultAceContigBuilder("id","ACNT")
										.addRead("read1", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.FORWARD,  Range.ofLength(4), phdInfo, 4)
										.addRead("read2", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.REVERSE,  Range.ofLength(4), phdInfo, 4);
										;
										
		sut.recallConsensus(MostFrequentBasecallConsensusCaller.INSTANCE);
		sut.recallConsensusNow();
		
		assertEquals("ACGT", sut.getConsensusBuilder().toString());
	}
	@Test
	public void recallConsensusDuringBuildWithoutQualityData(){
		DefaultAceContigBuilder sut = new DefaultAceContigBuilder("id","ACNT")
										.addRead("read1", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.FORWARD,  Range.ofLength(4), phdInfo, 4)
										.addRead("read2", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.REVERSE,  Range.ofLength(4), phdInfo, 4);
										;
										
		sut.recallConsensus(MostFrequentBasecallConsensusCaller.INSTANCE);
		
		assertEquals("ACGT", sut.build().getConsensusSequence().toString());
	}
	
	@Test
	public void recallAmiguiousConsensusDuringBuildWithoutQualityData(){
		DefaultAceContigBuilder sut = new DefaultAceContigBuilder("id","ACNT")
										.addRead("read1", new NucleotideSequenceBuilder("ACAT").build(), 0, Direction.FORWARD,  Range.ofLength(4), phdInfo, 4)
										.addRead("read2", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.REVERSE,  Range.ofLength(4), phdInfo, 4);
										;
										
		sut.recallConsensus(new ConicConsensusCaller(PhredQuality.valueOf(30)));
		
		assertEquals("ACRT", sut.build().getConsensusSequence().toString());
	}
	
	@Test
	public void recallAmiguiousConsensusNowWithoutQualityData(){
		DefaultAceContigBuilder sut = new DefaultAceContigBuilder("id","ACNT")
										.addRead("read1", new NucleotideSequenceBuilder("ACAT").build(), 0, Direction.FORWARD,  Range.ofLength(4), phdInfo, 4)
										.addRead("read2", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.REVERSE,  Range.ofLength(4), phdInfo, 4);
										;
										
		sut.recallConsensus(new ConicConsensusCaller(PhredQuality.valueOf(30)));
		sut.recallConsensusNow();
		
		assertEquals("ACRT", sut.getConsensusBuilder().toString());
	}
	
	@Test
	public void recallConsensusUsingActualQualityData(){
		DefaultAceContigBuilder sut = new DefaultAceContigBuilder("id","ACNT")
								.addRead("read1", new NucleotideSequenceBuilder("ACAT").build(), 0, Direction.FORWARD, Range.ofLength(4), phdInfo, 4)
								.addRead("read2", new NucleotideSequenceBuilder("ACGT").build(), 0, Direction.REVERSE,  Range.ofLength(4), phdInfo, 4);
								;
		Map<String,QualitySequence> qualityMap = new HashMap<String, QualitySequence>();
		qualityMap.put("read1", new QualitySequenceBuilder(new byte[]{30,30,60,30}).build());
		//read2 has a lower quality
		qualityMap.put("read2", new QualitySequenceBuilder(new byte[]{15,20,10,25}).build());
		
		QualitySequenceDataStore qualities = MapDataStoreAdapter.adapt(QualitySequenceDataStore.class, qualityMap);
		
		sut.recallConsensus(new ConicConsensusCaller(PhredQuality.valueOf(30)), qualities, GapQualityValueStrategies.LOWEST_FLANKING);
		//should pick the A because read1 is higher quality
		assertEquals("ACAT", sut.build().getConsensusSequence().toString());
	}
}
