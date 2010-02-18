/*
 * Created on Mar 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger;

import org.jcvi.trace.sanger.chromatogram.AllChromatogramUnitTests;
import org.jcvi.trace.sanger.phd.AllPhdUnitTests;
import org.jcvi.trace.sanger.traceArchive.AllTraceArchiveUnitTests;
import org.jcvi.trace.sanger.traceFileServer.AllJcviTraceFileServerUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        AllPhdUnitTests.class,
        AllChromatogramUnitTests.class,
        TestSangerTraceParser.class,
        AllTraceArchiveUnitTests.class,
        AllJcviTraceFileServerUnitTests.class
    }
    )
public class AllSangerTraceUnitTests {

}
