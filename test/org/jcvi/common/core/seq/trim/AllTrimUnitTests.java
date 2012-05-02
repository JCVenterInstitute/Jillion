package org.jcvi.common.core.seq.trim;

import org.jcvi.common.core.seq.trim.lucy.AllLucyUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestPrimerDetector.class,
	TestPrimerDetector_ActualData.class,
	TestPrimerDetectorInternalPrimerHit.class,
	AllLucyUnitTests.class
	
}
)
public class AllTrimUnitTests {

}
