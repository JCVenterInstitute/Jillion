/*
 * Created on Aug 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestBase64ToChar.class,
        TestReadTriplet.class,
        TestConvertTriplet.class,
        TestBase64Encoder.class
    }
    )
public class AllBase64UnitTests {

}
