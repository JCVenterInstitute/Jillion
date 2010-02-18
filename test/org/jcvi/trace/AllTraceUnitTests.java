/*
 * Created on Sep 18, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace;

import org.jcvi.trace.fourFiveFour.All454UnitTests;
import org.jcvi.trace.frg.AllFrgUnitTests;
import org.jcvi.trace.sanger.AllSangerTraceUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {    
        All454UnitTests.class,
        AllSangerTraceUnitTests.class,
        AllFrgUnitTests.class,
        
        TestFakeTigrSeqnameMatedComputeLibraryLetter.class,
        TestFakeTigrSeqnameWellPosition.class,
        TestFakeTigrSeqnameMatedTraceIdGeneratorcomputeTigrSeqnamePrefix.class,
        TestDefaultTraceFileNameIdGeneratorStripExtension.class
    }
    )
public class AllTraceUnitTests {

}
