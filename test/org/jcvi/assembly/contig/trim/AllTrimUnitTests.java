/*
 * Created on Oct 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.trim;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
     TestQualityClassExtender_left.class,
     TestQualityClassExtender_right.class
    }
    )
public class AllTrimUnitTests {

}
