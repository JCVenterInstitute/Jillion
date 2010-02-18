/*
 * Created on Sep 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import static org.junit.Assert.*;

import java.io.IOException;

import org.jcvi.trace.sanger.chromatogram.scf.section.EncodedSection;
import org.jcvi.trace.sanger.chromatogram.scf.section.Section;
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
