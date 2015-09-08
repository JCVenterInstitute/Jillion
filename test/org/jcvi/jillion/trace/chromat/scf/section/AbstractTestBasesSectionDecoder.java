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
import static org.easymock.EasyMock.*;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.util.ObjectsUtil;
import org.jcvi.jillion.internal.trace.chromat.scf.header.SCFHeader;
import org.jcvi.jillion.internal.trace.chromat.scf.section.SectionDecoderException;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractTestBasesSectionDecoder {
   
    protected AbstractTestBasesSection sut;
    protected Sequence<Nucleotide> bases;
    protected SCFHeader mockHeader;
    private String id = "id";
    @Before
    public void setupHeader(){

        sut = createAbstractTestBasesSection();
        bases = sut.getEncodedBases();
        mockHeader =sut.getMockHeader();
    }

    protected abstract AbstractTestBasesSection createAbstractTestBasesSection();

    @Test
    public void valid() throws SectionDecoderException{
        ScfChromatogramBuilder c = new ScfChromatogramBuilder(id);
        int currentOffset =0;
        expect(mockHeader.getBasesOffset()).andReturn(currentOffset);
        expect(mockHeader.getNumberOfBases()).andReturn((int)bases.getLength());

         final ByteBuffer expectedRequiredExpectedEncodedBases = sut.createRequiredExpectedEncodedBases();
         DataInputStream in = createInputStreamFrom(expectedRequiredExpectedEncodedBases);
        verifyParser(c, currentOffset, in, 0);

    }
    @Test
    public void validWithSkip() throws Exception{
        ScfChromatogramBuilder c = new ScfChromatogramBuilder(id);
        int currentOffset =0;
        int skipDistance =100;
        expect(mockHeader.getBasesOffset()).andReturn(currentOffset+skipDistance);
        expect(mockHeader.getNumberOfBases()).andReturn((int)bases.getLength());

        InputStream mockInputStream = sut.createValidMockInputStreamWithoutOptionalConfidence(skipDistance);

         replay(mockInputStream);
         verifyParser(c, currentOffset, new DataInputStream(mockInputStream), skipDistance);
         verify(mockInputStream);
    }

    @Test
    public void validOptionalConfidenceDataWithSkip() throws Exception{
        sut.addOptionalConfidences();
        ScfChromatogramBuilder c = new ScfChromatogramBuilder(id);
        int currentOffset =0;
        int skipDistance =100;
        expect(mockHeader.getBasesOffset()).andReturn(currentOffset+skipDistance);
        expect(mockHeader.getNumberOfBases()).andReturn((int)bases.getLength());

        InputStream mockInputStream = sut.createValidMockInputStreamWithOptionalConfidence(skipDistance);

         replay(mockInputStream);
         verifyParser(c, currentOffset, new DataInputStream(mockInputStream), skipDistance);
         verify(mockInputStream);
    }


    @Test
    public void readThrowsIOExceptionShouldWrapInSectionParserException() throws Exception{
        ScfChromatogramBuilder c = new ScfChromatogramBuilder(id);
        int currentOffset =0;
        expect(mockHeader.getBasesOffset()).andReturn(currentOffset);
        expect(mockHeader.getNumberOfBases()).andReturn((int)bases.getLength());

        IOException expectedIOException = new IOException("expected");
        InputStream mockInputStream = createMock(InputStream.class);
         expect(mockInputStream.read()).andThrow(expectedIOException);
         replay(mockInputStream,mockHeader);
         try{
             sut.getHandler().decode(new DataInputStream(mockInputStream), 0, mockHeader, c);
             fail("should wrap IOException in SectionParserException");
         }
         catch(SectionDecoderException e){
             assertEquals(e.getMessage(), "error reading bases section");
             assertEquals(e.getCause(), expectedIOException);
         }
         verify(mockInputStream,mockHeader);
    }

    @Test
    public void validOptionalConfidences() throws SectionDecoderException{
        sut.addOptionalConfidences();
        ScfChromatogramBuilder c = new ScfChromatogramBuilder(id);
        int currentOffset =0;
        expect(mockHeader.getBasesOffset()).andReturn(currentOffset);
        expect(mockHeader.getNumberOfBases()).andReturn((int)bases.getLength());

         final ByteBuffer expectedRequiredExpectedEncodedBases = sut.createEncodedBasesWithAllOptionalData();
         DataInputStream in = createInputStreamFrom(expectedRequiredExpectedEncodedBases);
         verifyParser(c, currentOffset, in, 0);
    }

    @Test
    public void validNoSubstitutionConfidences() throws SectionDecoderException{
        sut.addOptionalConfidences();
        sut.removeSubstitutionConfidence();
        ScfChromatogramBuilder c = new ScfChromatogramBuilder(id);
        int currentOffset =0;
        expect(mockHeader.getBasesOffset()).andReturn(currentOffset);
        expect(mockHeader.getNumberOfBases()).andReturn((int)bases.getLength());

         final ByteBuffer expectedRequiredExpectedEncodedBases = sut.createEncodedBasesWithoutSubstutionData();
         DataInputStream in = createInputStreamFrom(expectedRequiredExpectedEncodedBases);
         verifyParser(c, currentOffset, in, 0);
    }

    @Test
    public void validNoDeletionConfidences() throws SectionDecoderException{
        sut.addOptionalConfidences();
        sut.removeDeletionConfidence();
        ScfChromatogramBuilder c = new ScfChromatogramBuilder(id);
        int currentOffset =0;
        expect(mockHeader.getBasesOffset()).andReturn(currentOffset);
        expect(mockHeader.getNumberOfBases()).andReturn((int)bases.getLength());

         final ByteBuffer expectedRequiredExpectedEncodedBases = sut.createEncodedBasesWithoutDeletionData();
         DataInputStream in = createInputStreamFrom(expectedRequiredExpectedEncodedBases);
         verifyParser(c, currentOffset, in, 0);
    }

    @Test
    public void validNoInsertionConfidences() throws SectionDecoderException{
        sut.addOptionalConfidences();
        sut.removeInsertionConfidence();
        ScfChromatogramBuilder c = new ScfChromatogramBuilder(id);
        int currentOffset =0;
        expect(mockHeader.getBasesOffset()).andReturn(currentOffset);
        expect(mockHeader.getNumberOfBases()).andReturn((int)bases.getLength());

         final ByteBuffer expectedRequiredExpectedEncodedBases = sut.createEncodedBasesWithoutInsertionData();
         DataInputStream in = createInputStreamFrom(expectedRequiredExpectedEncodedBases);
         verifyParser(c, currentOffset, in, 0);
    }

    private void verifyParser(ScfChromatogramBuilder c, long currentOffset,
            DataInputStream in, long skipDistance)
            throws SectionDecoderException {
        replay(mockHeader);
        long newOffset =sut.getHandler().decode(in, currentOffset, mockHeader, c);
        verify(mockHeader);
        ScfChromatogram chromatogram = sut.getChromatogram();
        assertEquals(newOffset-currentOffset-skipDistance, (int)bases.getLength()*12);
        assertEquals(chromatogram.getNucleotideSequence(), 
                 c.basecalls());
        PositionSequence encodedPeaks = new PositionSequenceBuilder(c.peaks()).build();
        assertEquals(chromatogram.getPeakSequence(),
                encodedPeaks);
        
        assertEquals(chromatogram.getChannelGroup().getAChannel().getQualitySequence(),
                new QualitySequenceBuilder(c.aQualities()).build());
        assertEquals(chromatogram.getChannelGroup().getCChannel().getQualitySequence(),
        		new QualitySequenceBuilder(c.cQualities()).build());
        assertEquals(chromatogram.getChannelGroup().getGChannel().getQualitySequence(),
        new QualitySequenceBuilder(c.gQualities()).build());
        assertEquals(chromatogram.getChannelGroup().getTChannel().getQualitySequence(),
        		new QualitySequenceBuilder(c.tQualities()).build());

        ObjectsUtil.nullSafeEquals(chromatogram.getSubstitutionConfidence(),
                c.substitutionConfidence());
        ObjectsUtil.nullSafeEquals(chromatogram.getInsertionConfidence(),
                c.insertionConfidence());
        ObjectsUtil.nullSafeEquals(chromatogram.getDeletionConfidence(),
                c.deletionConfidence());
    }
    private DataInputStream createInputStreamFrom(
            final ByteBuffer expectedRequiredExpectedEncodedBases) {
        return new DataInputStream(new ByteArrayInputStream(expectedRequiredExpectedEncodedBases.array()));
    }

   
}
