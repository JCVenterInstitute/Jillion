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
 * Created on Oct 31, 2006
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.ztr.data;

import java.nio.ByteBuffer;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.seq.trace.sanger.chromat.ztr.data.Data;
import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.TraceEncoderException;


/**
 * The <code>FollowData</code> implements the follow predictor data
 * format.  Each byte value has a "follow byte" which is the most
 * likely byte to appear after it in the data. Each encoded value
 * is the difference between the actual data value and the predicted "followed"
 * value.  The technique removes some non-randomness in the input data 
 * and allows for better compression.
 * @author dkatzel
 * @see <a href="http://staden.sourceforge.net/ztr.html">ZTR SPEC v1.2</a>
 */
public enum FollowData implements Data {
    /**
     * Singleton instance of FollowData.
     */
    INSTANCE;
    /**
     * This is the index where the actual encoded values start.
     */
    private static int DATA_START_POSITION = 257;
    /**
     * 
    * {@inheritDoc}
     */
    @Override
    public byte[] parseData(byte[] data) throws TraceDecoderException {
        int uncompressedLength = data.length-DATA_START_POSITION;        
        byte[] follow = createFollowArray(data);
        ByteBuffer compressedData = getCompressedData(data, uncompressedLength);
        ByteBuffer uncompressedData = ByteBuffer.allocate(uncompressedLength);
        //prev is kept as an int to avoid java signed byte issues
        int prev = IOUtil.toUnsignedByte(compressedData.get());
        uncompressedData.put((byte)prev);
        while(compressedData.hasRemaining()){
            prev = IOUtil.toUnsignedByte((byte)(follow[prev] - compressedData.get()));
            uncompressedData.put((byte)prev);   
            
        }
        return uncompressedData.array();
    }

    private ByteBuffer getCompressedData(byte[] data, int uncompressedLength) {
        ByteBuffer compressedData = ByteBuffer.allocate(uncompressedLength);
        compressedData.put(data,DATA_START_POSITION, data.length-DATA_START_POSITION);
        compressedData.flip();
        return compressedData;
    }
    /**
     * The first 256 bytes of the data are the follow data.
     * @param data the encoded data where the follow data is located.
     * @return a byte array containing the follow data look up
     * such that follow[i] = the predicted value that will come
     * after a byte with the value <code>i</code>.
     */
    private byte[] createFollowArray(byte[] data) {
        //create next array
        byte next[] = new byte[DATA_START_POSITION-1];
        //load next array
        for(int i=1; i< DATA_START_POSITION; i++){
            next[i-1]= data[i];
        }
        return next;
    }

	@Override
	public byte[] encodeData(byte[] data) throws TraceEncoderException {
		ByteBuffer result = ByteBuffer.allocate(data.length+256+1);
		result.put(DataHeader.FOLLOW_DATA_ENCODED);
		byte[] followArray = generateFollowArray(data);
		result.put(followArray);
		//put 1st byte without follow encoding
		result.put(data[0]);
		//follow encode the rest
		for(int i=1; i<data.length; i++){
			int prev = IOUtil.toUnsignedByte(data[i-1]);
			int current = IOUtil.toUnsignedByte(data[i]);
			int nextFollowValue = IOUtil.toUnsignedByte(followArray[prev]) - current;
			result.put(IOUtil.toSignedByte(nextFollowValue));
		}
		result.flip();
		return result.array();
	}

	private byte[] generateFollowArray(byte[] data) {
		int[][] frequencyMatrix = new int[256][256];
		int[] countsArray = new int[256];
		byte[] followArray = new byte[256];
		
		for(int i=0; i< data.length-1; i++){
			int current = IOUtil.toUnsignedByte(data[i]);
			int next = IOUtil.toUnsignedByte(data[i+1]);
			int frequencyCount = ++frequencyMatrix[current][next];
			if(frequencyCount > countsArray[current]){
				countsArray[current]=frequencyCount;
				followArray[current]=IOUtil.toSignedByte(next);
			}
		}
		return followArray;
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
