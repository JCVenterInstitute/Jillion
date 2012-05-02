package org.jcvi.common.core.seq.trim.lucy;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestLucyQualityTrimmer.class,
	TestLucyTrimDataStore.class
}
)
public class AllLucyUnitTests {

}
