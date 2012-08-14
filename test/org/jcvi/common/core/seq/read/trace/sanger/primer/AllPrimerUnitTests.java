package org.jcvi.common.core.seq.read.trace.sanger.primer;

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
public class AllPrimerUnitTests {

}
