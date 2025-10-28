package org.jcvi.jillion.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.core.util.ArrayUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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
    public void equalsAndHashCodeSameRef(){
        Offsets sut = Offsets.fromSortedList(List.of(1,2,3,4,5));
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void equalsAndHashCodeSameValues(){
        Offsets a = Offsets.fromSortedList(List.of(1,2,3,4,5));
        Offsets b = Offsets.fromSortedList(List.of(1,2,3,4,5));
        TestUtil.assertEqualAndHashcodeSame(a, b);
    }
    @Test
    public void equalsAndHashCodeSameValuesDifferentCapacity(){
        Offsets a = Offsets.fromSortedList(List.of(1,2,3,4,5));
        Offsets b = Offsets.fromSortedList(List.of(1,2,3,4,5,6,7,8));

        b.remove(8);
        b.remove(7);
        b.remove(6);
        TestUtil.assertEqualAndHashcodeSame(a, b);
    }
    @Test
    public void equalsAndHashCodeDifferentValues(){
        Offsets a = Offsets.fromSortedList(List.of(1,2,3,4,5));
        Offsets b = Offsets.fromSortedList(List.of(1,2,3,4,5,6,7,8));

        TestUtil.assertNotEqualAndHashcodeDifferent(a, b);
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
    public void shift(){
        Offsets sut = Offsets.fromSortedList(List.of(1,2,3,4,5));

        sut.shift(10);

        assertArrayEquals(new int[]{11,12,13,14,15}, sut.toArray());

    }
    @Test
    public void shift0(){
        Offsets sut = Offsets.fromSortedList(List.of(1,2,3,4,5));

        sut.shift(0);

        assertArrayEquals(new int[]{1,2,3,4,5}, sut.toArray());

    }
    @Test
    public void shiftNegative(){
        Offsets sut = Offsets.fromSortedList(List.of(1,2,3,4,5));

        sut.shift(-5);

        assertArrayEquals(new int[]{-4,-3,-2,-1,0}, sut.toArray());

    }


    @Test
    public void xor(){
        Offsets sut = Offsets.fromSortedList(List.of(1,2,3,4,5));

        Offsets other = Offsets.fromSortedList(List.of(3,4,5,6,7,8,9));

        Offsets xor = sut.xor(other);

        assertArrayEquals(new int[]{1,2,6,7,8,9}, xor.toArray());

    }

    @Test
    public void computeGapOffsetsBeyondSeqLengthShouldBeIgnoredPreShifted(){
        Offsets sut = Offsets.fromSortedArray(new int[]{4,6, 99});
        assertEquals("ACGT-A-CGT", sut.computeGaps(NucleotideSequence.of("ACGTACGT"), true).toString());
    }

    @Test
    public void computeGapOffsetsPreShifted(){
        Offsets sut = Offsets.fromSortedArray(new int[]{4,6});
        assertEquals("ACGT-A-CGT", sut.computeGaps(NucleotideSequence.of("ACGTACGT"), true).toString());
    }
    @Test
    public void computeGapOffsetsNotPreShifted(){
        Offsets sut = Offsets.fromSortedArray(new int[]{4,6});
        assertEquals("ACGT-AC-GT", sut.computeGaps(NucleotideSequence.of("ACGTACGT"), false).toString());
    }
    @Test
    public void computeGapOffsetsNotPreShiftedBeyondSeqLengthShouldBeIgnored(){
        Offsets sut = Offsets.fromSortedArray(new int[]{4,6,99});
        assertEquals("ACGT-AC-GT", sut.computeGaps(NucleotideSequence.of("ACGTACGT"), false).toString());
    }

    @Test
    public void unique(){
        Offsets sut = Offsets.fromSortedList(List.of(1,2,3,4,5));

        Offsets other = Offsets.fromSortedList(List.of(3,4,5,6,7,8,9));

        Offsets.UniqueOffsets unique = Offsets.unique(sut, other);


        assertArrayEquals(new int[]{1,2}, unique.getA().toArray());
        assertArrayEquals(new int[]{6,7,8,9}, unique.getB().toArray());

        Offsets.UniqueOffsets unique2 = Offsets.unique(other, sut);


        assertArrayEquals(new int[]{1,2}, unique2.getB().toArray());
        assertArrayEquals(new int[]{6,7,8,9}, unique2.getA().toArray());

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
    public void addAndShiftMultiIndividualCalls(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.addAndShift(5);
        sut.addAndShift(3);

        assertArrayEquals(new int[]{1,3,5, 6,9,12}, sut.stream().toArray());
    }
    @Test
    public void addAndShiftAsOtherOffset(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.add(Offsets.fromSortedArray(new int[]{3,5}), Offsets.AddOptions.builder()
                .shift(true)
                .include(true)
                .build());

        assertArrayEquals(new int[]{1,3,5, 6,9,12}, sut.stream().toArray());
    }

    @Test
    public void removeWithShiftDoesNotContainValueDoesNothing(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.removeAllAndShift(List.of(5,8));

        assertArrayEquals(new int[]{1,4,7,10}, sut.stream().toArray());
    }
    @Test
    public void removeWithShiftIntListDoesNotContainValueDoesNothing(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.removeAllAndShift(Arrays.asList(5, 8));

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
    public void removeMultipleWithShiftMultipleInARow(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,8,9,10,16));

        sut.removeAllAndShift(List.of(7,8,9));

        assertArrayEquals(new int[]{1,4,7, 13}, sut.stream().toArray());

        assertEquals(4, sut.size());
    }

    @Test
    public void removeMultipleWithShiftMultipleInARow2(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,8,9,10,15,16,17,22));

        sut.removeAllAndShift(List.of(7,8,9, 16,17));

        assertArrayEquals(new int[]{1,4,7,12, 17}, sut.stream().toArray());

        assertEquals(5, sut.size());
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
    public void removeMultipleWithShiftIntListSomePresentSomeNotOnlyRemovePresent(){
        Offsets sut = Offsets.fromSortedList(List.of(1,4,7,10));

        sut.removeAllAndShift(Arrays.asList(4,6,7));

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



    @Test
    public void toFromJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Offsets sut  = Offsets.fromSortedList(List.of(1,4,7,10));
        String json = mapper.writeValueAsString(sut);

        Offsets reParsed = mapper.readValue(json, Offsets.class);
        assertEquals(reParsed, sut);
    }

}
