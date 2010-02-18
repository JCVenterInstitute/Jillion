/*
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import org.jcvi.assembly.agp.AllAgpUnitTests;
import org.jcvi.assembly.analysis.AllAnalysisUnitTests;
import org.jcvi.assembly.annot.ref.AllRefUnitTests;
import org.jcvi.assembly.cas.AllCasUnitTests;
import org.jcvi.assembly.contig.AllContigUnitTests;
import org.jcvi.assembly.coverage.AllCoverageUnitTests;
import org.jcvi.assembly.slice.AllSliceUnitTests;
import org.jcvi.assembly.tasm.AllTasmUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {        
        TestDefaultPlacedContigClone.class,
        TestDefaultPlacedRead.class,
        TestDefaultLocation.class,
        TestSplitReferenceEncodedNucleotideGlyphs.class,
        TestSplitPlacedRead.class,
        TestSectionOfPlacedRead.class,
        
        AllSliceUnitTests.class,
        AllAnalysisUnitTests.class,
        AllRefUnitTests.class,
        AllContigUnitTests.class,
        AllCoverageUnitTests.class,
        AllTasmUnitTests.class,
        AllAgpUnitTests.class,
        AllCasUnitTests.class,
        
        TestAssemblyUtil_gappedfullRange.class,
        TestAssemblyUtil_reverseComplimentRange.class
    }
    )
public class AllAssemblyUnitTests {

}
