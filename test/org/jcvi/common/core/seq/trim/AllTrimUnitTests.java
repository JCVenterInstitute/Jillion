package org.jcvi.common.core.seq.trim;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestPrimerDetector.class,
	TestPrimerDetector_ActualData.class,
	TestPrimerDetectorInternalPrimerHit.class
	
}
)
public class AllTrimUnitTests {

}
