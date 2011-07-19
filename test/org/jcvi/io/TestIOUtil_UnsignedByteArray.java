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
 * Created on May 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.common.core.io.IOUtil;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestIOUtil_UnsignedByteArray {

    private short[] expectedShortArray = new short[]{255, 200, 0, 14, 128,56,254,127};
   
    @Test
    public void read() throws IOException{
        ByteBuffer buf = ByteBuffer.allocate(expectedShortArray.length);
        for(int i=0; i<expectedShortArray.length; i++){
            buf.put((byte) expectedShortArray[i]);
        }
        short[] actualShortArray = IOUtil.readUnsignedByteArray(new ByteArrayInputStream(buf.array()), expectedShortArray.length);
        assertTrue(Arrays.equals(expectedShortArray, actualShortArray));
    }
    
    @Test
    public void put(){
        ByteBuffer mockBuffer = createMock(ByteBuffer.class);
        for(int i=0; i<expectedShortArray.length; i++){
            expect(mockBuffer.put((byte) expectedShortArray[i])).andReturn(mockBuffer);
        }
        replay(mockBuffer);
        IOUtil.putUnsignedByteArray(mockBuffer, expectedShortArray);
        verify(mockBuffer);
    }
}
