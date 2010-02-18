/*
 * Created on Sep 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses(
    {
        TestDefaultNucleotideGlyphCodec.class,
        TestNucleotideGlyph.class,
        TestNucleotideGlyphGetAmbiguity.class,
        TestReferenceEncodedNucleotideGlyph.class,
        TestReferenceEncodedNucleotideGlyph_gappedtoUngapped.class,
        TestNucleotideGlyph_GetGlyphsFor.class
    }
)
public class AllNucleotideUnitTests {

}
