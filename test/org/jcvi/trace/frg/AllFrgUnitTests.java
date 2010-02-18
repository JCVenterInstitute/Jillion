/*
 * Created on May 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestDefaultFragment.class,
        TestFrg2Parser.class,
        TestDefaultFragmentDataStore.class,
        TestMemoryMappedFragmentDataStore.class
    }
    )
public class AllFrgUnitTests {

}
