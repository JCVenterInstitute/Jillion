package org.jcvi.jillion.core.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.jcvi.jillion.core.util.GenomeStatistics.GenomeStatisticsBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestN75Computations {

	
	@Parameters
	public static List<Object[]> params(){
		return Arrays.asList(
					new Object[]{ 40, new int[]{80,70,50,40,30,20}},
					new Object[]{ 40, new int[]{80,70,50,40,30,20, 10, 5}}
				
				);
	}
	
	
	private final double expectedAnswer;
	private final int[] values;
	
	
	public TestN75Computations(double expectedAnswer, int[] values) {
		this.expectedAnswer = expectedAnswer;
		this.values = values;
	}
	
	
	
	
	@Test
	public void intBuilder(){
		GenomeStatisticsBuilder builder = GenomeStatistics.n75Builder();
		for(int i : values){
			builder.add(i);
		}
		
		assertEquals(expectedAnswer, builder.build().getAsInt(), 0.01D);
	}
	
	@Test
	public void longBuilder(){
		GenomeStatisticsBuilder builder = GenomeStatistics.n75Builder();
		for(int i : values){
			builder.add( (long) i);
		}
		
		assertEquals(expectedAnswer, builder.build().getAsInt(), 0.01D);
	}
	
	@Test
	public void intStream(){
		IntStream stream = IntStream.of(values);
		
		assertEquals(expectedAnswer, GenomeStatistics.n75(stream).getAsInt(), 0.01D);
	}
	
	@Test
	public void intXStream(){
		IntStream stream = IntStream.of(values);
		
		assertEquals(expectedAnswer, GenomeStatistics.nX(stream, .75D).getAsInt(), 0.01D);
	}
	
	@Test
	public void parallelIntStream(){
		IntStream stream = IntStream.of(values).parallel();
		
		assertEquals(expectedAnswer, GenomeStatistics.n75(stream).getAsInt(), 0.01D);
	}
	
	@Test
	public void longStream(){
		LongStream stream = IntStream.of(values).asLongStream();
		
		assertEquals(expectedAnswer, GenomeStatistics.n75(stream).getAsInt(), 0.01D);
	}
	
	@Test
	public void longXStream(){
		LongStream stream = IntStream.of(values).asLongStream();
		
		assertEquals(expectedAnswer, GenomeStatistics.nX(stream, .75D).getAsInt(), 0.01D);
	}
	
	@Test
	public void IntegerStream(){
		double actual = IntStream.of(values)
									.mapToObj(Integer::valueOf)
									.collect(GenomeStatistics.n75Collector())
									.getAsInt();
		
		assertEquals(expectedAnswer, actual, 0.01D);
	}
	
	@Test
	public void LongStream(){
		double actual = IntStream.of(values)
									.mapToObj(Long::valueOf)
									.collect(GenomeStatistics.n75Collector())
									.getAsInt();
		
		assertEquals(expectedAnswer, actual, 0.01D);
	}
	
	@Test
	public void XStream(){
		double actual = IntStream.of(values)
									.mapToObj(Long::valueOf)
									.collect(GenomeStatistics.nXCollector(.75D))
									.getAsInt();
		
		assertEquals(expectedAnswer, actual, 0.01D);
	}
	
}
