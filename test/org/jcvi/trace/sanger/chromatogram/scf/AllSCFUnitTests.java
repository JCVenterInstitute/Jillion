/*
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf;

import org.jcvi.trace.sanger.chromatogram.scf.header.AllHeaderUnitTests;
import org.jcvi.trace.sanger.chromatogram.scf.position.AllPositionStrategyUnitTests;
import org.jcvi.trace.sanger.chromatogram.scf.section.AllSectionUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {
        TestPrivateData.class,
        TestSCFChromatogram.class,
        TestAbstractSCFCodecDecoder.class,
        TestVersion2SCFCodecEncoder.class,
        TestVersion3SCFCodecEncoder.class,
       AllHeaderUnitTests.class,
       AllPositionStrategyUnitTests.class,
       AllSectionUnitTests.class,

       TestActualSCFCodec.class,
       TestSCFChromatogramWithGaps.class
    }
    )
public class AllSCFUnitTests {

}
