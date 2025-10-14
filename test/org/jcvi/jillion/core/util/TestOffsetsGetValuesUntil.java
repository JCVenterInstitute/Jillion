package org.jcvi.jillion.core.util;

import org.jcvi.jillion.internal.core.util.ArrayUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TestOffsetsGetValuesUntil {

    @Parameterized.Parameters(name = "{0} until value {1}")
    public static List<Object[]> data(){
        return List.of(
                new Object[]{ArrayUtil.asList(), 0,0},
                new Object[]{ArrayUtil.asList(), 1,0},
                new Object[]{ArrayUtil.asList(0), 0,0},
                new Object[]{ArrayUtil.asList(0), 1,1},
                new Object[]{ArrayUtil.asList(0,1), 1,1},
                new Object[]{ArrayUtil.asList(0,1), 2,2},
                new Object[]{ArrayUtil.asList(0,1,5), 2,2},
                new Object[]{ArrayUtil.asList(0,1,5), 7,3}
        );
    }

    private final Offsets offsets;
    private final int offset;
    private final int expected;

    public TestOffsetsGetValuesUntil(IntList intList, int offset, int expected){
        this.offsets = Offsets.fromSortedList(intList);
        this.offset = offset;
        this.expected = expected;
    }

    @Test
    public void test(){
        assertEquals(expected, offsets.getNumberOfValuesUntil(offset));
    }
}
