package org.jcvi.jillion.testutils.assembly.cas;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
		{
			TestCasParserTestDouble.class,
			TestCasParserTestDoubleAlignedSequences.class
			
		}
		)
public class AllCasTestUtilsTests {

}
