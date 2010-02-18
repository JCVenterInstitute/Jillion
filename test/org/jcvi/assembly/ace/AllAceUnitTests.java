/*
 * Created on Sep 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
       TestDefaultPhdInfo.class,
       TestAssembledFrom.class,
       TestAceParserMatchesAce2ContigSingleContig.class,
       TestAceParserMatchesAce2ContigMultipleContigs.class,
       TestAceParserPhdInfo.class,
       TestAceContigBuilderInvalidRead.class,
       TestDefaultAceBestSegment.class,
       TestDefaultAceBestSegmentMap.class,
       TestOntheFlyAceBestSegmentMap.class,
       TestAcePlacedReadAdapter.class,
       TestDefaultAceFileTagMap.class
    }
)
public class AllAceUnitTests {

}
