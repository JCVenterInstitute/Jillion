/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

import java.nio.ByteBuffer;
/**
 * <code>IntValueSizeStrategy</code> is an implementation
 * of {@link ValueSizeStrategy} that reads/writes single bytes.
 * @author dkatzel
 *
 */
public class IntValueSizeStrategy implements ValueSizeStrategy {
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public long getNext(ByteBuffer buf) {
        return buf.getInt();
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public void put(long value, ByteBuffer buf) {
        buf.putInt((int)value);
    }
    @Override
    public int numberOfBytesPerValue() {
        return 4;
    }
}
