/*
 * Created on Aug 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

import java.nio.ByteBuffer;

public class LongValueSizeStrategy implements ValueSizeStrategy {

    @Override
    public long getNext(ByteBuffer buf) {
        return buf.getLong();
    }

    @Override
    public void put(long value, ByteBuffer buf) {
        buf.putLong(value);
        
    }
    @Override
    public int numberOfBytesPerValue() {
        return 8;
    }
}
