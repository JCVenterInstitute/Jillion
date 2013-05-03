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
 * Created on Sep 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.chromat.scf.section;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.replay;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.internal.trace.chromat.scf.section.EncodedSection;
import org.jcvi.jillion.internal.trace.chromat.scf.section.Section;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractTestBasesSectionEncoder {



    protected AbstractTestBasesSection sut;
    protected Sequence<Nucleotide> bases;
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
