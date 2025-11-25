package org.jcvi.jillion.core.util;

import org.jcvi.jillion.core.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestOffsetComplement {

    @Parameterized.Parameters
    public static List<Object[]> data(){
        return List.of(
            new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7}), 2,6, Offsets.fromSortedArray(new int[]{1,7})},
            new Object[]{Offsets.fromSortedArray(new int[]{1,2,3,4,5,6,7}), 2,3, Offsets.fromSortedArray(new int[]{1,4,5,6,7})},

            new Object[]{Offsets.fromSortedArray(new int[]{1,2,5,6,7}), 2,6, Offsets.fromSortedArray(new int[]{1,7})},
                new Object[]{Offsets.fromSortedArray(new int[]{1,5,6,7}), 2,6, Offsets.fromSortedArray(new int[]{1,7})},
                new Object[]{Offsets.fromSortedArray(new int[]{1,7}), 2,6, Offsets.fromSortedArray(new int[]{1,7})},
        new Object[]{Offsets.fromSortedArray(new int[]{10,15,16}), 2,6, Offsets.fromSortedArray(new int[]{10,15,16})},
                new Object[]{Offsets.fromSortedArray(new int[]{1,7}), 10,15, Offsets.fromSortedArray(new int[]{1,7})},
                new Object[]{Offsets.fromSortedArray(new int[]{1,7}), 5,15, Offsets.fromSortedArray(new int[]{1})}

                );
    }

    private final Offsets sut;
    private final Range range;
    private final Offsets expected;


    public TestOffsetComplement(Offsets sut, int start, int end, Offsets expected){
        this.sut = sut;
        this.expected = expected;
        range = Range.of(start, end);
    }

    @Test
    public void test(){
        assertEquals(expected, sut.complement(range));
    }


}
