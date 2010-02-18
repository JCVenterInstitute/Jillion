/*
 * Created on Sep 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.section;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {
        TestNullSectionDecoder.class,
        TestCommentSectionEncoder.class,
        TestCommentSectionDecoder.class,
        TestPrivateDataDecoder.class,
        TestPrivateDataEncoder.class,
        TestVersion2SamplesSectionEncoder.class,
        TestVersion2SamplesSectionDecoder.class,
        TestDeltaDeltaEncoding.class,
        TestVersion3SamplesSectionEncoder.class,
        TestVersion3SamplesSectionDecoder.class,
        TestVersion3BasesSectionEncoder.class,
        TestVersion3BasesSectionDecoder.class,
        TestVersion2BasesSectionEncoder.class,
        TestVersion2BasesSectionDecoder.class,

        TestSectionCodecFactoryGetDecoderFor.class,
        TestSectionCodecFactoryGetEncoderFor.class
    }
    )
public class AllSectionUnitTests {

}
