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
package org.jcvi.trace.sanger.chromatogram.scf.position;

import static org.junit.Assert.assertEquals;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.trace.sanger.chromatogram.scf.position.ShortPositionStrategy;
import org.junit.Test;

public class TestShortPositionStrategy {
    ShortPositionStrategy sut = new ShortPositionStrategy();
    @Test
    public void maxAllowedValue(){
        assertEquals(Short.MAX_VALUE, sut.getMaxAllowedValue());
    }

    @Test
    public void getSampleSize(){
        assertEquals(2, sut.getSampleSize());
    }

    @Test
    public void getPosition() throws IOException{
        InputStream in = createMock(InputStream.class);
        short expectedShort = 200;
        //a short is 2 consecutive bytes read
        //most sig bits
        expect(in.read()).andReturn(expectedShort>>>8);
        //least sig bits
        expect(in.read()).andReturn(expectedShort & 0xFF);
        replay(in);
        assertEquals(expectedShort, sut.getPosition(new DataInputStream(in)));
        verify(in);
    }

    @Test
    public void setPosition(){
        final short value = (short)200;
        ByteBuffer mockBuffer = createMock(ByteBuffer.class);
        expect(mockBuffer.putShort(value)).andReturn(mockBuffer);
        replay(mockBuffer);
        sut.setPosition(value, mockBuffer);
        verify(mockBuffer);
    }
}
