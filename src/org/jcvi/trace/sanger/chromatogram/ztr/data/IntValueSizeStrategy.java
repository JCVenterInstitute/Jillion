/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

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
    public int getNext(ByteBuffer buf) {
        return buf.getInt();
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public void put(int value, ByteBuffer buf) {
        buf.putInt(value);
    }

}
