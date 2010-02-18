/*
 * Created on Mar 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import static org.junit.Assert.assertEquals;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class AssemblyTestUtil {

    public static void assertPlacedReadCorrect(PlacedRead expected,
            PlacedRead actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getStart(), actual.getStart());
        assertEquals(expected.getLength(), actual.getLength());
        assertEquals(expected.getSequenceDirection(), actual.getSequenceDirection());
        final NucleotideEncodedGlyphs expectedEncodedGlyphs = expected.getEncodedGlyphs();
        final NucleotideEncodedGlyphs actualEncodedGlyphs = actual.getEncodedGlyphs();
        assertEquals(expectedEncodedGlyphs.decode(), actualEncodedGlyphs.decode());
        assertEquals(expectedEncodedGlyphs.getValidRange(), actualEncodedGlyphs.getValidRange());
        
        
    }
}
