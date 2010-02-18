/*
 * Created on Feb 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.encoder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
    {
        TestRunLengthEncodedGlyphCodec.class,
        TestIllegalEncodedValueException.class,
        TestTigrQualitiesEncoder.class,
        TestTigrQualitiesEncoderCodec.class,
        TestTigrPeaksEncoder.class,
        TestTigrPeaksEncoderCodec.class
    }
    )
public class AllEnocderUnitTests {

}
