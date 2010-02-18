/*
 * Created on Jan 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.qualClass;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestQualityClassBuilder.class,
        TestQualityClassLookupByValue.class
    }
    )
public class AllQualityClassUnitTests {

}
