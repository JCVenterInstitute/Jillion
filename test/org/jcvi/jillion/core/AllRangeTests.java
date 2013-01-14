package org.jcvi.jillion.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	 TestRange.class,
    	 TestRangeWithEdgeCases.class,
         TestDirectedRange.class,
         TestRangeArrivalComparator.class,
         TestRangeDepartureComparator.class,
         TestEmptyRange.class,
         TestRangeCompliment.class,
         TestRangeIterator.class,
         TestRangeComparatorShortestToLongest.class,
         TestRangeComparatorLongestToShortest.class
    }
    )
public class AllRangeTests {

}
