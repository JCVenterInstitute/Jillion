/*
 * Created on Jun 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestDefaultTraceArchiveRecord.class,
        TestDefaultTraceArchiveRecordBuilder.class,
        TestCachedTraceArchiveMultiTrace.class,
        TestAbstractFolderTraceArchiveMultiTrace.class,
        TestDefaultFolderTraceArchiveDataStore.class,
        TestActualTraceArchiveXML.class,
        TestDefaultTraceNameConverter.class,
        TestTraceTypeCode.class
    }
    )
public class AllTraceArchiveUnitTests {

}
