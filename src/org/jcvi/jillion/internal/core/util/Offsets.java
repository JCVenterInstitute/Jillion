package org.jcvi.jillion.internal.core.util;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;

import java.util.BitSet;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

/**
 * Offsets stores a list of sorted offets indexes,
 * removing will adjust downstream values accordingly.
 *
 * This class is not Threadsafe.
 *
 * @since 6.0.4
 */
public class Offsets {

    private GrowableIntArray delegate;

    public static Offsets fromRanges(List<Range> ranges){
        BitSet bits = new BitSet();
        for(Range r : ranges){
            bits.set((int) r.getBegin(), (int) (r.getEnd()+1));
        }
        return new Offsets(new GrowableIntArray(bits.stream().toArray()));
    }
    public static Offsets fromSortedList(List<Integer> offsets){
        return new Offsets(offsets, false,true);
    }

    public static Offsets fromUnsortedList(List<Integer> offsets){
        return new Offsets(offsets, true,true);
    }
    private Offsets(GrowableIntArray array){
        this.delegate = array;
    }
    private Offsets(List<Integer> sortedOffsets,boolean resort, boolean assertSorted){
        this.delegate = new GrowableIntArray(sortedOffsets);
        if(resort){
            delegate.sort();
        }
        if(assertSorted){
            PrimitiveIterator.OfInt iter = this.delegate.iterator();
            if(!iter.hasNext()){
                return;
            }
            int prev = iter.nextInt();
            while(iter.hasNext()){
                int cur =iter.nextInt();
                if(cur <= prev){
                    throw new IllegalArgumentException("offsets must be monotonically strictly increasing but found 2 consecutive values " + prev +" and " + cur);
                }
            }
        }
    }


    public int size(){
        return delegate.getCurrentLength();
    }
    public IntStream stream(){
        return delegate.stream();
    }

    public List<Integer> asList(){
       return delegate.toBoxedList();
    }

    public boolean contains(int offset){
         return delegate.binarySearch(offset)>=0;
    }

    public void addAndShift(int value){
        if(contains(value)){
            return;
        }
        delegate.replaceIf(i-> i>value, i-> i+1);
        delegate.sortedInsert(value);
    }

    public void replaceIf(IntPredicate predicate, IntUnaryOperator replacementFunction){

        delegate.replaceIf(predicate, replacementFunction);
        delegate.sort();
        GrowableIntArray dups = new GrowableIntArray();
        PrimitiveIterator.OfInt iter = delegate.iterator();
        if(!iter.hasNext()){
            return;
        }
        int i=0;
        int prev = iter.nextInt();
        while(iter.hasNext()){
            int current = iter.nextInt();
            if(prev ==current){
                dups.append(i);
            }
            prev = current;
            i++;
        }

        if(dups.getCurrentLength() > 0) {
            dups.reverse();

            dups.forEach(k -> delegate.remove(k));
        }

    }

    public void add(int value){
        if(contains(value)){
            return;
        }

        delegate.sortedInsert(value);
    }

    public void remove(int offset){

        delegate.sortedRemove(offset);

    }
    public void removeAndShift(int offset){

        delegate.sortedRemove(offset);
        delegate.replaceIf(i-> i> offset, i-> i-1);

    }
    public void removeAllAndShift(List<Integer> valuesToRemove){

        int[] valuesToRemoveArray = valuesToRemove.stream().mapToInt(Integer::intValue)
                        .filter(i-> delegate.binarySearch(i) >=0)
                        .sorted()
                        .toArray();

        if(valuesToRemoveArray.length ==0){
            return;
        }
        for(int j = valuesToRemoveArray.length-1; j>=0; j--){
            int v = valuesToRemoveArray[j];
            delegate.sortedRemove(v);
            delegate.replaceIf(i-> i> v, i-> i-1);
        }

    }
}
