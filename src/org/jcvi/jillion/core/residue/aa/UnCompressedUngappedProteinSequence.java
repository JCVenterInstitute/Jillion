package org.jcvi.jillion.core.residue.aa;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.util.MemoizedSupplier;
import org.jcvi.jillion.core.util.iter.ArrayIterator;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class UnCompressedUngappedProteinSequence implements ProteinSequence{
    //This class uses the Serialization Proxy Pattern
    //described in Effective Java 2nd Ed
    //to substitute a proxy class to be serialized.

    private final AminoAcid[] array;
    private final Supplier<String> stringSupplier;

    public UnCompressedUngappedProteinSequence(AminoAcid[] array) {
        this.array = array;

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
        return Collections.emptyList();
    }

    @Override
    public int getNumberOfGaps() {
        return 0;
    }

    @Override
    public boolean isGap(int gappedOffset) {
        return false;
    }

    @Override
    public long getUngappedLength() {
        return array.length;
    }

    @Override
    public int getNumberOfGapsUntil(int gappedOffset) {
        return 0;
    }

    @Override
    public int getUngappedOffsetFor(int gappedOffset) {
        return gappedOffset;
    }

    @Override
    public int getGappedOffsetFor(int ungappedOffset) {
        return ungappedOffset;
    }

    @Override
    public AminoAcid get(long offset) {
        return array[(int)offset];
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
        if (!(o instanceof UnCompressedUngappedProteinSequence)) return false;
        UnCompressedUngappedProteinSequence that = (UnCompressedUngappedProteinSequence) o;
        return Arrays.equals(array, that.array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    @Override
    public Iterator<AminoAcid> ungappedIterator() {
        return iterator();
    }

    private void readObject(ObjectInputStream stream) throws java.io.InvalidObjectException{
        throw new java.io.InvalidObjectException("Proxy required");
    }


    private Object writeReplace(){
        return new ProteinSequenceProxy(this);
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
