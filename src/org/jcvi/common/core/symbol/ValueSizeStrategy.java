/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.symbol;

import java.nio.ByteBuffer;
/**
 * <code>ValueSizeStrategy</code> is a strategy pattern
 * implementation to abstract away the number of bytes
 * read from/written to a {@link ByteBuffer}.  This simplifies algorithms
 * and allows read/write operations to be written once and re-used by
 * different implementations that need to read/write different sized data.
 * @author dkatzel
 *
 *
 */
enum ValueSizeStrategy {
    /**
     * Read and write a single byte at a time.
     */
    BYTE(1){
        /**
         * get the next (unsigned) byte from the buffer.
         * @param buf the buffer to read the byte from.
         * @return an unsigned byte value as an int.
         */
        @Override
        public long getNext(ByteBuffer buf) {
            return buf.get();
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
    },
    /**
     * Read and write a short int (2 bytes) at a time.
     */
    SHORT(2){
        /**
         * get the next short from the buffer.
         * @param buf the buffer to read the byte from.
         * @return a short value as an int.
         */
        @Override
        public long getNext(ByteBuffer buf) {
            return buf.getShort();
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
    },
    /**
     * Read and write a 4 byte int at a time.
     */
    INT(4){
        @Override
        public long getNext(ByteBuffer buf) {
            return buf.getInt();
        }
        @Override
        public void put(long value, ByteBuffer buf) {
            buf.putInt((int)value);
        }
    },
    /**
     * Read and write 8 byte integers (longs).
     */
    LONG(8){
        @Override
        public long getNext(ByteBuffer buf) {
            return buf.getLong();
        }

        @Override
        public void put(long value, ByteBuffer buf) {
            buf.putLong(value);
            
        }
    }
    ;
    
    private final int numberOfBytesPerValue;
    
    private ValueSizeStrategy(int numberOfBytesPerValue) {
        this.numberOfBytesPerValue = numberOfBytesPerValue;
    }
    /**
     * Gets the next value from the given buffer.
     * @param buf the ByteBuffer to read from.
     * @return the value read from the buffer as an long.
     */
    abstract long getNext(ByteBuffer buf);
    /**
     * Puts the given value into the buffer. If this
     * implementation reads/writes data smaller than an long,
     * the passed in value will be cast down to the appropriate size.
     * @param value the value to write (must not be larger than 
     * the MAX_SIZE of the implementation value size.
     * @param buf the buffer to write to.
     * 
     */
    abstract void put(long value, ByteBuffer buf);
    /**
     * Get the number of bytes read or written to 
     * during each get or put operation.
     * @return 1,2,4, or 8.
     */
    public int numberOfBytesPerValue() {
        return numberOfBytesPerValue;
    }
}
