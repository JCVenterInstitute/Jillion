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
package org.jcvi.jillion.internal.trace.chromat.ztr.data;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.jillion.internal.core.io.ValueSizeStrategy;
import org.jcvi.jillion.internal.core.seq.trace.sanger.chromat.ztr.data.Data;
import org.jcvi.jillion.trace.TraceEncoderException;

/**
 * <code>AbstractDeltaData</code> is an abstract
 * implementation of the Delta encoded Data formats.
 * The Delta formats store the differences between successive
 * bytes instead of the actual values.  Different implementations
 * of <code>AbstractDeltaData</code> are used for the different
 * sizes of the encoded values.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 *
 */
public enum DeltaEncodedData implements Data {
	/**
	 * Implementation of the ZTR Delta8 Data Format which encodes the deltas between
	 * successive byte values.
	 * @author dkatzel
	 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
	 */
	BYTE(ValueSizeStrategy.BYTE, DataHeader.BYTE_DELTA_ENCODED),
	/**
	 * Implementation of the ZTR Delta16 Data Format
	 * which encodes the deltas between successive short values.
	 */
	SHORT(ValueSizeStrategy.SHORT, DataHeader.SHORT_DELTA_ENCODED),
	/**
	 * Implementation of the ZTR Delta32 Data Format 
	 * which encodes the deltas between successive int values.
	 * @author dkatzel
	 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
	 *
	 *
	 */
	INTEGER(ValueSizeStrategy.INTEGER, DataHeader.INTEGER_DELTA_ENCODED){
		/**
	     * 2 extra bytes of padding are needed to make 
	     * the total length divisible by 4.
	     */
	    @Override
	    protected final int getPaddingSize() {
	        return 2;
	    }
	};
	public enum Level{
		DELTA_LEVEL_1((byte)1),
		DELTA_LEVEL_2((byte)2),
		DELTA_LEVEL_3((byte)3),
		;
		private final byte level;

		private Level(byte level) {
			this.level = level;
		}

		/**
		 * @return the level
		 */
		public byte getLevel() {
			return level;
		}
		
	}
	public static final byte DELTA_LEVEL_1 = (byte)1;
	public static final byte DELTA_LEVEL_2 = (byte)2;
	public static final byte DELTA_LEVEL_3 = (byte)3;
	
	private final ValueSizeStrategy valueSizeStrategy;
	private final byte headerByte;
    private DeltaEncodedData(ValueSizeStrategy valueSizeStrategy, byte headerByte) {
		this.valueSizeStrategy = valueSizeStrategy;
		this.headerByte = headerByte;
	}
    
	/**
     * 
    * {@inheritDoc}
     */
    @Override
    public byte[] parseData(byte[] data){
        //read level
        int level = data[1];
        int startPosition = 2 +getPaddingSize();
        ByteBuffer compressed = ByteBuffer.allocate(data.length-startPosition);
        compressed.put(data, startPosition, data.length-startPosition);
        compressed.flip();
        ByteBuffer unCompressedData = ByteBuffer.allocate(compressed.capacity());
        DeltaStrategy.getStrategyFor(level).unCompress(compressed,valueSizeStrategy, unCompressedData);
        return unCompressedData.array();

    }
    
    /**
     * Some implementations may have additional
     * padding between the format byte and
     * when the actual data starts.  Usually this
     * is to needed to make the total length
     * of the data section divisible. Implementations
     * may override this method to return a different
     * padding size.
     * @return <code>0</code>
     */
    protected int getPaddingSize(){
        return 0;
    }
    /**
     * Same as {@link #encodeData(byte[], byte) encodeData(data,DEFAULT_LEVEL)}
     * @see #encodeData(byte[], byte)
     */
	@Override
	public byte[] encodeData(byte[] data) throws TraceEncoderException {
		return encodeData(data, Level.DELTA_LEVEL_1);
	}

	public byte[] encodeData(byte[] data, Level deltaLevel) throws TraceEncoderException {
		return encodeData(data, deltaLevel.level);
	}
	/**
	 * Encodes given byte array data as delta encoded using the 
	 * given level of deltas.
	 * @param data the data to delta encode.
	 * @param level the number of delta levels to use (1,2 or 3 permitted)
	 * @throws TraceEncoderException if there is a problem encoding the data.
	 * @return a new array containing the given input data as delta encoded.
	 * @throws IllegalArgumentException if the given level is not 1,2 or 3.
	 */
	public byte[] encodeData(byte[] data, byte level) throws TraceEncoderException {
		if(level<1 && level >3){
			throw new IllegalArgumentException("level must be between 1 and 3 inclusive: " + level);
		}
		ByteBuffer result = ByteBuffer.allocate(data.length +2+getPaddingSize());
		result.put(headerByte);
		result.put(level);
		for(int i=0; i< getPaddingSize(); i++){
			result.put((byte)0);
		}
		DeltaStrategy.getStrategyFor(level).compress(ByteBuffer.wrap(data),valueSizeStrategy, result );
		result.flip();
		return Arrays.copyOfRange(result.array(), 0, result.limit());
	}
}
