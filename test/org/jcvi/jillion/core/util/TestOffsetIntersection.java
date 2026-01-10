package org.jcvi.jillion.core.util;

import org.jcvi.jillion.core.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TestOffsetIntersection {

    @Parameterized.Parameters
    public static List<Object[]> data(){
        return List.of(
            new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7}), 2,6, Offsets.fromSortedArray(new int[]{2,3,4,5,6})},
            new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7}), 2,3, Offsets.fromSortedArray(new int[]{2,3})},

            new Object[]{Offsets.fromSortedArray(new int[]{1,2,5,6,7}), 2,6, Offsets.fromSortedArray(new int[]{2, 5,6})},
                new Object[]{Offsets.fromSortedArray(new int[]{1,5,6,7}), 2,6, Offsets.fromSortedArray(new int[]{5,6})},
                new Object[]{Offsets.fromSortedArray(new int[]{1,7}), 2,6, Offsets.fromSortedArray(new int[]{})},
        new Object[]{Offsets.fromSortedArray(new int[]{10,15,16}), 2,6, Offsets.fromSortedArray(new int[]{})},
                new Object[]{Offsets.fromSortedArray(new int[]{1,7}), 10,15, Offsets.fromSortedArray(new int[]{})}

                );
    }

    private final Offsets sut;
    private final Range range;
    private final Offsets expected;


    public TestOffsetIntersection( Offsets sut, int start, int end, Offsets expected){
        this.sut = sut;
        this.expected = expected;
        range = Range.of(start, end);
    }

    @Test
    public void test(){
        assertEquals(expected, sut.intersection(range));
    }


}
