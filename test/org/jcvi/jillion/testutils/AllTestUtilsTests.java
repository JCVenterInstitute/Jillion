package org.jcvi.jillion.testutils;

import org.jcvi.jillion.testutils.assembly.cas.AllCasTestUtilsTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
		{
			TestNucleotideSequenceTestUtil.class,
			
			AllCasTestUtilsTests.class
			
		}
		)
public class AllTestUtilsTests {

}
