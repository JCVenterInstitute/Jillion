package org.jcvi.common.io.zip;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestDefaultZipDataStore.class,
    	TestInMemoryZipDataStore.class,
    	TestInMemoryZipDataStoreStream.class
    }
    )
public class AllZipDataStoreUnitTests {

}
