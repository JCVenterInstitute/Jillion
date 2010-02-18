/*
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref;

import org.jcvi.assembly.annot.ref.ncbi.AllNcbiUnitTests;
import org.jcvi.assembly.annot.ref.writer.AllWriterTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestCodingRegion.class,
        TestDefaultRefGene.class,
        TestStrand.class,
        TestFrame.class,
        TestDefaultExon.class,
        
        AllWriterTests.class,
        AllNcbiUnitTests.class
    }
    )
public class AllRefUnitTests {

}
