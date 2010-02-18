/*
 * Created on Jan 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    { 
        TestDefaultCoverageRegion.class,
        TestDefaultCoverageMap.class,
        TestDefaultCoverageMapAvgCoverage.class
    }
    )
public class AllCoverageUnitTests {

}
