/*
 * Created on Apr 4, 2008
 *
 * @author dkatzel
 */
package org.jcvi.uid;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestUidService.class,
        TestEuidUidFacade.class

    }
)
public class AllUnitTests {

}
