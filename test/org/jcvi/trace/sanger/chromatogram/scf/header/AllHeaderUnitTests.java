/*
 * Created on Sep 24, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.scf.header;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {
        TestDefaultSCFHeader.class,
        TestDefaultSCFHeaderCodec.class
    }
    )
public class AllHeaderUnitTests {

}
