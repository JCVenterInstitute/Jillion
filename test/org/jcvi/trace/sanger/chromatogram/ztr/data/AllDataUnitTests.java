/*
 * Created on Dec 22, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.chromatogram.ztr.data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
     TestDataFactory.class,
     TestRawData.class,
     TestRunLengthEncodedData.class,
     TestSixteenBitToEightBitData.class,
     TestThirtyTowToEightBitData.class,
     TestDelta8Data.class,
     TestDelta16Data.class,
     TestDelta32Data.class,
     TestZLibData.class,
     TestFollowData.class
    }
    )
public class AllDataUnitTests {

}
