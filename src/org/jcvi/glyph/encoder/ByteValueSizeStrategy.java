/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

import java.nio.ByteBuffer;

import org.jcvi.io.IOUtil;
/**
 * <code>ByteValueSizeStrategy</code> is an implementation
 * of {@link ValueSizeStrategy} that reads/writes single bytes.
 * @author dkatzel
 *
 */
public class ByteValueSizeStrategy implements ValueSizeStrategy {
    /**
     * get the next (unsigned) byte from the buffer.
     * @param buf the buffer to read the byte from.
     * @return an unsigned byte value as an int.
     */
    @Override
    public long getNext(ByteBuffer buf) {
        return IOUtil.convertToUnsignedByte(buf.get());
    }
    /**
     * puts the given byte value into the given buffer.
     * @param value the value to write (must be able to be cast to a <code>byte</code>)
     * @param buf the Buffer to write to.
     */
    @Override
    public void put(long value, ByteBuffer buf) {
        buf.put((byte)value);
    }    
    
    @Override
    public int numberOfBytesPerValue() {
        return 1;
    }
}
