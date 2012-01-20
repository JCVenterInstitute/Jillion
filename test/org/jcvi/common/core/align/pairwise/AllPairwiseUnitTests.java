package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.pairwise.blosom.AllBlosomUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestNucleotideSmithWatermanAligner.class,
    	AllBlosomUnitTests.class,
    	
    	TestAminoAcidSmithWaterman.class
    }
    )
public class AllPairwiseUnitTests {

}
