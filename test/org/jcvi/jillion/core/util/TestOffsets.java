package org.jcvi.jillion.core.util;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.util.ArrayUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class TestOffsets {

    @Test
    public void fromSortedList(){
        Offsets sut = Offsets.fromSortedList(List.of(1,2,3,4,5));

        assertArrayEquals(new int[]{1,2,3,4,5}, sut.stream().toArray());

        assertEquals(List.of(1,2,3,4,5), sut.asList());

        List<Integer> actualforEachList = new ArrayList<>();
        sut.forEach(actualforEachList::add);
        assertEquals(List.of(1,2,3,4,5), actualforEachList);

    }

    @Test
    public void or(){
        Offsets sut = Offsets.fromSortedList(List.of(1,2,3,4,5));

        Offsets other = Offsets.fromSortedList(List.of(3,4,5,6,7,8,9));

        Offsets and = sut.or(other);

        assertArrayEquals(new int[]{1,2,3,4,5,6,7,8,9}, and.stream().toArray());



    }

    @Test
    public void and(){
        Offsets sut = Offsets.fromSortedList(List.of(1,2,3,4,5));

        Offsets other = Offsets.fromSortedList(List.of(3,4,5,6,7,8,9));

        Offsets and = sut.and(other);

        assertArrayEquals(new int[]{3,4,5}, and.stream().toArray());

    }



    @Test
    public void xor(){
        Offsets sut = Offsets.fromSortedList(List.of(1,2,3,4,5));

        Offsets other = Offsets.fromSortedList(List.of(3,4,5,6,7,8,9));

        Offsets and = sut.xor(other);

        assertArrayEquals(new int[]{1,2,6,7,8,9}, and.stream().toArray());



    }

    @Test
    public void fromUnSortedList(){
        Offsets sut = Offsets.fromUnsortedList(List.of(5,4,3,2,1));

        assertArrayEquals(new int[]{1,2,3,4,5}, sut.stream().toArray());

        assertEquals(List.of(1,2,3,4,5), sut.asList());
        List<Integer> actualforEachList = new ArrayList<>();
        sut.forEach(actualforEachList::add);
        assertEquals(List.of(1,2,3,4,5), actualforEachList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsortedListSentToSortedWillThrowException(){
        Offsets.fromSortedList(List.of(5,4,3,2,1));

    }

    @Test
    public void fromSingleRange(){
        Offsets sut = Offsets.fromRanges(List.of(Range.of(1,5)));

        assertArrayEquals(new int[]{1,2,3,4,5}, sut.stream().toArray());
        assertEquals(List.of(1,2,3,4,5), sut.asList());
    }

    @Test
    public void fromMultipleOverlappingRanges(){
        Offsets sut = Offsets.fromRanges(List.of(Range.of(1,3), Range.of(3,5)));

        assertArrayEquals(new int[]{1,2,3,4,5}, sut.stream().toArray());
    }

    @Test
    public void contains(){
        Offsets sut = Offsets.fromRanges(List.of(Range.of(1,5)));

        assertTrue(sut.contains(5));
        assertFalse(sut.contains(6));
        assertFalse(sut.contains(0));

    }

    @Test
    public void nonContiguousOffsets(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        assertTrue(sut.contains(1));
        assertFalse(sut.contains(2));
        assertFalse(sut.contains(3));
        assertTrue(sut.contains(4));

        assertEquals(4, sut.size());
    }

    @Test
    public void removeNoShift(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.remove(4);

        assertArrayEquals(new int[]{1,7,10}, sut.stream().toArray());
    }
    @Test
    public void removeDoesNotContainValueDoesNothing(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.remove(5);

        assertArrayEquals(new int[]{1,4,7,10}, sut.stream().toArray());
    }

    @Test
    public void removeWithShift(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.removeAndShift(4);

        assertArrayEquals(new int[]{1,6,9}, sut.stream().toArray());
    }
    @Test
    public void andAndShiftAlreadyExistsDoesNothing(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.addAndShift(4);

        assertArrayEquals(new int[]{1,4,7,10}, sut.stream().toArray());
    }

    @Test
    public void andAlreadyExistsDoesNothing(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.add(4);

        assertArrayEquals(new int[]{1,4,7,10}, sut.stream().toArray());
    }
    @Test
    public void addNoShift(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.add(5);

        assertArrayEquals(new int[]{1,4, 5,7,10}, sut.stream().toArray());
    }
    @Test
    public void shiftNoAdd(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.replaceIf(i->i>6, i-> i-1);

        assertArrayEquals(new int[]{1,4,6,9}, sut.stream().toArray());
    }

    @Test
    public void shiftNoAddCausesDuplicateRemovesDuplicates(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.replaceIf(i->i>4, i-> 4);

        assertArrayEquals(new int[]{1,4}, sut.stream().toArray());
    }

    @Test
    public void shiftNoAddCausesDuplicateRemovesDuplicates2(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.replaceIf(i->i>4, i-> i-3);

        assertArrayEquals(new int[]{1,4,7}, sut.stream().toArray());
    }

    @Test
    public void addAndShift(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.addAndShift(5);

        assertArrayEquals(new int[]{1,4, 5,8,11}, sut.stream().toArray());
    }

    @Test
    public void removeWithShiftDoesNotContainValueDoesNothing(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.removeAllAndShift(List.of(5,8));

        assertArrayEquals(new int[]{1,4,7,10}, sut.stream().toArray());
    }

    @Test
    public void removeMultipleWithShift(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.removeAllAndShift(List.of(4,7));

        assertArrayEquals(new int[]{1,8}, sut.stream().toArray());

        assertEquals(2, sut.size());
    }

    @Test
    public void removeMultipleWithShiftListOutOfOrderShouldStillWork(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.removeAllAndShift(List.of(7,4));

        assertArrayEquals(new int[]{1,8}, sut.stream().toArray());

        assertEquals(2, sut.size());
    }

    @Test
    public void removeMultipleWithShiftSomePresentSomeNotOnlyRemovePresent(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.removeAllAndShift(List.of(4,6,7));

        assertArrayEquals(new int[]{1,8}, sut.stream().toArray());

        assertEquals(2, sut.size());
        assertFalse(sut.isEmpty());
    }

    @Test
    public void clear(){
        Offsets sut  = Offsets.fromSortedList(List.of(1,4,7,10));
        sut.clear();

        assertEquals(0, sut.size());
        assertTrue(sut.isEmpty());
       assertArrayEquals(new int[]{}, sut.toArray());
    }

    @Test
    public void ungap(){
        Offsets sut  = Offsets.fromSortedList(List.of(1,4,7,10));
        Offsets gaps = Offsets.fromSortedList(List.of(5));

        sut.ungap(gaps);


        assertArrayEquals(new int[]{1,4,6,9}, sut.toArray());
    }

    @Test
    public void ungapEmptyGaps(){
        Offsets sut  = Offsets.fromSortedList(List.of(1,4,7,10));


        sut.ungap(Offsets.withInitialCapacity(10));


        assertArrayEquals(new int[]{1,4,7,10}, sut.toArray());
    }
    @Test
    public void ungapOnlyDownstreamGaps(){
        Offsets sut  = Offsets.fromSortedList(List.of(1,4,7,10));
        Offsets gaps = Offsets.fromSortedList(List.of(11,12));


        sut.ungap(gaps);


        assertArrayEquals(new int[]{1,4,7,10}, sut.toArray());
    }
    @Test
    public void allUpstreamGaps(){
        Offsets sut  = Offsets.fromSortedList(List.of(1,4,7,10));
        Offsets gaps = Offsets.fromSortedList(List.of(0));


        sut.ungap(gaps);


        assertArrayEquals(new int[]{0,3,6,9}, sut.toArray());
    }

    @Test
    public void insertAndShift(){
        Offsets sut  = Offsets.fromSortedList(List.of(1,4,7,10));
        Offsets insertion = Offsets.fromSortedList(ArrayUtil.asList(0,1));

        sut.insertAndShift(insertion, 4, 5);

        assertArrayEquals(new int[]{1,4,5,6,11,14}, sut.toArray());
    }



}
