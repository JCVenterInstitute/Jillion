package org.jcvi.jillion.core.util;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.RangeCollectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TestRangeCollectorsInclusive {

    private final Range expected;

    private final List<Range> ranges;

    @Parameterized.Parameters
    public static List<Object[]> data(){
        return List.of(
                new Object[]{Range.of(0,10), new String[]{"0..10"}},
                new Object[]{Range.of(0,10), new String[]{"0..5", "6..10"}},
                new Object[]{Range.of(0,10), new String[]{"0..5", "9..10"}},
                new Object[]{Range.of(0,10), new String[]{"0..5", "4..10"}},
                new Object[]{Range.of(0,10), new String[]{"0..5", "4..10", "2,6"}}

        );

    }

    private static List<Range> toRanges(String... ranges){
        List<Range> list = new ArrayList<>(ranges.length);
        for(String r : ranges){
            list.add(Range.parseRange(r));
        }
        return list;
    }

    public TestRangeCollectorsInclusive(Range expected, String[] ranges) {
        this.expected = expected;
        this.ranges = toRanges(ranges);
    }

    @Test
    public void test(){
        assertEquals(expected, ranges.stream().collect(RangeCollectors.inclusiveRange()));
    }
}
