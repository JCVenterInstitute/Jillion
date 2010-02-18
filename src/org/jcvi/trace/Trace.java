/*
 * Created on Sep 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.trace;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public interface Trace {

    NucleotideEncodedGlyphs getBasecalls();
    
    EncodedGlyphs<PhredQuality> getQualities();
}
