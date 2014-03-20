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
package org.jcvi.jillion.internal.trace.chromat.ztr;

import java.util.Arrays;

/**
 * Utility methods for manipulating ZTR data.
 * @author dkatzel
 *
 *
 */
public final class ZTRUtil {
    
    /**
     * ZTR magic number to let us know that 
     * this is a valid ztr file.
     */
    private static final byte[] ZTR_MAGIC_NUMBER = 
        new byte[]{(byte)0xAE,(byte)0x5A,(byte)0x54,(byte)0x52,
                (byte)0x0D,(byte)0x0A,(byte)0x1A,(byte)0x0A,};
    /**
     * private construtor so
     * no one can create one.
     *
     */
    private ZTRUtil(){
    	//can not instantiate
    }
    
    public static final byte[] getMagicNumber(){
        byte[] ret = new byte[ZTR_MAGIC_NUMBER.length];
        System.arraycopy(ZTR_MAGIC_NUMBER, 0, ret, 0, ret.length);
        return ret;
    }
    
    public static boolean isMagicNumber(byte[] magicNumber){
    	if(magicNumber.length < ZTR_MAGIC_NUMBER.length){
	    	byte[] subSet = new byte[magicNumber.length];
	    	System.arraycopy(ZTR_MAGIC_NUMBER, 0, subSet, 0, subSet.length);
	        return Arrays.equals(subSet, magicNumber);
    	}
    	return Arrays.equals(ZTR_MAGIC_NUMBER, magicNumber);
    }
    /**
     * Utility method to convert a 4 byte array into
     * a long value.
     * @param byteArray 4 byte array that represents a long value.
     * @return the value asa type long.
     */
    public static long readInt(byte[] byteArray){
        long longValue = 0;
        longValue |= byteArray[0] & 0xFF;
        longValue <<= 8;
        longValue |= byteArray[1] & 0xFF;
        longValue <<= 8;
        longValue |= byteArray[2] & 0xFF;
        longValue <<= 8;
        longValue |= byteArray[3] & 0xFF;
        return longValue;
    }

    
    
}
