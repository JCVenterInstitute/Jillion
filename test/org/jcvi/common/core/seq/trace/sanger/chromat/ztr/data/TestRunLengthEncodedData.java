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
 * Created on Dec 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.chromat.ztr.data;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.trace.TraceDecoderException;
import org.jcvi.common.core.seq.trace.TraceEncoderException;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestRunLengthEncodedData {
    static byte[] uncompressedData = new byte[]{20,9,9,9,9,9,10,9,8,7};
    static byte[] encodedData = new byte[15];
    static byte guard = (byte)8;
    RunLengthEncodedData sut = RunLengthEncodedData.INSTANCE;
    static{
        
        ByteBuffer buf = ByteBuffer.wrap(encodedData);
        //header for runlength encoding
        buf.put((byte)1);
        //length is different endian?
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        lengthBuffer.putInt(uncompressedData.length);
        buf.put(IOUtil.switchEndian(lengthBuffer.array()));
        buf.put(guard);
        buf.put(uncompressedData[0]);
        buf.put(guard);
        buf.put((byte)5);
        buf.put(uncompressedData[0+1]);
        
        buf.put(uncompressedData[0+1+5]);
        buf.put(uncompressedData[0+1+5+1]);
        buf.put(guard);
        //a byte of 0 means value is same as guard but
        //is not meant to be a guard 
        buf.put((byte)0);
        buf.put(uncompressedData[0+1+5+1+1+1]);
        buf.flip();
    }
    
    @Test
    public void parse() throws TraceDecoderException{
        byte[] actualUncompressed = sut.parseData(encodedData);
        assertTrue(Arrays.equals(actualUncompressed, uncompressedData));
    }
    @Test
    public void encode() throws TraceEncoderException{
    	byte[] actual = sut.encodeData(uncompressedData, guard);
    	assertArrayEquals(actual, encodedData);
    }
    
    @Test
    public void parseLongHomopolomer() throws TraceDecoderException{
        int repeatLength = 240;
        ByteBuffer buf = ByteBuffer.allocate(11);
        buf.put((byte)1);
        buf.put((byte)242);
        buf.put((byte)0);
        buf.put((byte)0);
        buf.put((byte)0);
        buf.put(guard);
        buf.put(uncompressedData[0]);
        buf.put(guard);
        buf.put((byte)repeatLength);
        buf.put(uncompressedData[0+1]);
        buf.put((byte)15);
        byte[] actual = sut.parseData(buf.array());
        byte[] expected = new byte[242];
        expected[0]=uncompressedData[0];
        for(int i=0; i<repeatLength; i++){
            expected[i+1] = uncompressedData[0+1];
        }
        expected[241] = 15;

        assertTrue(Arrays.equals(actual, expected));
        
        
    }
    
}
