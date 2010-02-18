/*
 * Created on Aug 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.fileServer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestReadOnlyDirectoryFileServer.class,
        TestReadWriteDirectoryFileServer.class,
        TestResourceFileServer.class,
        TestFTPFileServer.class
    }
    )
public class AllFileServerUnitTests {

}
