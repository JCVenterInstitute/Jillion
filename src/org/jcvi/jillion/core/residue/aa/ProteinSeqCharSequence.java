package org.jcvi.jillion.core.residue.aa;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.io.StreamUtil;

import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Creates a CharSequence efficiently from a {@link ProteinSequence}
 * without having to convert the whole thing to a String.
 *
 * @since 6.1
 */
class ProteinSeqCharSequence implements CharSequence{

    private final ProteinSequence delegate;

    public ProteinSeqCharSequence(ProteinSequence delegate) {
        this.delegate = delegate;
    }
    public ProteinSeqCharSequence(ProteinSequence delegate, Range range) {
        this.delegate = delegate.toBuilder(range)
                .turnOffDataCompression(true)
                .build();
    }

    @Override
    public IntStream chars() {
        class CharWrapperIterator implements PrimitiveIterator.OfInt {
            final Iterator<AminoAcid> iter = delegate.iterator();
            @Override
            public int nextInt() {
                return iter.next().asChar();
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
       return delegate.get(index).asChar();

    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new ProteinSeqCharSequence(delegate, Range.of(start, end-1));
    }
}
