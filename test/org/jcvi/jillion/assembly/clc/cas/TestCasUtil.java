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
 * Created on Jan 19, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import org.jcvi.jillion.assembly.clc.cas.CasUtil;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.testUtil.EasyMockUtil;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestCasUtil {

    @Test
    public void numberOfBytesRequired(){
        
        assertEquals(0, CasUtil.numberOfBytesRequiredFor(1L));
        assertEquals(1, CasUtil.numberOfBytesRequiredFor(100L));
        assertEquals(1, CasUtil.numberOfBytesRequiredFor(255L));
        assertEquals(1, CasUtil.numberOfBytesRequiredFor(256L));
        assertEquals(2, CasUtil.numberOfBytesRequiredFor(257L));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void numberOfBytesRequiredLessThanZeroShouldthrowException(){
        CasUtil.numberOfBytesRequiredFor(-1);
    }
    
    @Test
    public void parseByteCountOneByte() throws IOException{
        InputStream in = createMock(InputStream.class);
        int expected = 123;
        expect(in.read()).andReturn(expected);
        replay(in);
        assertEquals(expected, CasUtil.parseByteCountFrom(in));
        verify(in);
    }
    @Test
    public void parseByteCountTwoBytes() throws IOException{
        InputStream in = createMock(InputStream.class);
        int expected = Short.MAX_VALUE+1;
        byte[] asLittleEndian =IOUtil.switchEndian(IOUtil.convertUnsignedShortToByteArray(expected));
        expect(in.read()).andReturn(254);
        expect(in.read(isA(byte[].class), eq(0),eq(2)))
                .andAnswer(EasyMockUtil.writeArrayToInputStream(asLittleEndian));
        replay(in);
        assertEquals(expected, CasUtil.parseByteCountFrom(in));
        verify(in);
    }
    @Test
    public void parseByteCountFourBytes() throws IOException{
        InputStream in = createMock(InputStream.class);
        long expected = Integer.MAX_VALUE+1L;
        byte[] asLittleEndian =IOUtil.switchEndian(IOUtil.convertUnsignedIntToByteArray(expected));
        expect(in.read()).andReturn(255);
        expect(in.read(isA(byte[].class), eq(0),eq(4)))
                .andAnswer(EasyMockUtil.writeArrayToInputStream(asLittleEndian));
        replay(in);
        assertEquals(expected, CasUtil.parseByteCountFrom(in));
        verify(in);
    }
    
    @Test
    public void readCasUnsignedByteByteValue() throws IOException{
        InputStream in = createMock(InputStream.class);
        short expected = 120;
        byte[] asLittleEndian =IOUtil.switchEndian(IOUtil.convertUnsignedByteToByteArray(expected));
        expect(in.read(isA(byte[].class), eq(0),eq(1)))
                .andAnswer(EasyMockUtil.writeArrayToInputStream(asLittleEndian));
        replay(in);
        assertEquals(expected, CasUtil.readCasUnsignedByte(in));
        verify(in);
    }
    @Test
    public void readCasUnsignedByteShortValue() throws IOException{
        InputStream in = createMock(InputStream.class);
        short expected = Byte.MAX_VALUE+1;
        byte[] asLittleEndian =IOUtil.switchEndian(IOUtil.convertUnsignedByteToByteArray(expected));
        expect(in.read(isA(byte[].class), eq(0),eq(1)))
                .andAnswer(EasyMockUtil.writeArrayToInputStream(asLittleEndian));
        replay(in);
        assertEquals(expected, CasUtil.readCasUnsignedByte(in));
        verify(in);
    }
    
    @Test
    public void readCasUnsignedLongLongValue() throws IOException{
        InputStream in = createMock(InputStream.class);
        BigInteger expected = new BigInteger(Long.valueOf(Long.MAX_VALUE).toString());
        byte[] asLittleEndian =IOUtil.switchEndian(IOUtil.convertUnsignedLongToByteArray(expected));
        expect(in.read(isA(byte[].class), eq(0),eq(8)))
                .andAnswer(EasyMockUtil.writeArrayToInputStream(asLittleEndian));
        replay(in);
        assertEquals(expected, CasUtil.readCasUnsignedLong(in));
        verify(in);
    }
    @Test
    public void readCasUnsignedLongBigIntValue() throws IOException{
        InputStream in = createMock(InputStream.class);
        //really big # too big to fit inside a java long
        BigInteger expected = new BigInteger("18446744073709551614");
        byte[] asLittleEndian =IOUtil.switchEndian(IOUtil.convertUnsignedLongToByteArray(expected));
        expect(in.read(isA(byte[].class), eq(0),eq(8)))
                .andAnswer(EasyMockUtil.writeArrayToInputStream(asLittleEndian));
        replay(in);
        assertEquals(expected, CasUtil.readCasUnsignedLong(in));
        verify(in);
    }
    
    @Test
    public void parseCasByteLengthString() throws IOException{
        String expected ="the quick brown fox jumped over the lazy dog";
        int length = expected.length();
        ByteBuffer buffer = ByteBuffer.allocate(1 + length);
        buffer.put((byte)length);
        buffer.put(expected.getBytes());
        buffer.flip();
        
        assertEquals(expected, CasUtil.parseCasStringFrom(new ByteArrayInputStream(buffer.array())));
    }
    @Test
    public void parseCasShortLengthString() throws IOException{
        String expected =
            "A scientific truth does not triumph by convincing its opponents "+
            "and making them see the light, but rather because its opponents "+
            "eventually die and a new generation grows up that is familiar "+
            "with it. -Max Planck";
        
        int length = expected.length();
        ByteBuffer buffer = ByteBuffer.allocate(3 + length);
        buffer.put((byte)254);
        buffer.put(IOUtil.switchEndian(IOUtil.convertUnsignedShortToByteArray(length)));
        buffer.put(expected.getBytes());
        buffer.flip();
        
        assertEquals(expected, CasUtil.parseCasStringFrom(new ByteArrayInputStream(buffer.array())));
    }

}
