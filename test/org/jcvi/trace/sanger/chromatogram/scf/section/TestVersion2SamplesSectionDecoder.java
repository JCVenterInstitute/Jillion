/*
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;


public class TestVersion2SamplesSectionDecoder extends AbstractTestSamplesSectionDecoder{

    @Override
    protected AbstractTestSamplesSection createSut() {
        return new TestVersion2SamplesSection();
    }

    @Override
    protected float getVersion() {
        return 2F;
    }

}
