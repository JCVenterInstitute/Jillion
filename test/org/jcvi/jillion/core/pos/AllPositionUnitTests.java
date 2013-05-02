package org.jcvi.jillion.core.pos;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestPosition.class,
    	TestDefaultPositionCodec.class,
    	TestPositionSequenceBuilder.class
    }
    )
public class AllPositionUnitTests {

}
