package org.jcvi.jillion.internal.core.util;

import lombok.*;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;
import org.jcvi.jillion.core.util.IntList;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.PeekableOfIntIterator;
import org.jcvi.jillion.core.util.streams.ThrowingIntIndexedIntConsumer;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.function.IntConsumer;
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

   @Value
   @Builder
    public static class XorOptions{
       @Getter(AccessLevel.NONE)
       boolean shift;
       @Getter(AccessLevel.NONE)
       boolean include;

       public boolean shouldShift(){
           return shift;
       }
       public boolean shouldInclude(){
           return include;
       }

       public static XorOptionsBuilder builder(){
           return new XorOptionsBuilder()
                   .shift(false)
                   .include(true);
       }
       //required for javadoc
       public static class XorOptionsBuilder {

       }
    }

    public static final XorOptions DEFAULT_XOR_OPTIONS = XorOptions.builder().build();
    private final GrowableIntArray delegate;

    public Offsets or(Offsets b){

        GrowableIntArray newArray = delegate.copy();
        b.forEach(v->{
            if(delegate.binarySearch(v) <0){
                newArray.append(v);
            }
        });
        newArray.sort();
        return new Offsets(newArray);

    }
    public Offsets xor(Offsets b){
        return xor(b, DEFAULT_XOR_OPTIONS);
    }
    public Offsets xor(Offsets b, XorOptions options){

        if(options.shouldShift()){
            return _xor(b, options.shouldInclude());
        }
        return _xorWithoutShifting(b, options.shouldInclude());

    }
    private Offsets _xor(Offsets b, boolean include){

        //TODO this is very similar to merge and shift is there a way to rewrite
        //with lambdas for code re-use?

        PeekableOfIntIterator aIter = IteratorUtil.createPeekableIterator(delegate.iterator());
        PeekableOfIntIterator bIter = IteratorUtil.createPeekableIterator(b.delegate.iterator());

        int shift=0;
        GrowableIntArray array = new GrowableIntArray(delegate.getCurrentLength());

        int aNext;
        int bNext;
        while(aIter.hasNext() && bIter.hasNext()){
            aNext = aIter.peek();
            bNext = bIter.peek();
            if(aNext ==bNext){
                //XOR exclude!
                aIter.next();
                bIter.next();
                shift++;

            }else if(aNext < bNext){
                array.append(aNext-shift);
                aIter.next();
            }else{
                if(include) {
                    array.append(bNext - shift);
                }
                bIter.next();
            }
        }
        int effectivelyFinalShift = shift;
        IntConsumer andAndShiftRemaining = v-> array.append(v- effectivelyFinalShift);

        aIter.forEachRemaining(andAndShiftRemaining);
        if(include) {
            bIter.forEachRemaining(andAndShiftRemaining);
        }


        return new Offsets(array);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    private Offsets _xorWithoutShifting(Offsets b, boolean include){

        GrowableIntArray newArray = new GrowableIntArray(delegate.getCurrentLength());

        delegate.forEach(v->{
            if(b.delegate.binarySearch(v) <0){
                newArray.append(v);
            }
        });
        if(include) {
            b.forEach(v -> {
                if (delegate.binarySearch(v) < 0) {
                    newArray.append(v);
                }
            });
            newArray.sort();
        }
        return new Offsets(newArray);

    }

    public <R extends Residue<R>, T extends ResidueSequence<R, T, B>, B extends ResidueSequenceBuilder<R,T,B>> T computeGaps(T sequence){

        if(delegate.getCurrentLength()==0){
            return sequence.computeUngappedSequence();
        }
        List<Range> ranges = Ranges.asRanges(delegate.toArray());
        Collections.reverse(ranges);

        B builder = sequence.toBuilder()
                .ungap();
        ranges.forEach(builder::insertGap);

        return builder.build();
    }
    public Offsets and(Offsets b){

        GrowableIntArray newArray = new GrowableIntArray();


        b.forEach(v->{
            if(delegate.binarySearch(v) >=0){
                newArray.append(v);
            }
        });

        return new Offsets(newArray);

    }


    public <E extends Throwable> void forEachIndexed(ThrowingIntIndexedIntConsumer<E> consumer) throws E{
        delegate.forEachIndexed(consumer);
    }
    public void forEach(IntConsumer consumer){
        forEachIndexed((index, value) -> consumer.accept(value));
    }
    public Offsets mergeAndShift(Offsets b){
        return mergeAndShift(b, true);
    }
    public Offsets mergeAndShift(Offsets b, boolean include){
        PeekableOfIntIterator aIter = IteratorUtil.createPeekableIterator(delegate.iterator());
        PeekableOfIntIterator bIter = IteratorUtil.createPeekableIterator(b.delegate.iterator());

        int shift=0;
        GrowableIntArray array = new GrowableIntArray(delegate.getCurrentLength());

        int aNext;
        int bNext;
        while(aIter.hasNext() && bIter.hasNext()){
            aNext = aIter.peek()+shift;
            bNext = bIter.peek()+shift;
            if(aNext ==bNext){
                //same value just add
                array.append(aNext);
                aIter.next();
                bIter.next();
            }else if(aNext < bNext){
                array.append(aNext);
                aIter.next();
            }else{
                if(include) {
                    array.append(bNext);
                }
                bIter.next();
                shift++;
            }
        }
        int effectivelyFinalShift = shift;
        IntConsumer andAndShiftRemaining = v-> array.append(v+effectivelyFinalShift);

        aIter.forEachRemaining(andAndShiftRemaining);
        if(include) {
            bIter.forEachRemaining(andAndShiftRemaining);
        }

        return new Offsets(array);
    }

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
    public static Offsets fromSortedList(IntList offsets){
        return new Offsets(offsets, false,true);
    }

    public static Offsets fromUnsortedList(IntList offsets){
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
    private Offsets(IntList sortedOffsets,boolean resort, boolean assertSorted){
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

    public IntList asList(){
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
    public void removeAllAndShift(IntList valuesToRemove){

        int[] valuesToRemoveArray = valuesToRemove.intStream()
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
