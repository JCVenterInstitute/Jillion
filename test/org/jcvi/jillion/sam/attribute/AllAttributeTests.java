package org.jcvi.jillion.sam.attribute;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestReservedAttributeValidator.class,
    	TestChainedSamAttributeValidatorBuilder.class
    }
    )
public class AllAttributeTests {

}
