/*
 * Created on Jan 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    { 
        TestSequenceDirection.class,
        TestPeaks.class,
        TestPeaksUtil.class,
        TestDefaultConfidence.class,
        TestTigrPositionFileParser.class,
        TestMateOrientation.class,
        TestDefaultLibrary.class
    }
    )
public class AllSequencingTests {

}
