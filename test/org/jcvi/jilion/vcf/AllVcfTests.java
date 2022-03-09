package org.jcvi.jilion.vcf;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	VcfHeaderTest.class,
    	VcfNumberTest.class,
    	VcfFilterTest.class,
    	VcfFileParserTest.class,
    })
public class AllVcfTests {

}
