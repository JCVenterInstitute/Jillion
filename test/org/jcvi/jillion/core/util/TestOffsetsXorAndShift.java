package org.jcvi.jillion.core.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestOffsetsXorAndShift {


    private List<Integer> a, b;


    private List<Integer> expected;
    private List<Integer> expectedWithShift;
    private List<Integer> expectedExcludeWithShift;
    private List<Integer> expectedExcludeNoShift;

    @Parameterized.Parameters(name = "{0} xor {1}")
    public static List<Object[]> data(){

        return List.of(
                new Object[]{List.of(1,2,3), List.of(1,2,3), List.of(), List.of(), List.of(),List.of()},
                new Object[]{List.of(1), List.of(1,2,3), List.of(2,3), List.of(1,2), List.of(), List.of()},
                new Object[]{List.of(1,2), List.of(3), List.of(1,2,3), List.of(1,2,3),
                                                            List.of(1,2), List.of(1,2)},
                new Object[]{List.of(1,2), List.of(1,2,3), List.of(3), List.of(1),
                        List.of(), List.of()},

                new Object[]{List.of(1,3), List.of(1,2), List.of(2,3), List.of(1,2), List.of(3), List.of(2),
                                                                       },
                new Object[]{List.of(1,3), List.of(1,2,3), List.of(2), List.of(1), List.of(), List.of()}

        );
    }

    public TestOffsetsXorAndShift(List<Integer> a, List<Integer> b, List<Integer> expected,
                                  List<Integer> expectedWithShift,List<Integer> expectedExcludeNoShift,List<Integer> expectedExcludeWithShift) {
        this.a = a;
        this.expected = expected;
        this.b = b;
        this.expectedWithShift = expectedWithShift;
        this.expectedExcludeNoShift = expectedExcludeNoShift;
        this.expectedExcludeWithShift = expectedExcludeWithShift;
    }
    @Test
    public void xorDefaultToNoShift(){
        Offsets sut = Offsets.fromSortedList(a);

        Offsets other = Offsets.fromSortedList(b);

        Offsets and = sut.xor(other);

        assertEquals(expected, and.asList());

    }
    @Test
    public void xorNoShift(){
        Offsets sut = Offsets.fromSortedList(a);

        Offsets other = Offsets.fromSortedList(b);

        Offsets and = sut.xor(other, Offsets.XorOptions.builder()
                        .shift(false)
                .build());

        assertEquals(expected, and.asList());

    }
    @Test
    public void xorAndShift(){
        Offsets sut = Offsets.fromSortedList(a);

        Offsets other = Offsets.fromSortedList(b);

        Offsets and = sut.xor(other,Offsets.XorOptions.builder()
                .shift(true)
                .build());

        assertEquals(expectedWithShift, and.asList());

    }

    @Test
    public void xorExcludeWithShift(){
        Offsets sut = Offsets.fromSortedList(a);

        Offsets other = Offsets.fromSortedList(b);

        Offsets and = sut.xor(other, Offsets.XorOptions.builder()
                .shift(true)
                .include(false)
                .build());

        assertEquals(expectedExcludeWithShift, and.asList());

    }
    @Test
    public void xorExcludeNoShift(){
        Offsets sut = Offsets.fromSortedList(a);

        Offsets other = Offsets.fromSortedList(b);

        Offsets and = sut.xor(other, Offsets.XorOptions.builder()
                .shift(false)
                        .include(false)
                .build());

        assertEquals(expectedExcludeNoShift, and.asList());

    }

}
