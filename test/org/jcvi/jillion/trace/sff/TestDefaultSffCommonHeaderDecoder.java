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
 * Created on Oct 10, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.testUtil.EasyMockUtil;
import org.jcvi.jillion.trace.sff.SffCommonHeader;
import org.jcvi.jillion.trace.sff.SffDecoderException;
import org.jcvi.jillion.trace.sff.SffUtil;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestDefaultSffCommonHeaderDecoder extends AbstractTestDefaultSFFCommonHeaderCodec{

    @Test
    public void valid() throws SffDecoderException, IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        encode(mockInputStream, expectedHeader);
        replay(mockInputStream);
        SffCommonHeader actualHeader =sut.decodeHeader(new DataInputStream(mockInputStream));
        assertEquals(expectedHeader, actualHeader);
        verify(mockInputStream);
    }

    @Test
    public void invalidReadThrowsIOExceptionShouldWrapInSFFDecoderException() throws IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        IOException ioException = new IOException("expected");
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(4))).andThrow(ioException);
        replay(mockInputStream);
        try{
            sut.decodeHeader(new DataInputStream(mockInputStream));
            fail("should wrap IOException in SFFDecoderException");
        }catch(SffDecoderException e){
            assertEquals("error decoding sff file", e.getMessage());
            assertEquals(e.getCause(), ioException);
        }
        verify(mockInputStream);
    }
    @Test
    public void invalidReadFailsMagicNumberShouldThrowSFFDecoderException() throws IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(4))).andAnswer(EasyMockUtil.writeArrayToInputStream(".ZTR".getBytes()));
        replay(mockInputStream);
        try{
            sut.decodeHeader(new DataInputStream(mockInputStream));
            fail("should wrap IOException in SFFDecoderException");
        }catch(IOException e){
            SffDecoderException decoderException = (SffDecoderException)e.getCause();
            assertEquals("magic number does not match expected", decoderException.getMessage());
            assertNull(decoderException.getCause());
        }
        verify(mockInputStream);
    }
    @Test
    public void invalidReadFailsInvalidVersionShouldThrowSFFDecoderException() throws IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        encodeNotValidVersion(mockInputStream);
        replay(mockInputStream);
        try{
            sut.decodeHeader(new DataInputStream(mockInputStream));
            fail("should wrap IOException in SFFDecoderException");
        }catch(IOException e){
            SffDecoderException decoderException = (SffDecoderException)e.getCause();
            assertEquals("version not compatible with decoder", decoderException.getMessage());
            assertNull(decoderException.getCause());
        }
        verify(mockInputStream);
    }

    @Test
    public void invalidReadFailsInvalidFormatCodeShouldThrowSFFDecoderException() throws IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        invailidFormatCode(mockInputStream, expectedHeader);
        replay(mockInputStream);
        try{
            sut.decodeHeader(new DataInputStream(mockInputStream));
            fail("should wrap IOException in SFFDecoderException");
        }catch(IOException e){
            SffDecoderException decoderException = (SffDecoderException)e.getCause();
            assertEquals("unknown flowgram format code", decoderException.getMessage());
            assertNull(decoderException.getCause());
        }
        verify(mockInputStream);
    }

    @Test
    public void invalidReadFailsFlowNotLongEnoughShouldThrowSFFDecoderException() throws IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        invailidFlow(mockInputStream, expectedHeader);
        replay(mockInputStream);
        try{
            sut.decodeHeader(new DataInputStream(mockInputStream));
            fail("should wrap IOException in SFFDecoderException");
        }catch(IOException e){
            SffDecoderException decoderException = (SffDecoderException)e.getCause();
            assertEquals("error decoding flow", decoderException.getMessage());
            assertTrue(decoderException.getCause() instanceof EOFException);
        }
        verify(mockInputStream);
    }

    @Test
    public void invalidReadFailsKeySequenceNotLongEnoughShouldThrowSFFDecoderException() throws IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        invailidKeySequence(mockInputStream, expectedHeader);
        replay(mockInputStream);
        try{
            sut.decodeHeader(new DataInputStream(mockInputStream));
            fail("should wrap IOException in SFFDecoderException");
        }catch(IOException e){
            SffDecoderException decoderException = (SffDecoderException)e.getCause();
            assertEquals("error decoding keySequence", decoderException.getMessage());
            assertTrue(decoderException.getCause() instanceof EOFException);
        }
        verify(mockInputStream);
    }


    void encodeNotValidVersion(InputStream mockInputStream) throws IOException{
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(4)))
                .andAnswer(EasyMockUtil.writeArrayToInputStream(".sff".getBytes()));
        final byte[] invalidVersion = new byte[]{0,0,0,2};
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(4)))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(invalidVersion));
    }

    void encode(InputStream mockInputStream, SffCommonHeader header) throws IOException{
        final short keyLength =(short) (header.getKeySequence().getLength());
        int size = 31+header.getNumberOfFlowsPerRead()+ keyLength;
        long padding =SffUtil.caclulatePaddedBytes(size);
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(4)))
                .andAnswer(EasyMockUtil.writeArrayToInputStream(".sff".getBytes()));

        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(4)))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(new byte[]{0,0,0,1}));
        EasyMockUtil.putUnSignedLong(mockInputStream, header.getIndexOffset());
        EasyMockUtil.putUnSignedInt(mockInputStream, header.getIndexLength());
        EasyMockUtil.putUnSignedInt(mockInputStream, header.getNumberOfReads());
        EasyMockUtil.putUnSignedShort(mockInputStream, (short)(size+padding));
        EasyMockUtil.putUnSignedShort(mockInputStream, keyLength);
        EasyMockUtil.putUnSignedShort(mockInputStream, header.getNumberOfFlowsPerRead());
        EasyMockUtil.putByte(mockInputStream, (byte)1);
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(header.getNumberOfFlowsPerRead())))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(header.getFlowSequence().toString().getBytes()));
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq((int)keyLength)))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(header.getKeySequence().toString().getBytes()));

        expect(mockInputStream.skip(padding)).andReturn(padding);

    }

    void invailidFormatCode(InputStream mockInputStream, SffCommonHeader header) throws IOException{
        final short keyLength =(short) (header.getKeySequence().getLength());
        int size = 31+header.getNumberOfFlowsPerRead()+ keyLength;
        long padding =SffUtil.caclulatePaddedBytes(size);
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(4)))
                .andAnswer(EasyMockUtil.writeArrayToInputStream(".sff".getBytes()));

        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(4)))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(new byte[]{0,0,0,1}));
        EasyMockUtil.putUnSignedLong(mockInputStream, header.getIndexOffset());
        EasyMockUtil.putUnSignedInt(mockInputStream, header.getIndexLength());
        EasyMockUtil.putUnSignedInt(mockInputStream, header.getNumberOfReads());
        EasyMockUtil.putUnSignedShort(mockInputStream, (short)(size+padding));
        EasyMockUtil.putUnSignedShort(mockInputStream, keyLength);
        EasyMockUtil.putUnSignedShort(mockInputStream, header.getNumberOfFlowsPerRead());
        //invalid format code is here:
        EasyMockUtil.putByte(mockInputStream, (byte)2);

    }

    void invailidFlow(InputStream mockInputStream, SffCommonHeader header) throws IOException{
        final short keyLength =(short) (header.getKeySequence().getLength());
        int size = 31+header.getNumberOfFlowsPerRead()+ keyLength;
        long padding =SffUtil.caclulatePaddedBytes(size);
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(4)))
                .andAnswer(EasyMockUtil.writeArrayToInputStream(".sff".getBytes()));

        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(4)))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(new byte[]{0,0,0,1}));
        EasyMockUtil.putUnSignedLong(mockInputStream, header.getIndexOffset());
        EasyMockUtil.putUnSignedInt(mockInputStream, header.getIndexLength());
        EasyMockUtil.putUnSignedInt(mockInputStream, header.getNumberOfReads());
        EasyMockUtil.putUnSignedShort(mockInputStream, (short)(size+padding));
        EasyMockUtil.putUnSignedShort(mockInputStream, keyLength);
        EasyMockUtil.putUnSignedShort(mockInputStream, header.getNumberOfFlowsPerRead());
        EasyMockUtil.putByte(mockInputStream, (byte)1);
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(header.getNumberOfFlowsPerRead())))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(header.getFlowSequence().toString().substring(1).getBytes()));
        expect(mockInputStream.read(isA(byte[].class),eq(11),eq(1))).andReturn(-1); //EOF

    }

    void invailidKeySequence(InputStream mockInputStream, SffCommonHeader header) throws IOException{
        final short keyLength =(short) (header.getKeySequence().getLength());
        int size = 31+header.getNumberOfFlowsPerRead()+ keyLength;
        long padding =SffUtil.caclulatePaddedBytes(size);
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(4)))
                .andAnswer(EasyMockUtil.writeArrayToInputStream(".sff".getBytes()));

        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(4)))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(new byte[]{0,0,0,1}));
        EasyMockUtil.putUnSignedLong(mockInputStream, header.getIndexOffset());
        EasyMockUtil.putUnSignedInt(mockInputStream, header.getIndexLength());
        EasyMockUtil.putUnSignedInt(mockInputStream, header.getNumberOfReads());
        EasyMockUtil.putUnSignedShort(mockInputStream, (short)(size+padding));
        EasyMockUtil.putUnSignedShort(mockInputStream, keyLength);
        EasyMockUtil.putUnSignedShort(mockInputStream, header.getNumberOfFlowsPerRead());
        EasyMockUtil.putByte(mockInputStream, (byte)1);
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(header.getNumberOfFlowsPerRead())))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(header.getFlowSequence().toString().getBytes()));
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq((int)keyLength)))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(header.getKeySequence().toString().substring(1).getBytes()));
        
        expect(mockInputStream.read(isA(byte[].class), eq(3),eq(1))).andReturn(-1); //EOF
    }
}
