package org.jcvi.jillion.sam;

import org.jcvi.jillion.sam.cigar.AllCigarTests;
import org.jcvi.jillion.sam.header.AllSamHeaderTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	AllCigarTests.class,
    	AllSamHeaderTests.class,
    	
    	TestSamRecordFlags.class
    }
    )
public class AllSamTests {

}
