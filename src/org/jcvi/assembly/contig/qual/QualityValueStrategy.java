/*
 * Created on Feb 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig.qual;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public interface QualityValueStrategy {

    PhredQuality getQualityFor(PlacedRead placedRead, EncodedGlyphs<PhredQuality> fullQualities,
            int gappedReadIndex);
}
