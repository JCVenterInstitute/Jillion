/*
 * Created on Jul 29, 2008
 *
 * @author dkatzel
 */
package org.jcvi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {
        TestTestUtilEqualAndHashcodeSame.class,
        TestTestUtilNotEqualAndHashcodeDifferent.class
    }
    )
public class TestTestUtilSuite {

}
