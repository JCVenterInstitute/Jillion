/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Sep 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.common.core.seq.read.trace.sanger.PositionSequence;
import org.jcvi.common.core.seq.read.trace.sanger.PositionSequenceBuilder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.Confidence;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.DefaultConfidence;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogram;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.SCFChromatogramBuilder;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.header.SCFHeader;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.scf.section.SectionDecoderException;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.qual.QualitySequenceBuilder;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;
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
        SCFChromatogramBuilder c = new SCFChromatogramBuilder(id);
        int currentOffset =0;
        expect(mockHeader.getBasesOffset()).andReturn(currentOffset);
        expect(mockHeader.getNumberOfBases()).andReturn((int)bases.getLength());

         final ByteBuffer expectedRequiredExpectedEncodedBases = sut.createRequiredExpectedEncodedBases();
         DataInputStream in = createInputStreamFrom(expectedRequiredExpectedEncodedBases);
        verifyParser(c, currentOffset, in, 0);

    }
    @Test
    public void validWithSkip() throws Exception{
        SCFChromatogramBuilder c = new SCFChromatogramBuilder(id);
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
        SCFChromatogramBuilder c = new SCFChromatogramBuilder(id);
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
        SCFChromatogramBuilder c = new SCFChromatogramBuilder(id);
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
        SCFChromatogramBuilder c = new SCFChromatogramBuilder(id);
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
        SCFChromatogramBuilder c = new SCFChromatogramBuilder(id);
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
        SCFChromatogramBuilder c = new SCFChromatogramBuilder(id);
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
        SCFChromatogramBuilder c = new SCFChromatogramBuilder(id);
        int currentOffset =0;
        expect(mockHeader.getBasesOffset()).andReturn(currentOffset);
        expect(mockHeader.getNumberOfBases()).andReturn((int)bases.getLength());

         final ByteBuffer expectedRequiredExpectedEncodedBases = sut.createEncodedBasesWithoutInsertionData();
         DataInputStream in = createInputStreamFrom(expectedRequiredExpectedEncodedBases);
         verifyParser(c, currentOffset, in, 0);
    }

    private void verifyParser(SCFChromatogramBuilder c, long currentOffset,
            DataInputStream in, long skipDistance)
            throws SectionDecoderException {
        replay(mockHeader);
        long newOffset =sut.getHandler().decode(in, currentOffset, mockHeader, c);
        verify(mockHeader);
        SCFChromatogram chromatogram = sut.getChromatogram();
        assertEquals(newOffset-currentOffset-skipDistance, (int)bases.getLength()*12);
        assertEquals(chromatogram.getNucleotideSequence().asList(), 
                 c.basecalls().asList());
        PositionSequence encodedPeaks = new PositionSequenceBuilder(c.peaks()).build();
        assertEquals(chromatogram.getPositionSequence(),
                encodedPeaks);
        
        assertEquals(chromatogram.getChannelGroup().getAChannel().getConfidence(),
                new QualitySequenceBuilder(c.aConfidence()).build());
        assertEquals(chromatogram.getChannelGroup().getCChannel().getConfidence(),
        		new QualitySequenceBuilder(c.cConfidence()).build());
        assertEquals(chromatogram.getChannelGroup().getGChannel().getConfidence(),
        new QualitySequenceBuilder(c.gConfidence()).build());
        assertEquals(chromatogram.getChannelGroup().getTChannel().getConfidence(),
        		new QualitySequenceBuilder(c.tConfidence()).build());

        assertOptionalConfidenceEqual(chromatogram.getSubstitutionConfidence(),
                new DefaultConfidence(c.substitutionConfidence()));
        assertOptionalConfidenceEqual(chromatogram.getInsertionConfidence(),
                new DefaultConfidence(c.insertionConfidence()));
        assertOptionalConfidenceEqual(chromatogram.getDeletionConfidence(),
                new DefaultConfidence(c.deletionConfidence()));
    }
    private DataInputStream createInputStreamFrom(
            final ByteBuffer expectedRequiredExpectedEncodedBases) {
        return new DataInputStream(new ByteArrayInputStream(expectedRequiredExpectedEncodedBases.array()));
    }

    private void assertOptionalConfidenceEqual(
            Confidence expected,
            Confidence actual) {
        if(expected !=null){
            assertArrayEquals(expected.getData(), actual.getData());
        }
        else{
            assertArrayEquals(sut.EMPTY_CONFIDENCE, actual.getData());
        }

    }
}
