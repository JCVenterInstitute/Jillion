/*
 * Created on Aug 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceFileServer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestBasicJCVIAuthorizer.class,
        TestJTraceFilename.class,
        TestJcviTraceFileServer.class,
        TestReadWriteJcviTraceFileServer.class,
        TestJcviTraceFileServer_getting.class
    }
    )
public class AllJcviTraceFileServerUnitTests {

}
