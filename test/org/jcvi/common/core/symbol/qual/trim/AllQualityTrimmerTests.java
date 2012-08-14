package org.jcvi.common.core.symbol.qual.trim;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestBwaQualityTrimmer.class,
    	TestLucyQualityTrimmer.class
    }
    )
public class AllQualityTrimmerTests {

}
