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

package org.jcvi.io;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.jcvi.io.IOUtil.ReadResults;
import org.jcvi.testUtil.EasyMockUtil;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestIOUtil_safeBlockingRead {

    byte[] array = new byte[]{20,15,30,40};

    @Test
    public void valid() throws IOException{
        InputStream in = new ByteArrayInputStream(array);
        byte[] actualArray = new byte[array.length];
        ReadResults results = IOUtil.safeBlockingRead(in, actualArray);
        assertTrue(Arrays.equals(array, actualArray));
        assertFalse(results.isEndOfFileReached());
        assertEquals(array.length, results.getNumberOfBytesRead());
    }
    @Test
    public void readEOF() throws IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        int lastIndex = array.length;
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(lastIndex+1)))
                    .andAnswer(EasyMockUtil.writeArrayToInputStream(array));
        
        expect(mockInputStream.read(isA(byte[].class), eq(lastIndex), eq(1))).andReturn(-1);
        replay(mockInputStream);
        byte[] actualArray = new byte[lastIndex+1];
        ReadResults results = IOUtil.safeBlockingRead(mockInputStream, actualArray);
        assertTrue(results.isEndOfFileReached());
        assertEquals(lastIndex, results.getNumberOfBytesRead());
        for(int i=0; i<lastIndex; i++){
            assertEquals(array[i], actualArray[i]);
        }
        assertEquals(0, actualArray[lastIndex]);

    }
}
