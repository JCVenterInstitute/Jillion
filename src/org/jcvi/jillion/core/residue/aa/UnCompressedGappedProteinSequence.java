package org.jcvi.jillion.core.residue.aa;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.util.MemoizedSupplier;
import org.jcvi.jillion.core.util.iter.ArrayIterator;
import org.jcvi.jillion.internal.core.residue.AbstractResidueSequence;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.IntStream;
/**
 * ProteinSequence implementation that
 * stores the amino acids as a simple Array
 * this takes up more memory but is much
 * faster than compacting it down to bits.
 *
 * @since 5.3.2
 */
class UnCompressedGappedProteinSequence extends AbstractResidueSequence<AminoAcid, ProteinSequence, ProteinSequenceBuilder> implements ProteinSequence{
    //This class uses the Serialization Proxy Pattern
    //described in Effective Java 2nd Ed
    //to substitute a proxy class to be serialized.

    private final AminoAcid[] array;

    private transient final Supplier<GrowableIntArray> gapSupplier;
    private transient final Supplier<String> stringSupplier;

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
    public IntStream gaps() {
        return gapSupplier.get().stream();
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
        if( !(o instanceof ProteinSequence)){
            return false;
        }
        if (o instanceof UnCompressedGappedProteinSequence) {
            UnCompressedGappedProteinSequence that = (UnCompressedGappedProteinSequence) o;
            return Arrays.equals(array, that.array);
        }else{
            return toString().equals(  o.toString());
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    private Object writeReplace(){
        return new ProteinSequenceProxy(this);
    }

    @Override
    public ProteinSequence trim(Range trimRange) {
        return new UnCompressedUngappedProteinSequence(Arrays.copyOfRange(array, (int)trimRange.getBegin(), (int) trimRange.getEnd()+1));
    }

    private void readObject(ObjectInputStream stream) throws java.io.InvalidObjectException{
        throw new java.io.InvalidObjectException("Proxy required");
    }

    /**
     * Serialization Proxy Pattern object to handle
     * serialization of ProteinSequence objects.  This allows us
     * to change ProteinSequence fields and subclasses without
     * breaking serialization.
     *
     * @author dkatzel
     *
     */
    private static final class ProteinSequenceProxy implements Serializable {

        private static final long serialVersionUID = -8473861196950222580L;

        private final String seq;

        ProteinSequenceProxy(ProteinSequence s){
            seq = s.toString();
        }

        private Object readResolve(){
            return new ProteinSequenceBuilder(seq)
                    .turnOffDataCompression(true)
                    .build();
        }
    }
}
