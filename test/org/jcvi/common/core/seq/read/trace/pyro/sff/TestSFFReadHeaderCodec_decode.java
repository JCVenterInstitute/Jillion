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
 * Created on Oct 8, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.pyro.sff;



import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFDecoderException;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFReadHeader;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SFFUtil;
import org.jcvi.common.core.testUtil.EasyMockUtil;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.jcvi.common.core.testUtil.EasyMockUtil.*;
import static org.easymock.EasyMock.*;
public class TestSFFReadHeaderCodec_decode extends AbstractTestSFFReadHeaderCodec{

    @Test
    public void valid() throws SFFDecoderException, IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        encodeHeader(mockInputStream, expectedReadHeader);
        replay(mockInputStream);
        SFFReadHeader actualReadHeader =sut.decodeReadHeader(new DataInputStream(mockInputStream));
        assertEquals(actualReadHeader, expectedReadHeader);
        verify(mockInputStream);
    }
    @Test
    public void sequenceNameLengthEncodedIncorrectlyShouldThrowIOException() throws  IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        encodeHeaderWithWrongSequenceLength(mockInputStream, expectedReadHeader);
        replay(mockInputStream);
        try{
            sut.decodeReadHeader(new DataInputStream(mockInputStream));
            fail("should throw SFFDecoderException if name length encoded wrong");
        }catch(IOException e){
            Throwable cause = e.getCause();
            assertEquals("error decoding seq name", cause.getMessage());
        }


        verify(mockInputStream);
    }

    @Test
    public void readThrowsIOExceptionShouldWrapInSFFDecoderException() throws IOException{
        InputStream mockInputStream = createMock(InputStream.class);
        IOException expectedIOException = new IOException("expected");
        expect(mockInputStream.read()).andThrow(expectedIOException);
        replay(mockInputStream);
        try {
            sut.decodeReadHeader(new DataInputStream(mockInputStream));
            fail("should wrap IOException in SFFDecoderException");
        } catch (SFFDecoderException e) {
            assertEquals("error trying to decode read header", e.getMessage());
            assertEquals(expectedIOException, e.getCause());
        }

        verify(mockInputStream);
    }


    void encodeHeader(InputStream mockInputStream, SFFReadHeader readHeader) throws IOException{
        final String seqName = readHeader.getName();
        final int nameLength = seqName.length();
        int unpaddedLength = 16+nameLength;
        final long padds = SFFUtil.caclulatePaddedBytes(unpaddedLength);
        putShort(mockInputStream,(short)(padds+unpaddedLength));
        putShort(mockInputStream,(short)nameLength);
        putInt(mockInputStream,readHeader.getNumberOfBases());
        putShort(mockInputStream,(short)readHeader.getQualityClip().getLocalStart());
        putShort(mockInputStream,(short)readHeader.getQualityClip().getLocalEnd());
        putShort(mockInputStream,(short)readHeader.getAdapterClip().getLocalStart());
        putShort(mockInputStream,(short)readHeader.getAdapterClip().getLocalEnd());
        expect(mockInputStream.read(isA(byte[].class), eq(0),eq(nameLength)))
            .andAnswer(EasyMockUtil.writeArrayToInputStream(seqName.getBytes()));
        
        expect(mockInputStream.skip(padds)).andReturn(padds);
    }
    void encodeHeaderWithWrongSequenceLength(InputStream mockInputStream, SFFReadHeader readHeader) throws IOException{
        final String seqName = readHeader.getName();
        final int nameLength = seqName.length();
        int unpaddedLength = 16+nameLength;
        final long padds = SFFUtil.caclulatePaddedBytes(unpaddedLength);
        putShort(mockInputStream,(short)(padds+unpaddedLength));
        putShort(mockInputStream,(short)(nameLength+1));
        putInt(mockInputStream,readHeader.getNumberOfBases());
        putShort(mockInputStream,(short)readHeader.getQualityClip().getStart());
        putShort(mockInputStream,(short)readHeader.getQualityClip().getEnd());
        putShort(mockInputStream,(short)readHeader.getAdapterClip().getStart());
        putShort(mockInputStream,(short)readHeader.getAdapterClip().getEnd());
        expect(mockInputStream.read(isA(byte[].class), eq(0),eq(nameLength+1)))
            .andAnswer(EasyMockUtil.writeArrayToInputStream(seqName.getBytes()));
    }


}
