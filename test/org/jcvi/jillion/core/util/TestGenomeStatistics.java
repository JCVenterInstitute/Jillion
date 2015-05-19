package org.jcvi.jillion.core.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.LongStream;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;
import org.junit.Test;
public class TestGenomeStatistics {

	@Test
	public void n50(){
		assertEquals(70, GenomeStatistics.n50Builder()
						.add(80)
						.add(70)
						.add(50)
						.add(40)
						.add(30)
						.add(20)
						.build().getAsDouble(), .01D);
	}
	
	@Test
	public void anotherN50(){
		assertEquals(50, GenomeStatistics.n50Builder()
						.add(80)
						.add(70)
						.add(50)
						.add(40)
						.add(30)
						.add(20)
						.add(10)
						.add(5)
						.build().getAsDouble(), .01D);
	}
	
	
	@Test
	public void n50collector(){
		
		List<NucleotideSequence> seqs = Arrays.asList(NucleotideSequenceTestUtil.createRandom(80),
				NucleotideSequenceTestUtil.createRandom(70),
				NucleotideSequenceTestUtil.createRandom(50),
				NucleotideSequenceTestUtil.createRandom(40),
				NucleotideSequenceTestUtil.createRandom(30),
				NucleotideSequenceTestUtil.createRandom(20));
		
		
		
		LongStream stream = seqs.stream()
							.mapToLong(NucleotideSequence::getLength);
		
		OptionalDouble result = GenomeStatistics.nX(stream, .5D);
		
		
					
		assertEquals(70, result.getAsDouble(), .01D);
	}
	
	
	@Test
	public void ng50collector(){
		
		List<NucleotideSequence> seqs = Arrays.asList(NucleotideSequenceTestUtil.createRandom(80),
				NucleotideSequenceTestUtil.createRandom(70),
				NucleotideSequenceTestUtil.createRandom(50),
				NucleotideSequenceTestUtil.createRandom(40),
				NucleotideSequenceTestUtil.createRandom(30),
				NucleotideSequenceTestUtil.createRandom(20));
		
		
		
		
		OptionalDouble result = GenomeStatistics.ng50(seqs.stream().mapToLong(NucleotideSequence::getLength)
									, 300);
					
		OptionalDouble result2 = seqs.stream()
										.map(NucleotideSequence::getLength)
										.collect(GenomeStatistics.ng50Collector(300));
		assertEquals(70, result2.getAsDouble(), .01D);
		assertEquals(70, result.getAsDouble(), .01D);
	}
	
	
}
