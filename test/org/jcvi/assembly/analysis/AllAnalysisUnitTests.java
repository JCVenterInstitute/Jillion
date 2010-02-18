/*
 * Created on Jan 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestAbstractCoverageAnalyzer.class,
        TestReverseComplimentContigAnalyzer.class
    }
    )
public class AllAnalysisUnitTests {

}
