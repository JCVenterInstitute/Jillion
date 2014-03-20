/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Dec 30, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.core.io;

import java.nio.ByteBuffer;

import org.jcvi.jillion.core.io.IOUtil;


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
	/**
	 * No values can be read or written.  Takes up zero bytes.
	 * Will throw {@link IllegalArgumentException}s
	 * if {@link #getNext(ByteBuffer)} or {@link #put(ByteBuffer, int)}
	 * are called.
	 */
	NONE{
		/**
		 * Not allowed, will throw {@link IllegalArgumentException}
		 * if called.
		 * <p/>
		 * {@inheritDoc}
		 * @throws IllegalArgumentException always
		 */
		@Override
		public int getNext(ByteBuffer buf) {
			throw new IllegalArgumentException("can not call getNext()");
		}
		/**
		 * Not allowed, will throw {@link IllegalArgumentException}
		 * if called.
		 * <p/>
		 * {@inheritDoc}
		 * @throws IllegalArgumentException always
		 */
		@Override
		public void put(ByteBuffer buf, int value) {
			throw new IllegalArgumentException("can not call put()");
			
		}

		@Override
		public int getNumberOfBytesPerValue() {
			return 0;
		}
		
	},
	BYTE{
		@Override
		public int getNumberOfBytesPerValue() {
			return 1;
		}
		/**
	     * get the next (unsigned) byte from the buffer.
	     * @param buf the buffer to read the byte from.
	     * @return an unsigned byte value as an int.
	     */
	    @Override
	    public int getNext(ByteBuffer buf) {
	        return IOUtil.toUnsignedByte(buf.get());
	    }
	    /**
	     * puts the given byte value into the given buffer.
	     * @param buf the Buffer to write to.
	     * @param value the value to write (must be able to be cast to a <code>byte</code>)
	     */
	    @Override
	    public void put(ByteBuffer buf, int value) {
	        buf.put((byte)value);

	    }    
	},
	SHORT{
		
		@Override
		public int getNumberOfBytesPerValue() {
			return 2;
		}
		/**
	     * get the next short from the buffer.
	     * @param buf the buffer to read the byte from.
	     * @return a short value as an int.
	     */
	    @Override
	    public int getNext(ByteBuffer buf) {
	        return IOUtil.toUnsignedShort(buf.getShort());
	    }
	    /**
	     * puts the given short value into the given buffer.
	     * @param buf the Buffer to write to.
	     * @param value the value to write (must be able to be cast to a <code>short</code>)
	     */
	    @Override
	    public void put(ByteBuffer buf, int value) {
	        buf.putShort((short)value);

	    }
		
	},
	INTEGER{
		@Override
		public int getNumberOfBytesPerValue() {
			return 4;
		}
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
	    public void put(ByteBuffer buf, int value) {
	        buf.putInt(value);
	    }
	};
    /**
     * Gets the next value from the given buffer.
     * @param buf the ByteBuffer to read from.
     * @return the value read from the buffer as an int.
     */
	public abstract int getNext(ByteBuffer buf);
    /**
     * puts the given value into the buffer. If this
     * implementation reads/writes data smaller than an int,
     * the passed in value will be cast down to the appropriate size.
     * @param buf the buffer to write to.
     * @param value the value to write (must not be larger than 
     * the MAX_SIZE of the implementation value size.
     */
    public abstract void put(ByteBuffer buf, int value);
    /**
     * Get the number of bytes used to store
     * each value in a buffer.
     * @return the number of bytes used; never < 1.
     */
    public abstract int getNumberOfBytesPerValue();
    /**
     * Get the {@link ValueSizeStrategy} implementation
     * given the largest possible input value.
     * @param largestValue the largest input value 
     * that will be written to a buffer.
     * @return the {@link ValueSizeStrategy} implementation
     * that can read and write up to and including
     * the given largestValue but use the least amount
     * of bytes to do it.
     */
    public static ValueSizeStrategy getStrategyFor(int largestValue){
		 //used unsigned values for twice the storage space
		 //since these values will always be positive.
		 if(largestValue <= 0xFF){
			 return BYTE;
		 }
		 if(largestValue <= 0xFFFF){
			 return SHORT;
		 }
		 return INTEGER;
	 }
}
