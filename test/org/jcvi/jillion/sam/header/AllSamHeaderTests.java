package org.jcvi.jillion.sam.header;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestSamProgram.class,
    	TestReadGroup.class,
    	TestSamVersion.class,
    	TestReferenceSequence.class,
    	
    	TestSamHeaderTagKey.class
    }
    )
public class AllSamHeaderTests {

}
