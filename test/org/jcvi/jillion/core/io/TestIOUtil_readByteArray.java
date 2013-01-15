/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestIOUtil_readByteArray {

    byte[] array = new byte[]{20,15,30,40};

    @Test
    public void valid() throws IOException{
        InputStream in = new ByteArrayInputStream(array);
        byte[] actualArray = IOUtil.toByteArray(in, array.length);
        assertTrue(Arrays.equals(array, actualArray));
    }
    @Test
    public void didNotReadEnough() throws IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(array.length+1))).andReturn(array.length);
        expect(mockInputStream.read(isA(byte[].class), eq(array.length), eq(1))).andReturn(-1);
        replay(mockInputStream);
        try {
            IOUtil.toByteArray(mockInputStream, array.length+1);
            fail("if did not read exected length should throw IOException");
        } catch (IOException e) {
            String expectedMessage = "end of file after only "
                + array.length + " bytes read (expected "+ (array.length+1) +")";
            assertEquals(expectedMessage, e.getMessage());
        }

    }
}
