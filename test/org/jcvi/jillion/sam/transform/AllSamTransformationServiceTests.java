package org.jcvi.jillion.sam.transform;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestSamTransformationService.class,
	TestPaddedSamTransformationService.class
})
public class AllSamTransformationServiceTests {

}
