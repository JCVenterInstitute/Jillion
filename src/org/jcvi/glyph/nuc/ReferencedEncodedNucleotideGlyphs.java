/*
 * Created on Jan 23, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import java.util.Map;

public interface ReferencedEncodedNucleotideGlyphs extends NucleotideEncodedGlyphs{

    Map<Integer, NucleotideGlyph> getSnps();
}
