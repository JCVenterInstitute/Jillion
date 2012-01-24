package org.jcvi.common.core.align.pairwise;

import org.jcvi.common.core.align.pairwise.blosom.AllBlosomUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	AllBlosomUnitTests.class,
    	TestNucleotideSmithWatermanAligner.class,    	
    	TestAminoAcidSmithWaterman.class,
    	
    	TestNucleotideNeedlemanWunschAligner.class,
    	TestAminoAcidNeedlemanWunschAligner.class
    }
    )
public class AllPairwiseUnitTests {

}
