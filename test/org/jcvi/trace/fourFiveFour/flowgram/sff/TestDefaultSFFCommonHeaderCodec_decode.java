/*
 * Created on Oct 10, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.testUtil.EasyMockUtil;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFCommonHeader;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFDecoderException;
import org.jcvi.trace.fourFiveFour.flowgram.sff.SFFUtil;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;
public class TestDefaultSFFCommonHeaderCodec_decode extends AbstractTestDefaultSFFCommonHeaderCodec{

    @Test
    public void valid() throws SFFDecoderException, IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        encode(mockInputStream, expectedHeader);
        replay(mockInputStream);
        SFFCommonHeader actualHeader =sut.decodeHeader(new DataInputStream(mockInputStream));
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
        }catch(SFFDecoderException e){
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
        }catch(SFFDecoderException e){
            assertEquals("magic number does not match expected", e.getMessage());
            assertNull(e.getCause());
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
        }catch(SFFDecoderException e){
            assertEquals("version not compatible with decoder", e.getMessage());
            assertNull(e.getCause());
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
        }catch(SFFDecoderException e){
            assertEquals("unknown flowgram format code", e.getMessage());
            assertNull(e.getCause());
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
        }catch(SFFDecoderException e){
            assertEquals("error decoding flow", e.getMessage());
            assertNull(e.getCause());
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
        }catch(SFFDecoderException e){
            assertEquals("error decoding keySequence", e.getMessage());
            assertNull(e.getCause());
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

    void encode(InputStream mockInputStream, SFFCommonHeader header) throws IOException{
        final short keyLength =(short) (header.getKeySequence().length());
        int size = 31+header.getNumberOfFlowsPerRead()+ keyLength;
        long padding =SFFUtil.caclulatePaddedBytes(size);
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
        .andAnswer(EasyMockUtil.writeArrayToInputStream(header.getFlow().getBytes()));
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq((int)keyLength)))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(header.getKeySequence().getBytes()));

        expect(mockInputStream.skip(padding)).andReturn(padding);

    }

    void invailidFormatCode(InputStream mockInputStream, SFFCommonHeader header) throws IOException{
        final short keyLength =(short) (header.getKeySequence().length());
        int size = 31+header.getNumberOfFlowsPerRead()+ keyLength;
        long padding =SFFUtil.caclulatePaddedBytes(size);
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

    void invailidFlow(InputStream mockInputStream, SFFCommonHeader header) throws IOException{
        final short keyLength =(short) (header.getKeySequence().length());
        int size = 31+header.getNumberOfFlowsPerRead()+ keyLength;
        long padding =SFFUtil.caclulatePaddedBytes(size);
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
        .andAnswer(EasyMockUtil.writeArrayToInputStream(header.getFlow().substring(1).getBytes()));

    }

    void invailidKeySequence(InputStream mockInputStream, SFFCommonHeader header) throws IOException{
        final short keyLength =(short) (header.getKeySequence().length());
        int size = 31+header.getNumberOfFlowsPerRead()+ keyLength;
        long padding =SFFUtil.caclulatePaddedBytes(size);
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
        .andAnswer(EasyMockUtil.writeArrayToInputStream(header.getFlow().getBytes()));
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq((int)keyLength)))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(header.getKeySequence().substring(1).getBytes()));

    }
}
