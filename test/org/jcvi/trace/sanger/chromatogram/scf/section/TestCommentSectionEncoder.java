/*
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import java.io.IOException;

import org.jcvi.trace.sanger.chromatogram.scf.section.EncodedSection;
import org.jcvi.trace.sanger.chromatogram.scf.section.Section;
import org.junit.Test;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
public class TestCommentSectionEncoder extends AbstractTestCommentSection{

    @Test
    public void valid() throws IOException{
        expect(mockChroma.getProperties()).andReturn(expectedComments);
        final String expectedCommentAsString = this.convertPropertiesToSCFComment(expectedComments);
        mockHeader.setCommentSize(expectedCommentAsString.length());
        replay(mockChroma,mockHeader);
        EncodedSection actualEncodedSection =sut.encode(mockChroma, mockHeader);
        verify(mockChroma,mockHeader);
        assertEquals(Section.COMMENTS,actualEncodedSection.getSection());
       assertArrayEquals(expectedCommentAsString.getBytes(),
                actualEncodedSection.getData().array());
    }

    @Test
    public void nullCommentsMakesEncodedSectionWithNullData() throws IOException{
        mockHeader.setCommentSize(0);
        replay(mockHeader);
        EncodedSection actualEncodedSection =sut.encode(mockChroma, mockHeader);
        verify(mockHeader);
        assertEquals(Section.COMMENTS,actualEncodedSection.getSection());
        assertNull(actualEncodedSection.getData());
    }
}
