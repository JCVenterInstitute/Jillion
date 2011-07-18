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
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.pos;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.header.pos.BytePositionStrategy;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestBytePositionStrategy {
    BytePositionStrategy sut = new BytePositionStrategy();
    @Test
    public void maxAllowedValue(){
        assertEquals(Byte.MAX_VALUE, sut.getMaxAllowedValue());
    }

    @Test
    public void getSampleSize(){
        assertEquals(1, sut.getSampleSize());
    }

    @Test
    public void getPosition() throws IOException{
        InputStream in = createMock(InputStream.class);
        byte expectedByte = 16;
        expect(in.read()).andReturn((int)expectedByte);
        replay(in);
        assertEquals(expectedByte, sut.getPosition(new DataInputStream(in)));
        verify(in);
    }
    @Test
    public void setPositionTooBigShouldThrowIllegalArgumentException() {
        final short value = (short)(Byte.MAX_VALUE+1);
        try{
            sut.setPosition(value, null);
            fail("should throw illegalArgumentException if value is > max byte");
        }
        catch(IllegalArgumentException expected){
            assertEquals("position to put is too big :"+ value, expected.getMessage());
        }
    }
    @Test
    public void setPosition(){
        final short value = (short)10;
        ByteBuffer mockBuffer = createMock(ByteBuffer.class);
        expect(mockBuffer.put((byte)value)).andReturn(mockBuffer);
        replay(mockBuffer);
        sut.setPosition(value, mockBuffer);
        verify(mockBuffer);
    }

}
