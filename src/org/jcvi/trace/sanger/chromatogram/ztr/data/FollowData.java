/*
 * Created on Oct 31, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.nio.ByteBuffer;

import org.jcvi.io.IOUtil;
import org.jcvi.trace.TraceDecoderException;


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
public class FollowData implements Data {
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
        int prev = IOUtil.convertToUnsignedByte(compressedData.get());
        uncompressedData.put((byte)prev);
        while(compressedData.hasRemaining()){
            prev = IOUtil.convertToUnsignedByte((byte)(follow[prev] - compressedData.get()));
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

}
