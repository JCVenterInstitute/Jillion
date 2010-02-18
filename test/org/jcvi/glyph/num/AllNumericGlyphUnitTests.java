/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.num;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
       TestByteGlyph.class,
       TestByteGlyphFactory.class,
       TestShortGlyph.class,
       TestShortGlyphFactory.class,
       TestEncodedByteGlyph.class,
       TestEncodedShortGlyph.class,
       TestDefaultByteGlyphCodec.class,
       TestDefaultShortGlyphCodec.class
    }
    )
public class AllNumericGlyphUnitTests {

}
