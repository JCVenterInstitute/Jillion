/*
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {
        TestSFFUtil_convertFlowgramValues.class,
        TestSFFUtil_paddedBytes.class,
        TestSFFUtil_computeValues.class,
        TestSFFUtil_numberOfIntensities.class
    }
    )
public class AllSFFUtilUnitTests {

}
