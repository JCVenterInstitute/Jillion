/*
 * Created on May 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.idReader;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        AllIdParserUnitTests.class,
        
        TestCommaSeparatedIdReader.class,
        TestDefaultFileIdParser.class
    }
    )
public class AllIdReaderUnitTests {

}
