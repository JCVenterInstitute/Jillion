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
package org.jcvi.jillion.trace.sanger.chromat.scf.section;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.common.core.testUtil.EasyMockUtil;
import org.jcvi.jillion.core.internal.seq.trace.sanger.chromat.scf.section.SectionDecoderException;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestCommentSectionDecoder extends AbstractTestCommentSection{


    @Test
    public void valid() throws SectionDecoderException{
        final String scfCommentAsString = convertPropertiesToSCFComment(expectedComments);
        DataInputStream in =new DataInputStream(
                new ByteArrayInputStream(scfCommentAsString.getBytes()));

        expect(mockHeader.getCommentOffset()).andReturn(currentOffset);
        expect(mockHeader.getCommentSize()).andReturn(scfCommentAsString.length());
        replay(mockHeader);
        long newOffset =sut.decode(in, currentOffset, mockHeader, chromaStruct);
        verify(mockHeader);
        assertEquals(expectedComments, chromaStruct.properties());
        assertEquals(scfCommentAsString.length(),newOffset);
    }

    @Test
    public void validMustSkipToStartOfCommentSection() throws Exception{


        final String scfCommentAsString = convertPropertiesToSCFComment(expectedComments);
        int distanceToSkip=200;
        InputStream mockInputStream = createMock(InputStream.class);
        expect(mockInputStream.skip(distanceToSkip)).andReturn((long)distanceToSkip);
        //we need to manually fill in the array for this test
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(scfCommentAsString.length()))).andAnswer(
                EasyMockUtil.writeArrayToInputStream(scfCommentAsString.getBytes()));


        expect(mockHeader.getCommentOffset()).andReturn(distanceToSkip);
        expect(mockHeader.getCommentSize()).andReturn(scfCommentAsString.length());
        replay(mockHeader,mockInputStream);
        long newOffset =sut.decode(new DataInputStream(mockInputStream), currentOffset, mockHeader, chromaStruct);
        verify(mockHeader,mockInputStream);
        assertEquals(expectedComments, chromaStruct.properties());
        assertEquals(scfCommentAsString.length()+distanceToSkip,newOffset);
    }
    @Test
    public void readThrowsIOExceptionShouldWrapInSectionParserException() throws IOException{
        //need to mock inputstream and wrap it in aDataInputStream
        //because datainputStream read methods are final and can't be mocked.
        InputStream mockInputStream = createMock(InputStream.class);
        IOException expectedException = new IOException("expected");
        expect(mockHeader.getCommentOffset()).andReturn(currentOffset);
        int commentLength = 10;
        expect(mockHeader.getCommentSize()).andReturn(commentLength);
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(commentLength))).andThrow(expectedException);
        replay(mockHeader,mockInputStream);
        try{
            sut.decode(new DataInputStream(mockInputStream), currentOffset, mockHeader, chromaStruct);
            fail("should throw SectionParserException on error");
        }
        catch(SectionDecoderException e){

        }
        verify(mockHeader);
        assertNull(chromaStruct.properties());

    }


}
