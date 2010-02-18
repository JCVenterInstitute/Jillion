/*
 * Created on Sep 19, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;



public class TestVersion3SamplesSectionDecoder extends AbstractTestSamplesSectionDecoder{

    @Override
    protected AbstractTestSamplesSection createSut() {
        return new TestVersion3SamplesSection();
    }

    @Override
    protected float getVersion() {
        return 3F;
    }


}
