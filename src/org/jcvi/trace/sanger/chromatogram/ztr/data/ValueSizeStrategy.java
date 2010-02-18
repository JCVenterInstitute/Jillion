/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.nio.ByteBuffer;
/**
 * <code>ValueSizeStrategy</code> is a strategy pattern
 * implementation to abstract away the number of bytes
 * read/written to a {@link ByteBuffer}.  This simplifies algorithms
 * and allows read/write operations to be written once and re-used by
 * different implementations that need to read/write different sized data.
 * @author dkatzel
 *
 *
 */
public interface ValueSizeStrategy {
    /**
     * Gets the next value from the given buffer.
     * @param buf the ByteBuffer to read from.
     * @return the value read from the buffer as an int.
     */
    int getNext(ByteBuffer buf);
    /**
     * puts the given value into the buffer. If this
     * implementation reads/writes data smaller than an int,
     * the passed in value will be cast down to the appropriate size.
     * @param value the value to write (must not be larger than 
     * the MAX_SIZE of the implementation value size.
     * @param buf the buffer to write to.
     */
    void put(int value, ByteBuffer buf);
}
