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
