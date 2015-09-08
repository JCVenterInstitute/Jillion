/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.io;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.jillion.core.io.IOUtil;
import org.junit.Test;

public class TestIOUtil_readShortArray {

    short[] shortArray = new short[]{130, 20,260, 6500};
    byte[] expectedByteArray;
    @org.junit.Before
    public void setup(){
        ByteBuffer buf = ByteBuffer.allocate(shortArray.length *2);
        for(int i=0; i< shortArray.length; i++){
            buf.putShort(shortArray[i]);
        }
        expectedByteArray = buf.array();
    }
    @Test
    public void valid() throws IOException{
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(expectedByteArray));
        short[] actualArray = IOUtil.readShortArray(in, shortArray.length);
        assertTrue(Arrays.equals(shortArray, actualArray));
    }

    @Test
    public void didNotReadEnough(){
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(expectedByteArray));
        try {
            IOUtil.readShortArray(in, shortArray.length+1);
            fail("if did not read exected length should throw IOException");
        } catch (IOException expected) {

        }

    }
}
