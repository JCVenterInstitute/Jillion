package org.jcvi.jillion.align;

import org.jcvi.jillion.align.pairwise.AllPairwiseUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	AllBlosumUnitTests.class,
    	AllPairwiseUnitTests.class,
    	TestNucleotideIndelDetector.class,
    	TestProteinIndelDetector.class,
    }
    )
public class AllAlignmentTests {

}
