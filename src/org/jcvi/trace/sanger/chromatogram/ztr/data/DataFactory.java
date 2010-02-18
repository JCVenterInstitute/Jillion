/*
 * Created on Oct 27, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.trace.TraceDecoderException;



/**
 * <code>DataFactory</code> is a factory
 * that determines which implementation of {@link Data}
 * is needed to decode the ZTR DataFormat.
 * @author dkatzel
 *
 *
 */
public final class DataFactory {
    /**
     * Map of {@link Data} implementations keyed
     * by their format header byte value
     * in the ZTR spec.
     */
    private static final  Map<Byte, Data> DATA_MAP;
    static{
        Map<Byte, Data> map = new HashMap<Byte, Data>();
        map.put(Byte.valueOf((byte)0), new RawData());
        map.put(Byte.valueOf((byte)1), new RunLengthEncodedData());
        map.put(Byte.valueOf((byte)2), new ZLibData());
        map.put(Byte.valueOf((byte)64), new Delta8Data());
        map.put(Byte.valueOf((byte)65), new Delta16Data());
        map.put(Byte.valueOf((byte)66), new Delta32Data());
        map.put(Byte.valueOf((byte)70), new SixteenBitToEightBitData());
        map.put(Byte.valueOf((byte)71), new ThirtyTwoToEightBitData());
        map.put(Byte.valueOf((byte)72), new FollowData());
        DATA_MAP = Collections.unmodifiableMap(map);
    }
    /**
     * private constructor.
     */
    private DataFactory(){}
    /**
     * Read the Data Format from the given byte array
     * and determine which Data Format Implementation is required
     * to parse the given byte array.
     * @param data the data to decode.
     * @return the {@link Data} instance used to read the format.
     * @throws TraceDecoderException if <code>data</code> is null 
     * or has a length of <code>0</code>.
     */
    public static Data getDataImplementation(byte[] data)
            throws TraceDecoderException {
        // first byte is the data format (raw, run-length encoded, zipped etc)

        if (data ==null || data.length < 1) {
            throw new TraceDecoderException("can not parse data format");
        }
        final Byte format = Byte.valueOf(data[0]);
        final Data dataImpl = DATA_MAP.get(format);
        if(dataImpl ==null){
            throw new TraceDecoderException("format not supported : " + format);

        }
        return dataImpl;

    }
}
