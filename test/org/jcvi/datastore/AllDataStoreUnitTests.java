/*
 * Created on Apr 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    { 
        TestDataStoreIterator.class,
     TestDefaultContigFileDataStore.class,
     TestMemoryMappedContigFileDataStore.class,
     TestDefaultAceFileDataStore.class,
     TestMemoryMappedAceFileDataStore.class,
     TestCachedDataStore.class,
     TestSimpleDataStore.class,
     TestMultipleDataStoreWrapper.class
    }
    )
public class AllDataStoreUnitTests {

}
