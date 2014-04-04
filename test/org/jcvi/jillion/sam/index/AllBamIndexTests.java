package org.jcvi.jillion.sam.index;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    TestBamIndexParser.class,
    TestBamIndexWriter.class
    }
    )
public class AllBamIndexTests {

}
