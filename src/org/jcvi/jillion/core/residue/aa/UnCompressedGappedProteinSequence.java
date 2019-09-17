package org.jcvi.jillion.core.residue.aa;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.util.MemoizedSupplier;
import org.jcvi.jillion.core.util.iter.ArrayIterator;
import org.jcvi.jillion.internal.core.residue.AbstractResidueSequence;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class UnCompressedGappedProteinSequence extends AbstractResidueSequence<AminoAcid, ProteinSequence, ProteinSequenceBuilder> implements ProteinSequence{

    private final AminoAcid[] array;

    private final Supplier<GrowableIntArray> gapSupplier;
    private final Supplier<String> stringSupplier;

    public UnCompressedGappedProteinSequence(AminoAcid[] array) {
        this.array = array;
         gapSupplier= MemoizedSupplier.memoize(()->{
            GrowableIntArray gaps = new GrowableIntArray();
            int length = array.length;
            for(int i=0; i< length; i++){
                if(array[i] == AminoAcid.Gap){
                    gaps.append(i);
                }
            }
            return gaps;
        });

         stringSupplier = MemoizedSupplier.memoize(()->{
             StringBuilder builder = new StringBuilder((int)getLength());
             for(AminoAcid aa : this){
                 builder.append(aa.asChar());
             }
             return builder.toString();
         });
    }

    @Override
    public List<Integer> getGapOffsets() {
        return gapSupplier.get().toBoxedList();
    }

    @Override
    public int getNumberOfGaps() {
        return gapSupplier.get().getCurrentLength();
    }

    @Override
    public boolean isGap(int gappedOffset) {
        return gapSupplier.get().binarySearch(gappedOffset) >=0;
    }


    @Override
    public AminoAcid get(long offset) {
        return array[(int) offset];
    }

    @Override
    public long getLength() {
        return array.length;
    }

    @Override
    public Iterator<AminoAcid> iterator(Range range) {
        return new ArrayIterator<>(Arrays.copyOfRange(array, (int) range.getBegin(), (int)range.getEnd() +1));
    }

    @Override
    public ProteinSequenceBuilder toBuilder() {
        return new ProteinSequenceBuilder(this);
    }

    @Override
    public ProteinSequenceBuilder toBuilder(Range range) {
        return new ProteinSequenceBuilder(this, range);
    }

    @Override
    public ProteinSequenceBuilder newEmptyBuilder() {
        return new ProteinSequenceBuilder();
    }

    @Override
    public ProteinSequenceBuilder newEmptyBuilder(int initialCapacity) {
        return new ProteinSequenceBuilder(initialCapacity);
    }

    @Override
    public ProteinSequence asSubtype() {
        return this;
    }

    @Override
    public Iterator<AminoAcid> iterator() {
        return  new ArrayIterator<>(array);
    }

    @Override
    public String toString() {
        return stringSupplier.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnCompressedGappedProteinSequence)) return false;
        UnCompressedGappedProteinSequence that = (UnCompressedGappedProteinSequence) o;
        return Arrays.equals(array, that.array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }
}
