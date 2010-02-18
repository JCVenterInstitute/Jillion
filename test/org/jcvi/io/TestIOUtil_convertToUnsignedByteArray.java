/*
 * Created on Nov 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestIOUtil_convertToUnsignedByteArray {

    @Test
    public void unsignedByte() throws IOException{
        short unsignedByte = 255;
        byte[] byteArray = IOUtil.convertUnsignedByteToByteArray(unsignedByte);
        short reconvertedUnsignedByte =IOUtil.readUnsignedByte(new ByteArrayInputStream(byteArray));
        assertEquals(reconvertedUnsignedByte, unsignedByte);
    }
    @Test
    public void unsignedShort() throws IOException{
        int unsignedShort = Short.MAX_VALUE+1;
        byte[] byteArray = IOUtil.convertUnsignedShortToByteArray(unsignedShort);
        int reconvertedUnsignedShort =IOUtil.readUnsignedShort(new ByteArrayInputStream(byteArray));
        assertEquals(reconvertedUnsignedShort, unsignedShort);
    }
    
    @Test
    public void unsignedInt() throws IOException{
        long unsignedInt = Integer.MAX_VALUE+1L;
        byte[] byteArray = IOUtil.convertUnsignedIntToByteArray(unsignedInt);        
        long reconvertedUnsignedShort =IOUtil.readUnsignedInt(new ByteArrayInputStream(byteArray));
        assertEquals(reconvertedUnsignedShort, unsignedInt);
    }
    @Test
    public void unsignedLong() throws IOException{
        //Long.MAX_VALUE +1
        BigInteger unsignedLong = new BigInteger("9223372036854775808");
        byte[] byteArray = IOUtil.convertUnsignedLongToByteArray(unsignedLong);
        BigInteger reconvertedUnsignedLong =IOUtil.readUnsignedLong(new ByteArrayInputStream(byteArray));
        assertEquals(reconvertedUnsignedLong, unsignedLong);
    }
    @Test
    public void smallunsignedLong() throws IOException{

        BigInteger unsignedLong = BigInteger.valueOf(1000L);
        byte[] byteArray = IOUtil.convertUnsignedLongToByteArray(unsignedLong);
        BigInteger reconvertedUnsignedLong =IOUtil.readUnsignedLong(new ByteArrayInputStream(byteArray));
        assertEquals(reconvertedUnsignedLong, unsignedLong);
    }
}
