/*
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import java.nio.ByteBuffer;

import org.jcvi.trace.sanger.chromatogram.scf.section.AbstractSampleSectionCodec;
import org.jcvi.trace.sanger.chromatogram.scf.section.Version2SampleSectionCodec;



public class TestVersion2SamplesSection extends AbstractTestSamplesSection{

    /**
    * {@inheritDoc}
    */
    @Override
    protected AbstractSampleSectionCodec createSectionHandler() {
        return new Version2SampleSectionCodec();
    }
    @Override
    protected byte[] encodeShortPositions(){
        ByteBuffer result = ByteBuffer.allocate(aSamplesAsShorts.length*4*2);
        for(int i=0; i< aSamplesAsShorts.length; i++){
          result.putShort(aSamplesAsShorts[i]);
          result.putShort(cSamples[i]);
          result.putShort(gSamples[i]);
          result.putShort(tSamples[i]);
        }
        return result.array();
    }
    @Override
    protected byte[] encodeBytePositions(){

        ByteBuffer result = ByteBuffer.allocate(aSamplesAsBytes.length*4);
        for(int i=0; i< aSamplesAsBytes.length; i++){
          result.put((byte)aSamplesAsBytes[i]);
          result.put((byte)cSamples[i]);
          result.put((byte)gSamples[i]);
          result.put((byte)tSamples[i]);
        }
        return result.array();
    }


}
