package org.jcvi.jillion.core;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jcvi.jillion.internal.core.util.GrowableIntArray;
import org.junit.Test;
import static org.junit.Assert.*;

public abstract class AbstractTestAsRangeOpperations<T> {

    private final Function<T, List<Range>> asRangesFunction;
    private final BiFunction<T, Integer, List<Range>> asRangesMaxDistanceFunction;
    
    
    public AbstractTestAsRangeOpperations(
            Function<T, List<Range>> asRangesFunction,
            BiFunction<T, Integer, List<Range>> asRangesMaxDistanceFunction) {
        this.asRangesFunction = asRangesFunction;
        this.asRangesMaxDistanceFunction = asRangesMaxDistanceFunction;
    }
    @Test
    public void emptyBitset(){
        assertTrue(Ranges.asRanges(new BitSet()).isEmpty());
    }
    protected abstract T createInput(GrowableIntArray array);
    protected abstract T createInput(int...ints);
    
    @Test
    public void oneBit(){
        T bs = createInput(5);
        
        List<Range> expected = Arrays.asList(Range.of(5));
        
        assertEquals(expected, asRangesFunction.apply(bs));
    }
    
    @Test
    public void twoConsecutiveBits(){
        T bs = createInput(5, 6);
        
        List<Range> expected = Arrays.asList(Range.of(5,6));
        
        assertEquals(expected, asRangesFunction.apply(bs));
    }
    
    @Test
    public void twoNonConsecutiveBits(){
        T bs = createInput(5, 10);
        
        List<Range> expected = Arrays.asList(Range.of(5),
                                                Range.of(10));
        
        assertEquals(expected, asRangesFunction.apply(bs));
    }
    
    @Test
    public void twoNonConsecutiveBitsButWithinMergeMaxDistance(){
        T bs = createInput(5, 10);
        
        List<Range> expected = Arrays.asList(Range.of(5, 10));
        
        assertEquals(expected, asRangesMaxDistanceFunction.apply(bs,10));
    }
    
    @Test
    public void SomeBitsWithinAndBeyondMergeMaxDistance(){
        T bs = createInput(5, 10,22,23);
        
        List<Range> expected = Arrays.asList(Range.of(5, 10),
                                            Range.of(22,23));
        
        assertEquals(expected, asRangesMaxDistanceFunction.apply(bs,10));
    }
    
    @Test
    public void exactlyMaxMergeDistance(){
        T bs = createInput(5, 10,20);
        
        List<Range> expected = Arrays.asList(Range.of(5, 20));
        
        assertEquals(expected, asRangesMaxDistanceFunction.apply(bs,10));
    }
    
    @Test
    public void lotsOfBits(){
       
        GrowableIntArray array = new GrowableIntArray();
        for(int i=0; i<=10; i++){
            array.append(i);
        }
        for(int i=30; i<=464; i++){
            array.append(i);
        }
        T bs = createInput(array);
        List<Range> expected = Arrays.asList(Range.of(0, 10),
                Range.of(30,464));

        assertEquals(expected, asRangesFunction.apply(bs));
    }
}
