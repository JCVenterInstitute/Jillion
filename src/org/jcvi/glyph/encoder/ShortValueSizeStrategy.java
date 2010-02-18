/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

import java.nio.ByteBuffer;

import org.jcvi.io.IOUtil;
/**
 * <code>ShortValueSizeStrategy</code> is an implementation
 * of {@link ValueSizeStrategy} that reads/writes single bytes.
 * @author dkatzel
 *
 */
public class ShortValueSizeStrategy implements ValueSizeStrategy {
    /**
     * get the next short from the buffer.
     * @param buf the buffer to read the byte from.
     * @return a short value as an int.
     */
    @Override
    public long getNext(ByteBuffer buf) {
        return IOUtil.convertToUnsignedShort(buf.getShort());
    }
    /**
     * puts the given short value into the given buffer.
     * @param value the value to write (must be able to be cast to a <code>short</code>)
     * @param buf the Buffer to write to.
     */
    @Override
    public void put(long value, ByteBuffer buf) {
        buf.putShort((short)value);
    }
    @Override
    public int numberOfBytesPerValue() {
        return 2;
    }
    

}
