package org.jcvi.jillion.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TestRangeComplement {
	
	
	private final List<Range> a, b, expected;
	
	@Parameters(name = "{0}")
	public static List<Object[]> data(){
		return List.of(
				
				new Object[] {"empty lists", emptyList(), emptyList(), emptyList()},
				new Object[] {"union single range with empty list", List.of(Range.of(0, 1)), emptyList(), List.of(Range.of(0, 1))},
				new Object[] {"union list with empty list", List.of(Range.of(0, 1), Range.of(4,5)), emptyList(), List.of(Range.of(0, 1), Range.of(4,5))},
				new Object[] {"non overlapping single Ranges", List.of(Range.of(0, 1)), List.of(Range.of(4,5)), List.of(Range.of(0, 1))},
				new Object[] {"non overlapping lists of Ranges", List.of(Range.of(0, 1), Range.of(10,20)), 
						                                         List.of(Range.of(4,5), Range.of(100,200)), List.of(Range.of(0, 1), Range.of(10,20))},
				
				new Object[] {"equal single range should complement to empty list", List.of(Range.of(0, 1)), List.of(Range.of(0,1)),emptyList()},
				new Object[] {"equal non-overlapping range should complement to empty", List.of(Range.of(0, 1), Range.of(4,5)), List.of(Range.of(0, 1), Range.of(4,5)), emptyList()},
				//
				new Object[] {"different order equal non-overlapping range should complement to empty", List.of(Range.of(4,5), Range.of(0, 1)), List.of(Range.of(0, 1), Range.of(4,5)), emptyList()},
				new Object[] {"overlapping ranges on same side", List.of(Range.of(0, 10), Range.of(0, 1)), List.of(Range.of(4,5)), List.of(Range.of(0,3), Range.of(6,10))},
				new Object[] {"different but overlapping ranges should return complement", List.of(Range.of(0, 10)), List.of(Range.of(4,5)), List.of(Range.of(0,3), Range.of(6,10))},
				new Object[] {"one side spans gap of other should return just the complement of intersection", List.of(Range.of(0, 10), Range.of(20, 30)),
																														List.of(Range.of(8, 15), Range.of(18, 24)),
																														List.of(Range.of(0,7), Range.of(25,30))},
				
				new Object[] {"lots of non-overlapping ranges should return all of a", List.of(Range.of(0, 10), Range.of(20, 30)),
																					List.of(Range.of(12, 15), Range.of(18, 19)),
																					List.of(Range.of(0, 10), Range.of(20, 30))},
				new Object[] {"b spans all of a should return empty", List.of(Range.of(0, 10), Range.of(20, 30)),
																		List.of(Range.of(-10, 15), Range.of(18, 50)),
																		emptyList()},
				
				//real world examples
				/*
				 * ranges to ignore : [[ 157 .. 305 ]/0B]
gisaidMutationRanges : [[ 88 .. 88 ]/0B, [ 160 .. 165 ]/0B, [ 184 .. 185 ]/0B]
				 */
				new Object[] {"one range outside of overlap", List.of(Range.of(88)),
						List.of(Range.of(158, 305)),
						List.of(Range.of(88))},
				
				new Object[] {"one range outside of overlap the rest inside", List.of(Range.of(88), Range.of(158, 305)),
						List.of(Range.of(158, 305)),
						List.of(Range.of(88))},
				new Object[] {"one range outside of overlap the rest inside sub range", List.of(Range.of(88), Range.of(200, 250)),
						List.of(Range.of(158, 305)),
						List.of(Range.of(88))}
				);
	}
	
	public TestRangeComplement(String ignored, List<Range> a, List<Range> b, List<Range> expected) {
		this.a = a;
		this.b = b;
		this.expected = expected;
	}
	@Test
	public void ab() {
		List<Range> actual = Ranges.complement(a, b);
		assertEquals(expected, actual);
	}

	
}
