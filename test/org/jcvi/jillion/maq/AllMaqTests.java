package org.jcvi.jillion.maq;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	TestDefaultBinaryFastqDataStore.class,
    	TestLargeBinaryFastqDataStore.class,
    	TestIndexedBinaryFastqDataStore.class,
    	
    	TestBinaryFastqFileWriter.class
    }
    )
public class AllMaqTests {

}
