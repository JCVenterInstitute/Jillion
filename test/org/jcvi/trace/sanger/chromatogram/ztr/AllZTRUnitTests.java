/*
 * Created on Dec 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr;

import org.jcvi.trace.sanger.chromatogram.ztr.chunk.AllChunkUnitTests;
import org.jcvi.trace.sanger.chromatogram.ztr.data.AllDataUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
     TestZTRUtil.class,
     AllDataUnitTests.class,
     AllChunkUnitTests.class,
     
     TestZTRChromatogramParser.class
    }
    )
public class AllZTRUnitTests {

}
