package org.jcvi.jilion.vcf;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
    	
    	VcfNumberTest.class,
    	VcfFilterTest.class,
    	VcfFormatTest.class,
    	VcfInfoTest.class,
    	VcfNumberMergeTest.class,
    	
    	VcfHeaderTest.class,
    	VcfFileParserTest.class,
    })
public class AllVcfTests {

}
