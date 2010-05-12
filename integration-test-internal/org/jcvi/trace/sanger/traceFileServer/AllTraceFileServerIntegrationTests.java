/*
 * Created on Sep 15, 2009
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
        TestTigrTraceFileServer.class,
        TestTraceFileServerSecure.class
    }
    )
public class AllTraceFileServerIntegrationTests {

}
