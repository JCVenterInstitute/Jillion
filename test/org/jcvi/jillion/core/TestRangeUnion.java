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
public class TestRangeUnion {
	
	
	private final List<Range> a, b, expected;
	
	@Parameters(name = "{0}")
	public static List<Object[]> data(){
		return List.of(
				
				new Object[] {"empty lists", emptyList(), emptyList(), emptyList()},
				new Object[] {"union single range with empty list", List.of(Range.of(0, 1)), emptyList(), emptyList()},
				new Object[] {"union list with empty list", List.of(Range.of(0, 1), Range.of(4,5)), emptyList(), emptyList()},
				new Object[] {"non overlapping single Ranges", List.of(Range.of(0, 1)), List.of(Range.of(4,5)), emptyList()},
				new Object[] {"non overlapping lists of Ranges", List.of(Range.of(0, 1), Range.of(10,20)), 
						                                         List.of(Range.of(4,5), Range.of(100,200)), emptyList()},
				
				new Object[] {"equal single range should union to whole thing", List.of(Range.of(0, 1)), List.of(Range.of(0,1)),List.of(Range.of(0,1))},
				new Object[] {"equal non-overlapping range should union to whole thing", List.of(Range.of(0, 1), Range.of(4,5)), List.of(Range.of(0, 1), Range.of(4,5)), List.of(Range.of(0, 1), Range.of(4,5))},
				
				new Object[] {"different order equal non-overlapping range should union to whole thing", List.of(Range.of(4,5), Range.of(0, 1)), List.of(Range.of(0, 1), Range.of(4,5)), List.of(Range.of(0, 1), Range.of(4,5))},
				new Object[] {"overlapping ranges on same side", List.of(Range.of(0, 10), Range.of(0, 1)), List.of(Range.of(4,5)), List.of(Range.of(4,5))},
				new Object[] {"different but overlapping ranges should return intersection", List.of(Range.of(0, 10)), List.of(Range.of(4,5)), List.of(Range.of(4,5))},
				new Object[] {"one side spans gap of other should return just the intersection overlapping the span", List.of(Range.of(0, 10), Range.of(20, 30)),
																														List.of(Range.of(8, 15), Range.of(18, 24)),
																														List.of(Range.of(8,10), Range.of(20,24))}
				
				);
	}
	
	public TestRangeUnion(String ignored, List<Range> a, List<Range> b, List<Range> expected) {
		this.a = a;
		this.b = b;
		this.expected = expected;
	}
	@Test
	public void ab() {
		List<Range> actual = Ranges.union(a, b);
		assertEquals(expected, actual);
	}
	@Test
	public void ba() {
		List<Range> actual = Ranges.union( b, a);
		assertEquals(expected, actual);
	}
	
}
