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

import java.io.IOException;

import org.jcvi.jillion.internal.trace.chromat.scf.section.EncodedSection;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Section;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestCommentSectionEncoder extends AbstractTestCommentSection{

    @Test
    public void valid() throws IOException{
        expect(mockChroma.getComments()).andReturn(expectedComments);
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
