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
 * Created on Sep 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.PrivateData;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogramBuilder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.header.SCFHeader;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.PrivateDataCodec;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.SectionDecoder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.SectionDecoderException;
import org.jcvi.testUtil.EasyMockUtil;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
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
        } catch(IOException e){
            SectionDecoderException decoderException = (SectionDecoderException)e.getCause();
            
            assertEquals("could not read entire private data section", decoderException.getMessage());
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
