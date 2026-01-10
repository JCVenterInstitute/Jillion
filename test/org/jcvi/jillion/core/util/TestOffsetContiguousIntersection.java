package org.jcvi.jillion.core.util;

import org.jcvi.jillion.core.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestOffsetContiguousIntersection {

    @Parameterized.Parameters
    public static List<Object[]> data(){
        return List.of(
                new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7}), 8, null},
                new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7}), 0, null},
                new Object[]{Offsets.fromSortedArray(new int[]{1,6,7}), 5, null},
                new Object[]{Offsets.fromSortedArray(new int[]{1,5,7}), 5, Range.of(5)},

            new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7}), 1, Range.of(1,7)},
                new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7}), 2, Range.of(1,7)},
                new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7}), 3, Range.of(1,7)},
                new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7}), 4, Range.of(1,7)},
                new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7}), 5, Range.of(1,7)},
                new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7}), 6, Range.of(1,7)},
                new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7}), 7, Range.of(1,7)},

        new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7, 9,10,11}), 8, null},

                new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7, 9,10,11}), 9, Range.of(9,11)},
                new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7, 9,10,11}), 10, Range.of(9,11)},
                new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7, 9,10,11}), 11, Range.of(9,11)},
                new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7, 9,10,11}), 12, null}
                );
    }

    private final Offsets sut;
    private final Range expectedRange;
    private final int offset;


    public TestOffsetContiguousIntersection(Offsets sut, int offset, Range expectedRange){
        this.sut = sut;
        this.expectedRange = expectedRange;
        this.offset = offset;
    }

    @Test
    public void test(){
        assertEquals(expectedRange, sut.getContiguousRangeIntersecting(offset));
    }


}
