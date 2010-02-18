/*
 * Created on Sep 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.replay;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.trace.sanger.chromatogram.scf.header.SCFHeader;
import org.jcvi.trace.sanger.chromatogram.scf.section.EncodedSection;
import org.jcvi.trace.sanger.chromatogram.scf.section.Section;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractTestBasesSectionEncoder {



    protected AbstractTestBasesSection sut;
    protected EncodedGlyphs<NucleotideGlyph> bases;
    protected SCFHeader mockHeader;
    @Before
    public void setupHeader(){

        sut = createAbstractTestBasesSection();

        bases = sut.getEncodedBases();
        mockHeader =sut.getMockHeader();
    }

    protected abstract AbstractTestBasesSection createAbstractTestBasesSection();


    @Test
    public void valid() throws IOException{
        mockHeader.setNumberOfBases((int)bases.getLength());
        ByteBuffer encodedBytes =sut.createRequiredExpectedEncodedBases();
        replay(mockHeader);
        EncodedSection actualEncodedSection =sut.getHandler().encode(sut.getChromatogram(), mockHeader);
        assertEquals(Section.BASES,actualEncodedSection.getSection());
        assertArrayEquals(encodedBytes.array(), actualEncodedSection.getData().array());
    }

    @Test
    public void validWithOptionalConfidences() throws IOException{
        sut.addOptionalConfidences();
        mockHeader.setNumberOfBases((int)bases.getLength());
        ByteBuffer encodedBytes = sut.createEncodedBasesWithAllOptionalData();
        replay(mockHeader);
        EncodedSection actualEncodedSection =sut.getHandler().encode(sut.getChromatogram(), mockHeader);
        assertEquals(Section.BASES,actualEncodedSection.getSection());
        assertArrayEquals(encodedBytes.array(), actualEncodedSection.getData().array());
    }
    @Test
    public void validWithEmptySubstitutionConfidences() throws IOException{
        mockHeader.setNumberOfBases((int)bases.getLength());
        ByteBuffer encodedBytes =sut.createEncodedBasesWithoutSubstutionData();
        sut.removeSubstitutionConfidence();
        replay(mockHeader);
        EncodedSection actualEncodedSection =sut.getHandler().encode(sut.getChromatogram(), mockHeader);
        assertEquals(Section.BASES,actualEncodedSection.getSection());
        assertArrayEquals(encodedBytes.array(), actualEncodedSection.getData().array());
    }

    @Test
    public void validWithEmptyInsertionConfidences() throws IOException{
        mockHeader.setNumberOfBases((int)bases.getLength());
        ByteBuffer encodedBytes =sut.createEncodedBasesWithoutInsertionData();

        sut.removeInsertionConfidence();
        replay(mockHeader);
        EncodedSection actualEncodedSection =sut.getHandler().encode(sut.getChromatogram(), mockHeader);
        assertEquals(Section.BASES,actualEncodedSection.getSection());
        assertArrayEquals(encodedBytes.array(), actualEncodedSection.getData().array());
    }

    @Test
    public void validWithEmptyDeletionConfidences() throws IOException{
        mockHeader.setNumberOfBases((int)bases.getLength());
        ByteBuffer encodedBytes =sut.createEncodedBasesWithoutDeletionData();
        sut.removeDeletionConfidence();
        replay(mockHeader);
        EncodedSection actualEncodedSection =sut.getHandler().encode(sut.getChromatogram(), mockHeader);
        assertEquals(Section.BASES,actualEncodedSection.getSection());
        assertArrayEquals(encodedBytes.array(), actualEncodedSection.getData().array());

    }


}
