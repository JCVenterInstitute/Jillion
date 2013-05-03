/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
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
