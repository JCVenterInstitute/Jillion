/*
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import java.nio.ByteBuffer;

import org.jcvi.trace.sanger.chromatogram.scf.SCFUtils;
import org.jcvi.trace.sanger.chromatogram.scf.section.AbstractSampleSectionCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.Version3SampleSectionCodec;

public class TestVersion3SamplesSection  extends AbstractTestSamplesSection{
    @Override
    protected AbstractSampleSectionCodec createSectionHandler() {
        return new Version3SampleSectionCodec();
    }

    @Override
    protected byte[] encodeBytePositions() {
        ByteBuffer result = ByteBuffer.allocate(aSamplesAsBytes.length*4);
        bulkBytePut(result,SCFUtils.deltaDeltaEncode(aSamplesAsBytes));
        bulkBytePut(result,SCFUtils.deltaDeltaEncode(cSamples));
        bulkBytePut(result,SCFUtils.deltaDeltaEncode(gSamples));
        bulkBytePut(result,SCFUtils.deltaDeltaEncode(tSamples));
        return result.array();
    }

    @Override
    protected byte[] encodeShortPositions() {
        ByteBuffer result = ByteBuffer.allocate(aSamplesAsShorts.length*4*2);
        bulkShortPut(result,SCFUtils.deltaDeltaEncode(aSamplesAsShorts));
        bulkShortPut(result,SCFUtils.deltaDeltaEncode(cSamples));
        bulkShortPut(result,SCFUtils.deltaDeltaEncode(gSamples));
        bulkShortPut(result,SCFUtils.deltaDeltaEncode(tSamples));

        return result.array();
    }



    private void bulkShortPut(ByteBuffer buffer, short[] array){
        for(int i=0; i<array.length; i++){
            buffer.putShort(array[i]);
        }
    }

    private void bulkBytePut(ByteBuffer buffer, short[] arrayOfBytes){
        for(int i=0; i<arrayOfBytes.length; i++){
            buffer.put((byte)arrayOfBytes[i]);
        }
    }
}
