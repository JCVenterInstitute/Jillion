/*
 * Created on Sep 23, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import static org.easymock.classextension.EasyMock.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.jcvi.trace.sanger.chromatogram.scf.section.AbstractBasesSectionCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.Version2BasesSectionCodec;

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
        expect(mockInputStream.skip(skipDistance)).andReturn(skipDistance);

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
