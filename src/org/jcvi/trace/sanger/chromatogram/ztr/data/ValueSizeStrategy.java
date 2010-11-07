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
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.nio.ByteBuffer;

import org.jcvi.io.IOUtil;
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
public enum ValueSizeStrategy {
	
	BYTE{
		/**
	     * get the next (unsigned) byte from the buffer.
	     * @param buf the buffer to read the byte from.
	     * @return an unsigned byte value as an int.
	     */
	    @Override
	    public int getNext(ByteBuffer buf) {
	        return IOUtil.convertToUnsignedByte(buf.get());
	    }
	    /**
	     * puts the given byte value into the given buffer.
	     * @param value the value to write (must be able to be cast to a <code>byte</code>)
	     * @param buf the Buffer to write to.
	     */
	    @Override
	    public void put(int value, ByteBuffer buf) {
	        buf.put((byte)value);

	    }    
	},
	SHORT{
		/**
	     * get the next short from the buffer.
	     * @param buf the buffer to read the byte from.
	     * @return a short value as an int.
	     */
	    @Override
	    public int getNext(ByteBuffer buf) {
	        return IOUtil.convertToUnsignedShort(buf.getShort());
	    }
	    /**
	     * puts the given short value into the given buffer.
	     * @param value the value to write (must be able to be cast to a <code>short</code>)
	     * @param buf the Buffer to write to.
	     */
	    @Override
	    public void put(int value, ByteBuffer buf) {
	        buf.putShort((short)value);

	    }
	},
	INTEGER{
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
	};
    /**
     * Gets the next value from the given buffer.
     * @param buf the ByteBuffer to read from.
     * @return the value read from the buffer as an int.
     */
    abstract int getNext(ByteBuffer buf);
    /**
     * puts the given value into the buffer. If this
     * implementation reads/writes data smaller than an int,
     * the passed in value will be cast down to the appropriate size.
     * @param value the value to write (must not be larger than 
     * the MAX_SIZE of the implementation value size.
     * @param buf the buffer to write to.
     */
    abstract void put(int value, ByteBuffer buf);
}
