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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Sep 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sanger.chromat.scf.section;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.jcvi.jillion.internal.trace.sanger.chromat.scf.section.AbstractBasesSectionCodec;
import org.jcvi.jillion.internal.trace.sanger.chromat.scf.section.Version2BasesSectionCodec;

public class TestVersion2BasesSection extends AbstractTestBasesSection{

    @Override
    protected ByteBuffer createEncodedBasesWithAllOptionalData() {
        return createEncodedData(true, true, true);
    }

    @Override
    protected ByteBuffer createEncodedBasesWithoutDeletionData() {
        return createEncodedData(true, true, false);
    }

    @Override
    protected ByteBuffer createEncodedBasesWithoutInsertionData() {
        return createEncodedData(true, false, true);
    }

    @Override
    protected ByteBuffer createEncodedBasesWithoutSubstutionData() {
        return createEncodedData(false, true, true);
    }

    @Override
    protected ByteBuffer createRequiredExpectedEncodedBases() {
        return createEncodedData(false, false, false);
    }

    private ByteBuffer createEncodedData(boolean includeSubstitution,boolean includeInsertion, boolean includeDeletion) {
        ByteBuffer result = ByteBuffer.wrap(new byte[(int)encodedBases.getLength()*12]);
        for(int i=0; i< encodedBases.getLength(); i++){
            result.putInt(peaks[i]);
            result.put(aConfidence[i]);
            result.put(cConfidence[i]);
            result.put(gConfidence[i]);
            result.put(tConfidence[i]);
            result.put((byte)encodedBases.get(i).getCharacter().charValue());
            result.put(includeSubstitution ? subsitutionConfidence[i] : (byte)0);
            result.put(includeInsertion ? insertionConfidence[i] : (byte)0);
            result.put(includeDeletion ? deletionConfidence[i] : (byte)0);

        }
        return result;
    }

    @Override
    protected InputStream createValidMockInputStreamWithOptionalConfidence(
            long skipDistance) throws IOException {
        return createValidMockInputStream(skipDistance, true);
    }

    private InputStream createValidMockInputStream(long skipDistance,
            boolean includeOptionalConfidence) throws IOException {
        InputStream mockInputStream = createMock(InputStream.class);
        expect(mockInputStream.read()).andReturn(1);
        expect(mockInputStream.skip(skipDistance-1)).andReturn(skipDistance-1);

        for(int i=0; i<encodedBases.getLength(); i++){
            expectRead(mockInputStream, i,includeOptionalConfidence);
        }
        return mockInputStream;
    }

    private void expectRead(InputStream mockInputStream, int index,boolean includeOptionalConfidence) throws IOException {
        expectPeakRead(mockInputStream, peaks[index]);

        expect(mockInputStream.read()).andReturn((int)aConfidence[index]);
        expect(mockInputStream.read()).andReturn((int)cConfidence[index]);
        expect(mockInputStream.read()).andReturn((int)gConfidence[index]);
        expect(mockInputStream.read()).andReturn((int)tConfidence[index]);
        expect(mockInputStream.read()).andReturn((int)encodedBases.get(index).getCharacter().charValue());
        if(includeOptionalConfidence){
            expect(mockInputStream.read()).andReturn((int)subsitutionConfidence[index]);
            expect(mockInputStream.read()).andReturn((int)insertionConfidence[index]);
            expect(mockInputStream.read()).andReturn((int)deletionConfidence[index]);
        }
        else{
            expect(mockInputStream.read()).andReturn(0);
            expect(mockInputStream.read()).andReturn(0);
            expect(mockInputStream.read()).andReturn(0);
        }

    }

    @Override
    protected InputStream createValidMockInputStreamWithoutOptionalConfidence(
            long skipDistance) throws IOException {
        return createValidMockInputStream(skipDistance, false);
    }

    @Override
    protected AbstractBasesSectionCodec createAbstractBasesSectionHandler() {
        return new Version2BasesSectionCodec();
    }

}
