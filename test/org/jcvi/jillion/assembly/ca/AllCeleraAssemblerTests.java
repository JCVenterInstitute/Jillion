package org.jcvi.jillion.assembly.ca;

import org.jcvi.jillion.assembly.ca.asm.AllAsmUnitTests;
import org.jcvi.jillion.assembly.ca.frg.AllFrgUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	AllFrgUnitTests.class,
    	AllAsmUnitTests.class
    }
    )
public class AllCeleraAssemblerTests {

}
