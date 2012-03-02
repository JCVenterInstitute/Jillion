package org.jcvi.common.core.assembly.asm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    { 
     TestAsmUtil.class	,
     TestDefaultAsmContigDataStore.class,
     TestIndexedAsmContigDataStore.class,
     
     TestDefaultUnitigDataStore.class,
     TestIndexedUnitigDataStore.class
    }
    )
public class AllAsmUnitTests {

}
