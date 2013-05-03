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
package org.jcvi.jillion.internal.trace.chromat.ztr.data;

/**
 * {@code DataHeader} contains the various supported ZTR 1.2
 * Header byte values which determines how a data block is encoded.
 * @author dkatzel
 *
 *
 */
public final class DataHeader {

    
    /**
     * Data header value specifying that this data block will not be encoded (raw).
     */
    public static final Byte RAW = Byte.valueOf((byte)0);
    /**
     * Data header value specifying that this data block
     *  will be encoded in run length encoding format.
     */
    public static final Byte RUN_LENGTH_ENCODED = Byte.valueOf((byte)1);
    /**
     * Data header value specifying that this data block
     *  will be compressed using zlib (probably Huffman).
     */
    public static final Byte ZLIB_ENCODED = Byte.valueOf((byte)2);
    /**
     * Data header value specifying that this data block will be encoded
     * as a (possibly multi level) delta encoding of byte values.
     */
    public static final Byte BYTE_DELTA_ENCODED = Byte.valueOf((byte)64);
    /**
     * Data header value specifying that this data block will be encoded
     * as a (possibly multi level) delta encoding of short values.
     */
    public static final Byte SHORT_DELTA_ENCODED = Byte.valueOf((byte)65);
    /**
     * Data header value specifying that this data block will be encoded
     * as a (possibly multi level) delta encoding of int values.
     */
    public static final Byte INTEGER_DELTA_ENCODED = Byte.valueOf((byte)66);
    /**
     * Data header value specifying that this data block will be encoded
     * to convert a signed short value into a byte value(assuming it fits).
     */
    public static final Byte SHRINK_SHORT_TO_BYTE_ENCODED = Byte.valueOf((byte)70);
    /**
     * Data header value specifying that this data block will be encoded
     * to convert a signed int value into a byte value(assuming it fits).
     */
    public static final Byte SHRINK_INTEGER_TO_BYTE_ENCODED = Byte.valueOf((byte)71);
    /**
     * Data header value specifying that this data block will be encoded
     * using a computing most frequent follow symbol table.
     */
    public static final Byte FOLLOW_DATA_ENCODED = Byte.valueOf((byte)72);
    
    private DataHeader(){
    	//can not instantiate
    }
    
}
