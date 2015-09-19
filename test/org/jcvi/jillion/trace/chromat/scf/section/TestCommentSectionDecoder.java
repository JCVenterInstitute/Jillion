/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jcvi.jillion.core.testUtil.EasyMockUtil;
import org.jcvi.jillion.internal.trace.chromat.scf.section.SectionDecoderException;
import org.junit.Test;
public class TestCommentSectionDecoder extends AbstractTestCommentSection{


    @Test
    public void valid() throws SectionDecoderException{
        final String scfCommentAsString = convertPropertiesToSCFComment(expectedComments);
        DataInputStream in =new DataInputStream(
                new ByteArrayInputStream(scfCommentAsString.getBytes()));

        expect(mockHeader.getCommentOffset()).andReturn(currentOffset);
        expect(mockHeader.getCommentSize()).andReturn(scfCommentAsString.length());
        replay(mockHeader);
        long newOffset =sut.decode(in, currentOffset, mockHeader, builder);
        verify(mockHeader);
        assertEquals(expectedComments, builder.comments());
        assertEquals(scfCommentAsString.length(),newOffset);
    }

    @Test
    public void validMustSkipToStartOfCommentSection() throws Exception{


        final String scfCommentAsString = convertPropertiesToSCFComment(expectedComments);
        int distanceToSkip=200;
        InputStream mockInputStream = createMock(InputStream.class);
        expect(mockInputStream.read()).andReturn(1);
        expect(mockInputStream.skip(distanceToSkip-1)).andReturn((long)(distanceToSkip-1));
        //we need to manually fill in the array for this test
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(scfCommentAsString.length()))).andAnswer(
                EasyMockUtil.writeArrayToInputStream(scfCommentAsString.getBytes()));


        expect(mockHeader.getCommentOffset()).andReturn(distanceToSkip);
        expect(mockHeader.getCommentSize()).andReturn(scfCommentAsString.length());
        replay(mockHeader,mockInputStream);
        long newOffset =sut.decode(new DataInputStream(mockInputStream), currentOffset, mockHeader, builder);
        verify(mockHeader,mockInputStream);
        assertEquals(expectedComments, builder.comments());
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
            sut.decode(new DataInputStream(mockInputStream), currentOffset, mockHeader, builder);
            fail("should throw SectionParserException on error");
        }
        catch(SectionDecoderException e){

        }
        verify(mockHeader);
        assertTrue(builder.comments().isEmpty());

    }


}
