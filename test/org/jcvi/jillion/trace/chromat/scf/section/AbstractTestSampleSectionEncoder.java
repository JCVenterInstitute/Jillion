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
 * Created on Sep 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;

import static org.junit.Assert.*;

import java.io.IOException;

import org.jcvi.jillion.internal.trace.chromat.scf.section.EncodedSection;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Section;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractTestSampleSectionEncoder {
    private AbstractTestSamplesSection sut;

    @Before
    public void createMockHeader(){
        sut = createSut();
    }
    protected abstract AbstractTestSamplesSection createSut();
    @Test
    public void validEncodeShorts() throws IOException{
        sut.makeChromatogramsHaveShorts();
        EncodedSection actualEncodedSection = sut.encode((byte)2);
        assertEquals(Section.SAMPLES, actualEncodedSection.getSection());
        final byte[] expectedEncodedShorts = sut.encodeShortPositions();
        assertArrayEquals(expectedEncodedShorts, actualEncodedSection.getData().array());

    }

    @Test
    public void validEncodeBytes() throws IOException{
        sut.makeChromatogramsHaveBytes();
        EncodedSection actualEncodedSection = sut.encode((byte)1);
        assertEquals(Section.SAMPLES, actualEncodedSection.getSection());
        final byte[] expectedEncodedBytes = sut.encodeBytePositions();
        assertArrayEquals(expectedEncodedBytes, actualEncodedSection.getData().array());

    }
}
