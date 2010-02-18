/*
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;




public class TestVersion3SamplesSectionEncoder extends AbstractTestSampleSectionEncoder{

    @Override
    protected AbstractTestSamplesSection createSut() {
        return new TestVersion3SamplesSection();
    }



}
