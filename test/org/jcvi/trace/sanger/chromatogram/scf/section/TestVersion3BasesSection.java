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
 * Created on Sep 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.testUtil.EasyMockUtil;
import org.jcvi.trace.sanger.chromatogram.scf.section.AbstractBasesSectionCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.Version3BasesSectionCodec;


public class TestVersion3BasesSection extends AbstractTestBasesSection{


    @Override
    protected ByteBuffer createRequiredExpectedEncodedBases(){
        ByteBuffer result = ByteBuffer.wrap(new byte[(int)encodedBases.getLength()*12]);
        bulkPutAsInts(result, peaks);
        result.put(aConfidence);
        result.put(cConfidence);
        result.put(gConfidence);
        result.put(tConfidence);
        result.put(DECODED_BASES.getBytes());
        return result;
    }


    @Override
    protected InputStream createValidMockInputStreamWithoutOptionalConfidence(long skipDistance)
    throws IOException {
        InputStream mockInputStream = createMock(InputStream.class);
         expect(mockInputStream.skip(skipDistance)).andReturn(skipDistance);
         //do peaks
         expectPeakReads(mockInputStream);
         expectRequiredConfidenceReads(mockInputStream);
         expectBasesRead(mockInputStream);
         //empty substitution, insert, and deletion confidences
         expectEmptyConfidenceData(mockInputStream);
         expectEmptyConfidenceData(mockInputStream);
         expectEmptyConfidenceData(mockInputStream);
        return mockInputStream;
    }
    @Override
    protected InputStream createValidMockInputStreamWithOptionalConfidence(long skipDistance)
    throws IOException {
        InputStream mockInputStream = createMock(InputStream.class);
         expect(mockInputStream.skip(skipDistance)).andReturn(skipDistance);
         //do peaks
         expectPeakReads(mockInputStream);
         expectRequiredConfidenceReads(mockInputStream);
         expectBasesRead(mockInputStream);
         expectFullConfidenceRead(mockInputStream, subsitutionConfidence);
         expectFullConfidenceRead(mockInputStream, insertionConfidence);
         expectFullConfidenceRead(mockInputStream, deletionConfidence);

        return mockInputStream;
    }
    private void expectEmptyConfidenceData(InputStream mockInputStream) throws IOException {
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq((int)encodedBases.getLength()))).andAnswer(
                EasyMockUtil.writeArrayToInputStream(EMPTY_CONFIDENCE));

    }
    private void expectBasesRead(InputStream mockInputStream) throws IOException {
        expect(mockInputStream.read(isA(byte[].class), eq(0), eq((int)encodedBases.getLength()))).andAnswer(
                EasyMockUtil.writeArrayToInputStream(DECODED_BASES.getBytes()));

    }
    private void expectFullConfidenceRead(InputStream mockInputStream,byte[] confidence) throws IOException{

        expect(mockInputStream.read(isA(byte[].class), eq(0), eq(confidence.length))).andAnswer(
                EasyMockUtil.writeArrayToInputStream(confidence));
    }
    private void expectRequiredConfidenceReads(InputStream mockInputStream) throws IOException {
        expectFullConfidenceRead(mockInputStream, aConfidence);
        expectFullConfidenceRead(mockInputStream, cConfidence);
        expectFullConfidenceRead(mockInputStream, gConfidence);
        expectFullConfidenceRead(mockInputStream, tConfidence);


    }

    @Override
    protected ByteBuffer createEncodedBasesWithAllOptionalData() {
        final ByteBuffer expectedRequiredExpectedEncodedBases = createRequiredExpectedEncodedBases();
        expectedRequiredExpectedEncodedBases.put(subsitutionConfidence);
        expectedRequiredExpectedEncodedBases.put(insertionConfidence);
        expectedRequiredExpectedEncodedBases.put(deletionConfidence);
        return expectedRequiredExpectedEncodedBases;
    }
    @Override
    protected ByteBuffer createEncodedBasesWithoutSubstutionData() {
        final ByteBuffer expectedRequiredExpectedEncodedBases = createRequiredExpectedEncodedBases();
        expectedRequiredExpectedEncodedBases.put(EMPTY_CONFIDENCE);
        expectedRequiredExpectedEncodedBases.put(insertionConfidence);
        expectedRequiredExpectedEncodedBases.put(deletionConfidence);
        return expectedRequiredExpectedEncodedBases;
    }
    @Override
    protected ByteBuffer createEncodedBasesWithoutDeletionData() {
        final ByteBuffer expectedRequiredExpectedEncodedBases = createRequiredExpectedEncodedBases();
        expectedRequiredExpectedEncodedBases.put(subsitutionConfidence);
        expectedRequiredExpectedEncodedBases.put(insertionConfidence);
        expectedRequiredExpectedEncodedBases.put(EMPTY_CONFIDENCE);
        return expectedRequiredExpectedEncodedBases;
    }
    @Override
    protected ByteBuffer createEncodedBasesWithoutInsertionData() {
        final ByteBuffer expectedRequiredExpectedEncodedBases = createRequiredExpectedEncodedBases();
        expectedRequiredExpectedEncodedBases.put(subsitutionConfidence);
        expectedRequiredExpectedEncodedBases.put(EMPTY_CONFIDENCE);
        expectedRequiredExpectedEncodedBases.put(deletionConfidence);
        return expectedRequiredExpectedEncodedBases;
    }


    @Override
    protected AbstractBasesSectionCodec createAbstractBasesSectionHandler() {
        return new Version3BasesSectionCodec();
    }
}
