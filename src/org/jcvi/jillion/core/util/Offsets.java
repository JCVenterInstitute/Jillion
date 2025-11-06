package org.jcvi.jillion.core.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;
import lombok.Builder;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.RangeCollectors;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.core.residue.ResidueSequenceBuilder;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.PeekableIterator;
import org.jcvi.jillion.core.util.iter.PeekableOfIntIterator;
import org.jcvi.jillion.core.util.streams.ThrowingIntIndexedIntConsumer;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Offsets stores a list of sorted offets indexes,
 * removing will adjust downstream values accordingly.
 *
 * This class is not Threadsafe.
 *
 * @since 6.0.4
 */
public class Offsets {

    @JsonCreator
    public static Offsets fromSortedArray(int[] sortedOffsets) {
        return new Offsets(new GrowableIntArray(sortedOffsets));
    }
    @JsonValue
    public int[] toArray() {
        return delegate.toArray();
    }

    public PrimitiveIterator.OfInt iterator() {
        return delegate.iterator();
    }

    public Offsets copy() {
        return new Offsets(delegate.copy());
    }

    public void clear() {
        delegate.clear();
    }

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

    @Value
    @Builder
    public static class AddOptions{
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

        public static AddOptionsBuilder builder(){
            return new AddOptionsBuilder()
                    .shift(false)
                    .include(true);
        }
        //required for javadoc
        public static class AddOptionsBuilder {

        }
    }

    public static final XorOptions DEFAULT_XOR_OPTIONS = XorOptions.builder().build();
    private GrowableIntArray delegate;

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
        return delegate.toString('{', '}');
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

    /**
     * Build a gapped sequence where all the values in this Offsets are gaps.
     * @param sequence the sequence to use, any gaps in this sequence are ignored/removed before adding
     *                 the new gap offsets.
     * @return a new gapped sequence
     * @param <R>
     * @param <T>
     * @param <B>
     * @implNote this is the same as calling {@link #computeGaps(ResidueSequence, boolean) computeGaps(sequence, false)}
     * @see #computeGaps(ResidueSequence, boolean)
     */
    public <R extends Residue<R>, T extends ResidueSequence<R, T, B>, B extends ResidueSequenceBuilder<R,T,B>> T computeGaps(T sequence) {
        return computeGapsBuilder(sequence).build();
    }
    /**
     * Build a gapped sequence BUILDER where all the values in this Offsets are gaps.
     * @param sequence the sequence to use, any gaps in this sequence are ignored/removed before adding
     *                 the new gap offsets.
     * @return a new gapped sequence
     * @param <R>
     * @param <T>
     * @param <B>
     * @implNote this is the same as calling {@link #computeGaps(ResidueSequence, boolean) computeGaps(sequence, false)}
     * @see #computeGaps(ResidueSequence, boolean)
     */
    public <R extends Residue<R>, T extends ResidueSequence<R, T, B>, B extends ResidueSequenceBuilder<R,T,B>> B computeGapsBuilder(T sequence) {
        return computeGapsBuilder(sequence, false);
    }
    /**
     * Build a gapped sequence where all the values in this Offsets are gaps.
     * @param sequence the sequence to use, any gaps in this sequence are ignored/removed before adding
     *                 the new gap offsets.
     * @param preShifted {@code true} if these offsets are already "preshifted" to account for adding the gaps, {@code false} otherwise.
     *
     * @apiNote For example, if the sequence is {@code ACGTACGT} and our offsets are {@code {4,6}}
     * if preShifted was set to {@code false} then the 2nd offset {6} would get shifted by 1 to {7}
     * to account for the new gap added at {4} to be {@code ACGT-AC-GT}, but if preShifted was set to {@code true},
     * then the sequence would be {@code ACGT-A-CGT} as the gap offset of {6} would not be shifted.
     *
     * @return a new gapped sequence
     * @param <R>
     * @param <T>
     * @param <B>
     * @implNote this is the same as calling {@link #computeGaps(ResidueSequence, boolean) computeGaps(sequence, false)}
     * @see #computeGaps(ResidueSequence, boolean)
     */
    public <R extends Residue<R>, T extends ResidueSequence<R, T, B>, B extends ResidueSequenceBuilder<R,T,B>> T computeGaps(T sequence, boolean preShifted){

        return computeGapsBuilder(sequence, preShifted).build();
    }
    /**
     * Build a gapped sequence where all the values in this Offsets are gaps.
     * @param sequence the sequence to use, any gaps in this sequence are ignored/removed before adding
     *                 the new gap offsets.
     * @param preShifted {@code true} if these offsets are already "preshifted" to account for adding the gaps, {@code false} otherwise.
     *
     * @apiNote For example, if the sequence is {@code ACGTACGT} and our offsets are {@code {4,6}}
     * if preShifted was set to {@code false} then the 2nd offset {6} would get shifted by 1 to {7}
     * to account for the new gap added at {4} to be {@code ACGT-AC-GT}, but if preShifted was set to {@code true},
     * then the sequence would be {@code ACGT-A-CGT} as the gap offset of {6} would not be shifted.
     *
     * @return a new gapped sequence
     * @param <R>
     * @param <T>
     * @param <B>
     * @implNote this is the same as calling {@link #computeGaps(ResidueSequence, boolean) computeGaps(sequence, false)}
     * @see #computeGaps(ResidueSequence, boolean)
     */
    public <R extends Residue<R>, T extends ResidueSequence<R, T, B>, B extends ResidueSequenceBuilder<R,T,B>> B computeGapsBuilder(T sequence, boolean preShifted){
        B builder = sequence.toBuilder()
                .ungap();
        if(delegate.getCurrentLength()==0){

            return builder;
        }
        List<Range> ranges = Ranges.asRanges(delegate.toArray());

        if(!preShifted){
            Collections.reverse(ranges);

        }




        ranges.forEach( r->{
            if(builder.getLength() >= r.getBegin()){
                builder.insertGap(r);
            }
                });

        return builder;
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

        //this range math is so we don't have overlapping range positions
        //which will simplify merging down below

        List<Range> aRanges = delegate.asRanges();
        List<Range> bRanges = b.delegate.asRanges();

        List<Range> aExclusive = Ranges.complement(aRanges, bRanges);
        List<Range> bExclusive = Ranges.complement(bRanges, aRanges);
        List<Range> inBoth = Ranges.union(aRanges, bRanges);

        List<Range> merged = Stream.concat(aExclusive.stream(), inBoth.stream())
                                    .collect(RangeCollectors.mergeRanges());


        //at this point, everything in a is in merged and stuff we have to shift is in bExclusive


        PeekableIterator<Range> aIter = IteratorUtil.createPeekableIterator(merged);

        PeekableIterator<Range> bIter = IteratorUtil.createPeekableIterator(bExclusive);

        int shift=0;
        GrowableIntArray array = new GrowableIntArray(delegate.getCurrentLength());

        int aNext;
        int bNext;
        //this allows us to add multiple B ranges in a row without shifting
        int bShiftedSinceLastA=0;
        while(aIter.hasNext() && bIter.hasNext()){
            aNext = (int) aIter.peek().getBegin() +shift;
            bNext = (int) bIter.peek().getBegin() +shift-bShiftedSinceLastA;
            if(aNext ==bNext){
                //both ranges start at same position but might be different lengths
                int aLength = (int)aIter.peek().getLength();
                int bLength = (int) bIter.peek().getLength();
                //either way we add all of A
                for(int i=0; i< aIter.peek().getLength(); i++) {
                    array.append(aNext+i);
                }


                if(aLength < bLength) {
                    //B is bigger
                    // we have to deal with shifting
                    // because we did the Range math above
                    // we don't have to do extra checking like "does the next A also intersect this B ?"
                    // because we handled that already by splitting A and B ranges into non-overlapping
                    int shiftAmount = bLength - aLength;
                    if(include) {
                        for(int i=0; i< shiftAmount; i++) {
                            array.append(aNext+aLength +i);
                        }
                    }
                    shift+=shiftAmount;
                }
                aIter.next();
                bIter.next();
            }else if(aNext < bNext){
                for(int i=0; i< aIter.peek().getLength(); i++) {
                    array.append(aNext+i);
                }
                aIter.next();
                bShiftedSinceLastA=0;
            }else{
                if(include) {
                    for(int i=0; i< bIter.peek().getLength(); i++) {
                        array.append(bNext+i);
                    }

                }
                int shiftThisRange = (int) bIter.next().getLength();
                bShiftedSinceLastA +=shiftThisRange;
                shift+=shiftThisRange;

            }
        }
        int effectivelyFinalShift = shift;
        Consumer<Range> rangeConsumer =r->{
            int begin = (int) r.getBegin();
            for(int i=0; i< r.getLength(); i++) {
                array.append(begin+i + effectivelyFinalShift);
            }
        };

        aIter.forEachRemaining(rangeConsumer);
        if(include) {
            bIter.forEachRemaining(rangeConsumer);
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
    public static Offsets withInitialCapacity(int initialCapacity){
        return new Offsets(initialCapacity);
    }
    private Offsets(int initialCapacity){
        this(new GrowableIntArray(initialCapacity));
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
    public int getNumberOfValuesUntil( int offset){
        return computeInsertionPointOf(offset, false);
    }
    public int computeInsertionPointOf(int gappedOffset){
        return computeInsertionPointOf(gappedOffset, true);
    }
    public int computeInsertionPointOf(int gappedOffset, boolean afterCollision){
        int insertionPoint = delegate.binarySearch(gappedOffset);
        if(insertionPoint >=0){
            //if we landed on a gap, then
            //the we want the length of the array
            //up until that offset so that's why it's +1
            return afterCollision? insertionPoint +1 : insertionPoint;
        }
        return -insertionPoint -1;
    }

    public boolean isEmpty(){
        return delegate.getCurrentLength()==0;
    }

    /**
     * Append the given offsets to the end these offsets
     * shifting their values by the current Offset length
     * @param other
     */
    public void appendAndShift(Offsets other){
        appendAndShift(other, delegate.getCurrentLength());

    }
    /**
     * Append the given offsets to the end these offsets
     * shifting their values by the current Offset length
     * @param other
     */
    public void appendAndShift(Offsets other, int shiftAmount){

        delegate.append(other.delegate, i-> i+shiftAmount);

    }

    public void prependAndShift(Offsets other, int shiftAmount){
        GrowableIntArray newDelegate = new GrowableIntArray(other.delegate.getCurrentLength() + delegate.getCurrentLength());
        newDelegate.append(other.delegate);
        newDelegate.append(delegate, i-> i+shiftAmount);

        delegate = newDelegate;


    }

    public void insertAndShift(Offsets other, int shiftAmount, int startOffset) {
        if(startOffset ==0){
            prependAndShift(other,shiftAmount);
            return;
        }
        int insertionPoint = computeInsertionPointOf(startOffset, false);

        GrowableIntArray newDelegate = new GrowableIntArray(other.delegate.getCurrentLength() + delegate.getCurrentLength());


        delegate.arrayCopy(0, newDelegate, 0, insertionPoint);
        newDelegate.append(other.delegate, i -> i + startOffset);
        newDelegate.append(delegate, insertionPoint, delegate.getCurrentLength()-insertionPoint, i -> i + shiftAmount);

       delegate = newDelegate;



    }



    public void reverseCoordinates(int sequenceLength){
        int delta = sequenceLength-1;
        delegate.replaceAll(i-> delta-i);
        delegate.reverse();
    }

    public void addAndShift(int value){
        if(contains(value)){
            return;
        }

        delegate.replaceIf(i-> i>value, i-> i+1);
        delegate.sortedInsert(value);
    }

    public void add(Offsets offsetsToAdd, AddOptions options){
        PrimitiveIterator.OfInt iter = offsetsToAdd.delegate.reverseIterator();
        IntConsumer function = options.shouldShift()? this::addAndShift : this::add;

        while(iter.hasNext()){
            function.accept(iter.nextInt());
        }
    }
    @Value
    public static class UniqueOffsets{
        Offsets a, b;
    }

    public static UniqueOffsets unique(Offsets a, Offsets b){
        XorOptions split = XorOptions.builder()
                .include(false)
                .shift(false)
                .build();

        return new UniqueOffsets(
                a.xor(b, split),
                b.xor(a, split));
    }


    private void replaceIfUnsafe(IntPredicate predicate, IntUnaryOperator replacementFunction) {
        delegate.replaceIf(predicate, replacementFunction);
    }
    public void replaceIf(IntPredicate predicate, IntUnaryOperator replacementFunction){

        replaceIfUnsafe(predicate, replacementFunction);

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

            dups.forEach(delegate::remove);
        }

    }

    /**
     * Appends the given value to the end of the offset list.
     * WARNING: this does not checking for sorted-ness
     * and is only provided for use when you know that values are safe
     * to append to the end of the offsets.  This is mostly used
     * in performance focused code when data to append is known to be pre-sorted.
     * 
     * @param value the value to append
     * @see #add(int) 
     * @see #addAndShift(int)
     */
    public void appendUnsafe(int value){
        delegate.append(value);
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

    /**
     * Adjusts this Offset coordinates to shift to the left all set values
     * to adjust for the removal of the gap offsets which are provided.
     *
     * For example if this Offsets has a value of {@code { 1, 4}} and we want
     * to ungap it and there is a gap at {@code {2}} then the result is our
     * adjusted Offsets will now be {@code {1, 3}} since the offset at 4 will be
     * shifted one to the left after the gap is removed.
     *
     * @param gapOffsets the gaps that will be removed; can not be null
     *
     * @throws NullPointerException if gapOffsets is null.
     *
     * @implNote This should produce the same result as repeated
     * calls to {@link #removeAndShift(int)} for all gap positions
     * but this should be more efficient.
     */
    public void ungap(Offsets gapOffsets){
        if(gapOffsets.isEmpty()){
            //no gaps to remove
            return;
        }
        //first we have to shift the N's
        PeekableOfIntIterator gapOffsetIter = IteratorUtil.createPeekableIterator(gapOffsets.iterator());

        int shiftSize=0;
        int i=0;
        while(i< delegate.getCurrentLength()){
            int currentNOffset = delegate.get(i);
            while(gapOffsetIter.hasNext()){
                int nextGapOffset =gapOffsetIter.peek();
                if(nextGapOffset < currentNOffset){
                    shiftSize++;
                    gapOffsetIter.next();
                }else{
                    break;
                }
            }
            delegate.replaceUnsafe(i, currentNOffset - shiftSize);
            i++;
        }

    }
    public void removeAndShift(int offset){

        delegate.sortedRemove(offset);
        delegate.replaceIf(i-> i> offset, i-> i-1);

    }
    public void removeAllAndShift(Offsets valuesToRemove, int startOffset, int otherSequenceLength){



        if(!valuesToRemove.isEmpty()) {
            int downStreamOffset = computeInsertionPointOf(startOffset + otherSequenceLength);
            if (downStreamOffset >= delegate.getCurrentLength()) {
                //nothing to shift downstream of whole other offsets
                //check if we have anything that will remain
                int lastValueToRemove = valuesToRemove.delegate.get(valuesToRemove.delegate.getCurrentLength() - 1) + startOffset;
                int lastValueInsertionPoint = computeInsertionPointOf(lastValueToRemove, false);
                if (lastValueInsertionPoint > delegate.getCurrentLength()) {

                    int insertionPoint = computeInsertionPointOf(startOffset, false);
                    //check if there is anything to remove at all?
                    if (insertionPoint < delegate.getCurrentLength()) {
                        delegate.truncate(insertionPoint);
                    }
                    return;
                }

            }
            valuesToRemove.forEach(v->{
                delegate.sortedRemove(v+startOffset);
            });

        }
        delegate.replaceIf(i-> i >= startOffset, i-> i-otherSequenceLength);

    }
    public void removeAllAndShift(Offsets valuesToRemove){

        int[] valuesToRemoveArray = valuesToRemove.stream()
                .filter(i-> delegate.binarySearch(i) >=0)
                .sorted()
                .toArray();


        for(int j = valuesToRemoveArray.length-1; j>=0; j--){
            int v = valuesToRemoveArray[j];
            delegate.sortedRemove(v);
            delegate.replaceIf(i-> i> v, i-> i-1);
        }

    }
    public void removeAllAndShift(IntList valuesToRemove){

        _removeAll(valuesToRemove, true);

    }

    private void _removeAll(IntList valuesToRemove, boolean shift){

        int[] valuesToRemoveArray = valuesToRemove.intStream()
                .filter(i-> delegate.binarySearch(i) >=0)
                .sorted()
                .toArray();

        if(valuesToRemoveArray.length ==0){
            return;
        }
        for(int j = valuesToRemoveArray.length-1; j>=0; j--){
            int v = valuesToRemoveArray[j];
            delegate.remove(v);
            if(shift) {
                delegate.replaceIf(i -> i > v, i -> i - 1);
            }
        }

    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Offsets)) return false;
        Offsets offsets = (Offsets) o;
        return Objects.equals(delegate, offsets.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(delegate);
    }

    public void removeAllNoShift(IntList valuesToRemove){
        _removeAll(valuesToRemove, false);
    }
    private void _removeAll(IntStream intStream, boolean shift){

        int[] valuesToRemoveArray = intStream
                .map(delegate::binarySearch)
                .filter(i-> i >=0)
                .sorted()
                .toArray();

        if(valuesToRemoveArray.length ==0){
            return;
        }
        List<Range> ranges = Ranges.asRanges(valuesToRemoveArray);
        Collections.reverse(ranges);
        for(Range r : ranges){

            int begin = (int) r.getBegin();
            int end = (int) r.getEnd();
            int length = end - begin+1;

            if(shift && end < delegate.getCurrentLength()) {
                Range downstreamRange = Range.of(end + 1, delegate.getCurrentLength() - 1);
                delegate.replaceIf(downstreamRange, i -> i > end, i -> i - length);
            }
            delegate.remove(Range.of(begin, end));
        }

    }
    public void removeAllAndShift(List<Integer> valuesToRemove){
        _removeAll(valuesToRemove.stream().mapToInt(Integer::intValue), true);

    }

    public void shift(int shiftAmount){
        if(shiftAmount==0){
            //no-op
            return;
        }
        delegate.replaceAll(i-> i+ shiftAmount);
    }
}
