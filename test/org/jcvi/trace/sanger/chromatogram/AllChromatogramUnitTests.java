/*
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram;

import org.jcvi.trace.sanger.chromatogram.scf.AllSCFUnitTests;
import org.jcvi.trace.sanger.chromatogram.ztr.AllZTRUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {

        TestChannel.class,
        TestChannelGroup.class,

        TestBasicChromatogram.class,
        TestEncodedByteData.class,
        TestEncodedShortData.class,
        
        AllSCFUnitTests.class,
        
        AllZTRUnitTests.class,
        
        TestConvertZtr2Scf.class
    }
    )
public class AllChromatogramUnitTests {

}
