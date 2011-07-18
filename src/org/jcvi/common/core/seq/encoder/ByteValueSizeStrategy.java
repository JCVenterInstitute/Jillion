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
package org.jcvi.common.core.seq.encoder;

import java.nio.ByteBuffer;
/**
 * <code>ByteValueSizeStrategy</code> is an implementation
 * of {@link ValueSizeStrategy} that reads/writes single bytes.
 * @author dkatzel
 *
 */
public class ByteValueSizeStrategy implements ValueSizeStrategy<Byte> {
    /**
     * get the next (unsigned) byte from the buffer.
     * @param buf the buffer to read the byte from.
     * @return an unsigned byte value as an int.
     */
    @Override
    public Byte getNext(ByteBuffer buf) {
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
    
    @Override
    public int numberOfBytesPerValue() {
        return 1;
    }
}
