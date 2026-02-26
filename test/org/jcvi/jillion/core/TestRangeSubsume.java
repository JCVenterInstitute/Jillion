package org.jcvi.jillion.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestRangeSubsume {


	private final List<Range> a,  expected;

	@Parameters(name = "{0}")
	public static List<Object[]> data(){
		return List.of(

				new Object[] {"empty lists", emptyList(), emptyList()},
				new Object[] {"union single range with empty list", List.of(Range.of(0, 1)),List.of(Range.of(0, 1))},
				new Object[] {"non overlapping", List.of(Range.of(0, 1), Range.of(4,5)), List.of(Range.of(0, 1), Range.of(4,5))},
				new Object[] {"many non overlapping lists of Ranges", List.of(Range.of(0, 1), Range.of(10,20) ,Range.of(4,5), Range.of(100,200)), List.of(Range.of(0, 1),Range.of(4,5),Range.of(10,20) , Range.of(100,200))},

				new Object[] {"same range multiple times should be merged into 1 object", List.of(Range.of(0, 1), Range.of(0,1)),List.of(Range.of(0,1))},
				new Object[] {"several same ranges multiple times", List.of(Range.of(0, 1), Range.of(4,5), Range.of(0, 1), Range.of(4,5)), List.of(Range.of(0, 1), Range.of(4,5))},

				new Object[] {"different order equal non-overlapping range should union to whole thing",List.of(Range.of(4,5), Range.of(0, 1), Range.of(0, 1), Range.of(4,5)), List.of(Range.of(0, 1), Range.of(4,5))},

				new Object[] {"range overlapping but not subrange should not subsume", List.of(Range.of(5, 10), Range.of(3, 6)), List.of(Range.of(3, 6), Range.of(5, 10) )},

				new Object[] {"sub range should subsume", List.of(Range.of(0, 10), Range.of(4,5)), List.of(Range.of(0,10))},
				new Object[] {"ribosomal slippage should NOT subsume", List.of(Range.of(13442,13468), Range.of(13468,16236)),List.of(Range.of(13442,13468), Range.of(13468,16236))}

				);
	}

	public TestRangeSubsume(String ignored, List<Range> a,  List<Range> expected) {
		this.a = a;
		this.expected = expected;
	}
	@Test
	public void subsume() {
		List<Range> actual = Ranges.subsume(a);
		assertEquals(expected, actual);
	}

	
}
