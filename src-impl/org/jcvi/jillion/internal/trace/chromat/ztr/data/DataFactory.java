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
 * Created on Oct 27, 2006
 *
 * @author dkatzel
 */
package org.jcvi.jillion.internal.trace.chromat.ztr.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.internal.core.seq.trace.sanger.chromat.ztr.data.Data;
import org.jcvi.jillion.trace.TraceDecoderException;



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
        map.put(DataHeader.RAW, RawData.INSTANCE);
        map.put(DataHeader.RUN_LENGTH_ENCODED, RunLengthEncodedData.INSTANCE);
        map.put(DataHeader.ZLIB_ENCODED, ZLibData.INSTANCE);
        map.put(DataHeader.BYTE_DELTA_ENCODED, DeltaEncodedData.BYTE);
        map.put(DataHeader.SHORT_DELTA_ENCODED, DeltaEncodedData.SHORT);
        map.put(DataHeader.INTEGER_DELTA_ENCODED, DeltaEncodedData.INTEGER);
        map.put(DataHeader.SHRINK_SHORT_TO_BYTE_ENCODED, ShrinkToEightBitData.SHORT_TO_BYTE);
        map.put(DataHeader.SHRINK_INTEGER_TO_BYTE_ENCODED, ShrinkToEightBitData.INTEGER_TO_BYTE);
        map.put(DataHeader.FOLLOW_DATA_ENCODED, FollowData.INSTANCE);
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
