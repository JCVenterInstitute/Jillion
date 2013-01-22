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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Nov 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.jcvi.jillion.core.io.IOUtil;
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
    public void smallUnsignedShortAddPadding() throws IOException{
        int unsignedShort = 3;
        byte[] byteArray = IOUtil.convertUnsignedShortToByteArray(unsignedShort);
        assertArrayEquals(new byte[]{0,3}, byteArray);
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
