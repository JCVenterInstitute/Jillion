package org.jcvi.jillion.sam;

import org.jcvi.jillion.sam.cigar.AllCigarTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	AllCigarTests.class
    }
    )
public class AllSamTests {

}
