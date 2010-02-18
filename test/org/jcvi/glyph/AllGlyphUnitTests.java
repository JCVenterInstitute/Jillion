/*
 * Created on Sep 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.glyph;


import org.jcvi.glyph.encoder.AllEnocderUnitTests;
import org.jcvi.glyph.nuc.AllNucleotideUnitTests;
import org.jcvi.glyph.num.AllNumericGlyphUnitTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {

       AllNucleotideUnitTests.class,
       AllNumericGlyphUnitTests.class,
       TestRunLength.class,
       TestRunLengthEncoder.class,
       AllEnocderUnitTests.class
    }
)
public class AllGlyphUnitTests {

}
