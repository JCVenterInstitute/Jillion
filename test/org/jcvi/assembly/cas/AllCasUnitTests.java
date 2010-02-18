/*
 * Created on Jan 14, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import org.jcvi.assembly.cas.alignment.AllCasAlignmentUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestCasUtil.class,
        
        TestDefaultCasGappedReferenceMap.class,
        
        AllCasAlignmentUnitTests.class
    }
    )
public class AllCasUnitTests {

}
