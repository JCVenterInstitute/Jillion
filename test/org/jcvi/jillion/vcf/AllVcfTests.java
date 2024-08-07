package org.jcvi.jillion.vcf;

import org.jcvi.jillion.vcf.dsl.AllVcfDSLTests;
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
    	VcfContigInfoTest.class,
    	
    	VcfHeaderTest.class,
    	VcfFileParserTest.class,
    	
    	AllVcfDSLTests.class
    })
public class AllVcfTests {

}
