package org.jcvi.jillion.core.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TestOffsetsMergeAndShift {


    private List<Integer> a, b;


    private List<Integer> expected;
    private List<Integer> excludedExpected;

    @Parameterized.Parameters(name = "{0} + {1}")
    public static List<Object[]> data(){

        return List.of(
                new Object[]{List.of(1,2,3), List.of(1,2,3), List.of(1,2,3), List.of(1,2,3)},
                new Object[]{List.of(1), List.of(1,2,3), List.of(1,2,3), List.of(1)},
                new Object[]{List.of(1,2), List.of(3), List.of(1,2,3), List.of(1,2)},
                new Object[]{List.of(1,2), List.of(1,2,3), List.of(1,2,3), List.of(1,2)},
                //with shifts
                new Object[]{List.of(1,3), List.of(1,2), List.of(1,2,4), List.of(1,4)},
                new Object[]{List.of(1,3), List.of(1,2,3), List.of(1,2,4), List.of(1,4)},
                //consecutive shifts
                new Object[]{List.of(7), List.of(1,2,3), List.of(1,2,3,10), List.of(10)},
                new Object[]{List.of(7,10), List.of(1,2,3), List.of(1,2,3,10,13), List.of(10,13)},
                new Object[]{List.of(7,8,9,10), List.of(1,2,3), List.of(1,2,3,10,11,12,13), List.of(10,11,12,13)},
                new Object[]{List.of(7,8,9,10), List.of(1,2,3,7,8), List.of(1,2,3,10,11,12,13), List.of(10,11,12,13)},
                new Object[]{List.of(9,10), List.of(1,2,3,7,8), List.of(1,2,3,7,8,14,15), List.of(14,15)}
        );
    }

    public TestOffsetsMergeAndShift(List<Integer> a, List<Integer> b, List<Integer> expected, List<Integer> excludedExpected) {
        this.a = a;
        this.expected = expected;
        this.excludedExpected = excludedExpected;
        this.b = b;
    }


    @Test
    public void addAndShiftSameValuesAdd(){
        Offsets sut = Offsets.fromSortedList(a);

        Offsets other = Offsets.fromSortedList(b);

        Offsets and = sut.mergeAndShift(other);

        assertEquals(expected, and.asList());

    }
    @Test
    public void addAndShiftSameValuesAddExplicitIncluded(){
        Offsets sut = Offsets.fromSortedList(a);

        Offsets other = Offsets.fromSortedList(b);

        Offsets and = sut.mergeAndShift(other, true);

        assertEquals(expected, and.asList());

    }
    @Test
    public void addAndShiftSameValuesAddExcluded(){
        Offsets sut = Offsets.fromSortedList(a);

        Offsets other = Offsets.fromSortedList(b);

        Offsets and = sut.mergeAndShift(other, false);

        assertEquals(excludedExpected, and.asList());

    }
}
