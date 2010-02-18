/*
 * Created on Sep 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.io;

import org.jcvi.io.fileServer.AllFileServerUnitTests;
import org.jcvi.io.idReader.AllIdReaderUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestIOUtil_closeAndIgnore.class,
        TestIOUtil_blockingSkip.class,
        TestIOUtil_readByteArray.class,
        TestIOUtil_readShortArray.class,
        TestIOUtil_convertSignedToUnsigned.class,
        TestIOUtil_UnsignedByteArray.class,
        TestIOUtil_deleteDir.class,
        TestIOUtil_convertToUnsignedByteArray.class,
        TestCheckSumUtil.class,
        
        AllBase64UnitTests.class,
        AllIdReaderUnitTests.class,
        
        AllFileServerUnitTests.class
        
    }
)
public class AllIOUnitTests {

}
