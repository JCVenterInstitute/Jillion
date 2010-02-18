/*
 * Created on Apr 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestLRUCache.class,
        TestEmptyIterator.class,
        TestArrayIterable.class,
        TestFileIterator.class,
        TestDepthFirstFileIterator.class,
        TestBreadthFirstFileIterator.class,
        TestStringUtilities.class,
        TestMultipleWrapper.class
    }
    )
public class AllUtilUnitTests {

}
