package org.jcvi.jillion.assembly.clc.cas.consed;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
     TestFastaConsedPhdAdaptedIterator.class,
     TestFastqConsedPhdAdaptedIterator.class,
     TestFlowgramConsedPhdAdaptedIterator.class
    }
    )
public class AllPhdAdaptedIteratorTests {

}
