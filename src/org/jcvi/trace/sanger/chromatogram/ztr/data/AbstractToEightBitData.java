/*
 * Created on Dec 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.nio.ByteBuffer;

/**
 * <code>AbstractToEightBitData</code> is an abstract
 * implementation of the ZTR data formats "X to 8 bits".
 * these formats try to store a number in only 8 bits.  If the number is too
 * large to fit in only 8 bits, then a guard value of -128 followed by the
 * actual value is used.  These formats are work well following a delta
 * format since after a delta format, most values can fit in a single byte.
 * @author dkatzel
 */
public abstract class AbstractToEightBitData implements Data {
    /**
     * guard value which tells decoder that the the following
     * byte values are are number larger than can fit in a single byte.
     */
    private static final byte GUARD = -128;
    private final ValueSizeStrategy valueSizeStrategy;
    /**
     * Constructor.
     * @param valueSizeStrategy the implementation 
     * of {@link ValueSizeStrategy} to use.
     */
    public AbstractToEightBitData(ValueSizeStrategy valueSizeStrategy){
        this.valueSizeStrategy = valueSizeStrategy;
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
                valueSizeStrategy.put(valueSizeStrategy.getNext(in), result);                
            }
            else{
                valueSizeStrategy.put(value, result);   
            }
        }
        return toByteArray(result);
    }
    /**
     * Gets the maximum possible size the decoded dat can be for
     * this format.
     * @param numberOfEncodedBytes the number of encoded bytes of data.
     * @return the length of the largest possible decoded data.
     */
    protected abstract int getMaxPossibleDecodedSize(int numberOfEncodedBytes);


}
