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
 * Created on Sep 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.testUtil.EasyMockUtil;
import org.jcvi.jillion.internal.trace.chromat.scf.PrivateDataImpl;
import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.internal.trace.chromat.scf.section.PrivateDataCodec;
import org.jcvi.jillion.internal.trace.chromat.scf.section.SectionDecoder;
import org.jcvi.jillion.internal.trace.chromat.scf.section.SectionDecoderException;
import org.jcvi.jillion.trace.chromat.scf.PrivateData;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.junit.Before;
import org.junit.Test;
public class TestPrivateDataDecoder {
    private byte[] data = new byte[]{20,30,40, -20, -67,125};
    private PrivateData expectedPrivateData = new PrivateDataImpl(data);
    SectionDecoder sut = new PrivateDataCodec();
    SCFHeader mockHeader;
    ScfChromatogramBuilder c;
    DataInputStream in;
    @Before
    public void setup(){
        mockHeader = createMock(SCFHeader.class);
        c = new ScfChromatogramBuilder("id");
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
        expect(mockInputStream.read()).andReturn(1);
        expect(mockInputStream.skip(bytesToSkip-1)).andReturn((long)(bytesToSkip-1));
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
        assertArrayEquals(expectedPrivateData.getBytes(), c.privateData());
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
