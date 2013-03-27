package org.jcvi.jillion_experimental;

import org.jcvi.jillion_experimental.align.AllAlignUnitTests;
import org.jcvi.jillion_experimental.plate.AllPlateUnitTests;
import org.jcvi.jillion_experimental.primer.AllPrimerUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        
             
         AllPrimerUnitTests.class,
        AllPlateUnitTests.class,
        AllAlignUnitTests.class
    }
)
public class AllExperimentalTests {

}
