/*
 * Created on Feb 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.qual;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {  
        TestAbstractQualityValueStrategy.class,
        TestLowestFlankingQualityValueStrategy.class,
        TestAbstractQualityValueStrategyWithComplimentedRead.class
    }
    )
public class AllQualityValueStrategyUnitTests {

}
