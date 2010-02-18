/*
 * Created on Oct 27, 2006
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr;

/**
 * Utility methods for manipulating ZTR data.
 * @author dkatzel
 *
 *
 */
public final class ZTRUtil {
    /**
     * private construtor so
     * no one can create one.
     *
     */
    private ZTRUtil(){}

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
