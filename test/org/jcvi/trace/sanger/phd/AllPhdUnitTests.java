/*
 * Created on Mar 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
     TestPhdCodecDecode.class,
     TestDefaultPhdFileDataStore.class,
     TestFakePhdReadParser.class,
     TestBuildArtificialPhd.class
    }
    )
public class AllPhdUnitTests {

}
