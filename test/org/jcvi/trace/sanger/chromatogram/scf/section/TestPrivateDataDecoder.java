/*
 * Created on Sep 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import static org.easymock.classextension.EasyMock.createMock;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.testUtil.EasyMockUtil;
import org.jcvi.trace.sanger.chromatogram.scf.PrivateData;
import org.jcvi.trace.sanger.chromatogram.scf.SCFChromatogramBuilder;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;
import org.jcvi.trace.sanger.chromatogram.scf.section.PrivateDataCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionDecoder;
import org.jcvi.trace.sanger.chromatogram.scf.section.SectionDecoderException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;
public class TestPrivateDataDecoder {
    private byte[] data = new byte[]{20,30,40, -20, -67,125};
    private PrivateData expectedPrivateData = new PrivateData(data);
    SectionDecoder sut = new PrivateDataCodec();
    SCFHeader mockHeader;
    SCFChromatogramBuilder c;
    DataInputStream in;
    @Before
    public void setup(){
        mockHeader = createMock(SCFHeader.class);
        c = new SCFChromatogramBuilder();
        in = new DataInputStream(new ByteArrayInputStream(data));
    }

    @Test
    public void valid() throws SectionDecoderException{
        decodeValid(in,0, 0);
    }
    @Test
    public void validWithSkip() throws SectionDecoderException, IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        int bytesToSkip = 100;
        expect(mockInputStream.skip(bytesToSkip)).andReturn((long)bytesToSkip);
        expect(mockInputStream.read(isA(byte[].class), eq(0),eq(data.length)))
                .andAnswer(EasyMockUtil.writeArrayToInputStream(data));
        replay(mockInputStream);
        decodeValid(new DataInputStream(mockInputStream),0, bytesToSkip);
        verify(mockInputStream);
    }

    private void decodeValid(DataInputStream inputStream,int currentOffset, int bytesToSkip)
            throws SectionDecoderException {
        expect(mockHeader.getPrivateDataOffset()).andReturn(currentOffset+bytesToSkip);
        expect(mockHeader.getPrivateDataSize()).andReturn(data.length);
        replay(mockHeader);
        long newOffset =sut.decode(inputStream, currentOffset, mockHeader, c);
        assertEquals(newOffset-currentOffset-bytesToSkip, data.length);
        assertArrayEquals(expectedPrivateData.getData().array(), c.privateData());
        verify(mockHeader);
    }
    @Test
    public void incorrectNumberOfBytesReadShouldThrowSectionDecoderException() throws IOException{
        byte[] only4Bytes = new byte[]{(byte)1,(byte)2,(byte)3,(byte)4, };
        expect(mockHeader.getPrivateDataOffset()).andReturn(0);
        
        final int expectedNumberOfBytes = only4Bytes.length +1;
        expect(mockHeader.getPrivateDataSize()).andReturn(expectedNumberOfBytes);
        InputStream mockInputStream = createMock(InputStream.class);
        expect(mockInputStream.read(isA(byte[].class), eq(0),eq(expectedNumberOfBytes)))
        .andAnswer(EasyMockUtil.writeArrayToInputStream(only4Bytes));
        replay(mockHeader,mockInputStream);
        try {
            sut.decode(new DataInputStream(mockInputStream), 0L, mockHeader, c);
            fail("should throw exception if not expected number of bytes read");
        } catch (SectionDecoderException e) {
            assertEquals("could not read entire private data section", e.getMessage());
        }
    }
    @Test
    public void validNullPrivateData() throws SectionDecoderException{
        int currentOffset=0;
        expect(mockHeader.getPrivateDataOffset()).andReturn(currentOffset);
        expect(mockHeader.getPrivateDataSize()).andReturn(0);
        replay(mockHeader);
        long newOffset =sut.decode(in, currentOffset, mockHeader, c);
        assertEquals(newOffset-currentOffset, 0);
        assertNull(c.privateData());
        verify(mockHeader);
    }
}
