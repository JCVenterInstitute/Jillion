package org.jcvi.jillion.sam.cigar;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestCigarOperation.class,
    	TestCigarElement.class,
    	TestCigar.class
    }
    )
public class AllCigarTests {

}
