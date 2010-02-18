/*
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import org.jcvi.assembly.contig.qual.AllQualityValueStrategyUnitTests;
import org.jcvi.assembly.contig.trim.AllTrimUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {  
        TestMultipleContigFileVisitors.class,
        TestContigFileWriter.class,
        TestQualityClassRegion.class,
        TestQualityDifference.class,
        TestDefaultHighQualityDifferenceContigMap.class,
        TestDefaultBasecallCount.class,
        
        AllQualityValueStrategyUnitTests.class,
        TestDefaultQualityClassContigMap.class,
        TestDefaultContigQualityClassComputer.class,
        TestDefaultContigQualityClassComputerComputeQualityFromRegion.class,
        
        AllTrimUnitTests.class
    }
    )
public class AllContigUnitTests {

}
