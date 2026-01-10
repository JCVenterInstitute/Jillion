package org.jcvi.jillion.core.residue.nt;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;

import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Creates a CharSequence efficiently from a {@link NucleotideSequence}
 * without having to convert the whole thing to a String.
 *
 * @since 6.1
 */
class NucleotideSeqCharSequence implements CharSequence{

    private final NucleotideSequence delegate;

    public NucleotideSeqCharSequence(NucleotideSequence delegate) {
        this.delegate = delegate;
    }
    public NucleotideSeqCharSequence(NucleotideSequence delegate, Range range) {
        this.delegate = delegate.toBuilder(range)
                .turnOffDataCompression(true)
                .build();
    }

    @Override
    public IntStream chars() {
        class CharWrapperIterator implements PrimitiveIterator.OfInt {
            final Iterator<Nucleotide> iter = delegate.iterator();
            @Override
            public int nextInt() {
                return iter.next().getCharacter();
            }

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }
        }
        return StreamSupport.intStream(() ->
                        Spliterators.spliterator(
                                new CharWrapperIterator(),
                                length(),
                                Spliterator.ORDERED),
                Spliterator.SUBSIZED | Spliterator.SIZED | Spliterator.ORDERED,
                false);
    }


    @Override
    public int length() {
        return (int) delegate.getLength();
    }

    @Override
    public char charAt(int index) {
       return delegate.get(index).getCharacter();

    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new NucleotideSeqCharSequence(delegate, Range.of(start, end-1));
    }
}
