/*
 * Created on Apr 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.assembly.slice.consensus.AllConsensusUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    { 
        TestDefaultSliceLocation.class,
        TestDefaultContigSlice.class,
        
        AllConsensusUnitTests.class
    }
    )
public class AllSliceUnitTests {

}
