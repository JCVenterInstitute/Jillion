package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Ranges;
import org.jcvi.jillion.core.util.iter.ArrayIterator;
import org.jcvi.jillion.internal.core.io.StreamUtil;
import org.jcvi.jillion.internal.core.residue.AbstractResidueSequence;
import org.jcvi.jillion.internal.core.util.ArrayUtil;
import org.jcvi.jillion.internal.core.util.GrowableByteArray;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;
import org.jcvi.jillion.internal.core.util.MemoizedSupplier;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

abstract class AbstractSimpleNucleotideSequence extends AbstractResidueSequence<Nucleotide, NucleotideSequence, NucleotideSequenceBuilder> implements NucleotideSequence{

    private final Nucleotide[] data;

    private transient final Supplier<String> stringSupplier;
    private transient final Supplier<List<Range>> nRangeSupplier;
    private transient final Supplier<GrowableIntArray> gapSupplier;
    private transient final Supplier<Boolean> isDnaSupplier, isRnaSupplier;

    public AbstractSimpleNucleotideSequence(GrowableByteArray data) {
        this(data.stream().mapToObj(i -> Nucleotide.getByOrdinal(i)).toArray(i -> new Nucleotide[i]));
    }
    public AbstractSimpleNucleotideSequence(Nucleotide[] data) {
        this.data = data;
        stringSupplier = MemoizedSupplier.memoize(()->{
           
            return new NucleotideSequenceBuilder(List.of(this.data)).toString();
        });

        nRangeSupplier = MemoizedSupplier.memoize(()->{
            GrowableIntArray ns = new GrowableIntArray();
            int i=0;
            for(Nucleotide n : this.data){

                if(n == Nucleotide.Unknown){
                    ns.append(i);
                }
                i++;
            }
            return Ranges.asRanges(ns.toArray());
        });

        gapSupplier = MemoizedSupplier.memoize(()->{
            GrowableIntArray gaps = new GrowableIntArray();
            int length = data.length;
            for(int i=0; i< length; i++){
                if(data[i] == Nucleotide.Gap){
                    gaps.append(i);
                }
            }
            return gaps;
        });
        isDnaSupplier = MemoizedSupplier.memoize(()->{
            //can't find any U's
            return !Arrays.stream(data).filter(v-> v==Nucleotide.Uracil).findAny().isPresent();

        });

        isRnaSupplier = MemoizedSupplier.memoize(()->{
            //can't find any T's
            return !Arrays.stream(data).filter(v-> v==Nucleotide.Thymine).findAny().isPresent();

        });
    }

    @Override
    public Stream<Range> findMatches(Pattern pattern) {
        Matcher matcher = pattern.matcher(toString());

        return StreamUtil.newGeneratedStream(() -> matcher.find()
                ? Optional.of(Range.of(matcher.start(), matcher.end() - 1))
                : Optional.empty());
    }

    @Override
    public Stream<Range> findMatches(Pattern pattern, Range subSequenceRange) {

        StringBuilder builder = new StringBuilder((int) subSequenceRange.getLength());
        Arrays.stream(data, (int)subSequenceRange.getBegin(), (int) subSequenceRange.getEnd()+1)
                .map(Nucleotide::getCharacter)
                .forEach(builder::append);

        String subSeq= builder.toString();
        Matcher matcher = pattern.matcher(subSeq);
        int shift = (int) subSequenceRange.getBegin();
        return StreamUtil.newGeneratedStream(() -> matcher.find()
                ? Optional.of(Range.of(shift+ matcher.start(), shift+ matcher.end() - 1))
                : Optional.empty());
    }

    @Override
    public List<Range> getRangesOfNs() {
        return new ArrayList<>(nRangeSupplier.get());
    }

    @Override
    public boolean isDna() {
        return isDnaSupplier.get();
    }

    @Override
    public boolean isRna() {
        return isRnaSupplier.get();
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
    public List<Range> getRangesOfGaps() {
        return Ranges.asRanges(gapSupplier.get().toArray());
    }

    @Override
    public int getNumberOfGaps() {
        return gapSupplier.get().getCurrentLength();
    }

    @Override
    public boolean isGap(int gappedOffset) {
        return get(gappedOffset) == Nucleotide.Gap;
    }

    @Override
    public Nucleotide get(long offset) {
        return data[(int)offset];
    }

    @Override
    public long getLength() {
        return data.length;
    }

    @Override
    public Iterator<Nucleotide> iterator(Range range) {
        return Arrays.stream(data, (int) range.getBegin(), (int) range.getEnd()+1)
                    .iterator();

    }

    @Override
    public NucleotideSequenceBuilder toBuilder() {
        return new NucleotideSequenceBuilder((Iterable<Nucleotide>) this::iterator)
        		.turnOffDataCompression(true);
    }

    @Override
    public NucleotideSequenceBuilder toBuilder(Range range) {
        return new NucleotideSequenceBuilder(this, range)
        		.turnOffDataCompression(true);
    }
    
    @Override
    public NucleotideSequenceBuilder toBuilder(List<Range> ranges) {
        return new NucleotideSequenceBuilder(this, ranges)
        		.turnOffDataCompression(true);
    }

    @Override
    public NucleotideSequence asSubtype() {
        return this;
    }

    @Override
    public Iterator<Nucleotide> iterator() {
        return Arrays.stream(data).iterator();
    }

    @Override
    public String toString() {
        return stringSupplier.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NucleotideSequence)){
            return false;
        }
        if( o instanceof AbstractSimpleNucleotideSequence){
            return Arrays.equals(data, ((AbstractSimpleNucleotideSequence)o).data);
        }

            return toString().equals( o.toString());

    }

    @Override
	public Iterator<Nucleotide> reverseComplementIterator() {
		Nucleotide[] copy = Arrays.stream(data).map(Nucleotide::complement).toArray(i-> new Nucleotide[i]);
		ArrayUtil.reverse(copy);
		
		return new ArrayIterator<Nucleotide>(copy, false);
	}
	@Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public NucleotideSequence trim(Range trimRange) {
    	return createNewInstance(Arrays.copyOfRange(data,
                (int)trimRange.getBegin(), (int) trimRange.getEnd()+1));
        
    }
    protected abstract NucleotideSequence createNewInstance(Nucleotide[] dataArray);
}
