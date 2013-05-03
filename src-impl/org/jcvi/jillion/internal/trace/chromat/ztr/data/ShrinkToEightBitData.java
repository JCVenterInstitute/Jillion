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
 * Created on Dec 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.ztr.data;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.jillion.internal.core.io.ValueSizeStrategy;
import org.jcvi.jillion.internal.core.seq.trace.sanger.chromat.ztr.data.Data;
import org.jcvi.jillion.trace.TraceEncoderException;

/**
 * <code>AbstractToEightBitData</code> is an abstract
 * implementation of the ZTR data formats "X to 8 bits".
 * these formats try to store a number in only 8 bits.  If the number is too
 * large to fit in only 8 bits, then a guard value of -128 followed by the
 * actual value is used.  These formats are work well following a delta
 * format since after a delta format, most values can fit in a single byte.
 * @author dkatzel
 */
public enum ShrinkToEightBitData implements Data {
    /**
     * {@code SHORT_TO_BYTE} is the implementation 
     * of the ZTR 16 bit to 8 bit conversion format.
     */
    SHORT_TO_BYTE(ValueSizeStrategy.SHORT, 2,DataHeader.SHRINK_SHORT_TO_BYTE_ENCODED ),
    /**
     * {@code INTEGER_TO_BYTE} is the implementation
     *  of the ZTR 32 bit to 8 bit conversion format.
     */
    INTEGER_TO_BYTE(ValueSizeStrategy.INTEGER,4, DataHeader.SHRINK_INTEGER_TO_BYTE_ENCODED)
      ;
    /**
     * guard value which tells decoder that the the following
     * byte values are are number larger than can fit in a single byte.
     */
    private static final byte GUARD = -128;
    private final ValueSizeStrategy valueSizeStrategy;
    private final byte formatByte;
    private final int numberOfBytesPerElement;
    /**
     * Constructor.
     * @param valueSizeStrategy the implementation 
     * of {@link ValueSizeStrategy} to use.
     */
    private ShrinkToEightBitData(ValueSizeStrategy valueSizeStrategy, int numberOfBytesPerElement, byte formatByte){
        this.valueSizeStrategy = valueSizeStrategy;
        this.formatByte = formatByte;
        this.numberOfBytesPerElement = numberOfBytesPerElement;
    }
    
    private boolean isGuard(byte value){
        return value == GUARD;
    }
    
    private ByteBuffer getInput(byte[] data) {
        ByteBuffer in = ByteBuffer.allocate(data.length-1);
        in.put(data, 1, data.length-1);
        in.flip();
        return in;
    }
    
    protected byte[] toByteArray(ByteBuffer out) {
        //we need to return a byte array
        //and byteBuffer.array() returns the full
        //backing array including space we didn't use
        //so make another byteArray with only
        //the data we need.
        out.flip();
        int size = out.remaining();
        ByteBuffer result = ByteBuffer.allocate(size);
        result.put(out);
        return result.array();
    }
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public byte[] parseData(byte[] data){
        ByteBuffer in = getInput(data);
        //data.length -1 to ignore format byte
        ByteBuffer result = ByteBuffer.allocate(getMaxPossibleDecodedSize(data.length -1));
        while(in.hasRemaining()){
            byte value = in.get();
            if(isGuard(value)){
                valueSizeStrategy.put(result, valueSizeStrategy.getNext(in));                
            }
            else{
                valueSizeStrategy.put(result, value);   
            }
        }
        return toByteArray(result);
    }
    /**
     * Gets the maximum possible size the decoded data can be for
     * this format.
     * @param numberOfEncodedBytes the number of encoded bytes of data.
     * @return the length of the largest possible decoded data.
     */
    protected int getMaxPossibleDecodedSize(int numberOfEncodedBytes){
    	return numberOfEncodedBytes*numberOfBytesPerElement;
    }

	/* (non-Javadoc)
	 * @see org.jcvi.trace.sanger.chromatogram.ztr.data.Data#encodeData(byte[])
	 */
	@Override
	public byte[] encodeData(byte[] data) throws TraceEncoderException {
		ByteBuffer encodedBuffer = ByteBuffer.allocate(4*data.length);
		encodedBuffer.put(formatByte);
		ByteBuffer inputBuffer = ByteBuffer.wrap(data);
		while(inputBuffer.hasRemaining()){
			byte[] next = getNext(inputBuffer);
			 int nextAsInt = new BigInteger(next).intValue();
			 if(nextAsInt <=127 && nextAsInt >= -127){
				 encodedBuffer.put((byte)nextAsInt);
			 }else{
				 encodedBuffer.put(GUARD);
				 encodedBuffer.put(next);
			 }
			
		}
		encodedBuffer.flip();
		return Arrays.copyOfRange(encodedBuffer.array(), 0, encodedBuffer.limit());
	}

	protected byte[] getNext(ByteBuffer inputBuffer){
		byte[] array = new byte[numberOfBytesPerElement];
		inputBuffer.get(array);
		return array;
	}
	/**
	 * This returns the same result as {@link #encodeData(byte[])}
	 * the optional parameter is ignored. 
	 */
	@Override
	public byte[] encodeData(byte[] data, byte ignored)
			throws TraceEncoderException {
		return encodeData(data);
	}


}
