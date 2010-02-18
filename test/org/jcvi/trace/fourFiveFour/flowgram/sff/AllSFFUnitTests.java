/*
 * Created on Oct 7, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {
        AllSFFUtilUnitTests.class,
        TestDefaultReadData.class,
        TestDefaultSFFReadHeader.class,
        TestDefaultSFFCommonHeader.class,
        TestSFFReadHeaderCodec_decode.class,
        TestSFFReadHeaderCodec_encoder.class,
        TestDefaultSFFReadDataCodec_decode.class,
        TestDefaultSFFReadDataCodec_encode.class,
        TestDefaultSFFCommonHeaderCodec_decode.class,
        TestDefaultSFFCommonHeaderCodec_encode.class,
        TestSFFFlowgram.class,
        TestSFFCodecParseActualSFFFile.class,
        TestLargeSffFileDataStore.class,
        TestFlowIndexOverflow.class,
        TestNewblerSuffixNameConverter.class,
        TestDefaultSffFlowgramDataStore.class,
        TestSFFUtil_getTrimRange.class,
        TestSffVisitorWriter.class,
        TestH2NucleotideSffDataStore.class,
        TestH2QualitySffDataStore.class
        
    }
    )
public class AllSFFUnitTests {

}
