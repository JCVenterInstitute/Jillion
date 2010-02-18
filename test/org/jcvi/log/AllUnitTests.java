/*
 * Created on Jul 30, 2007
 *
 * @author dkatzel
 */
package org.jcvi.log;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;



@RunWith(Suite.class)
@SuiteClasses(
    {
        TestGlobalLog.class,
        TestLog4jFacility.class

    }
)
public class AllUnitTests {


}
