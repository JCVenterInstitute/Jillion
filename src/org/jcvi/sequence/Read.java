/*
 * Created on Sep 3, 2008
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;


public interface Read<T extends NucleotideEncodedGlyphs> {

    String getId();
    T getEncodedGlyphs();
    long getLength();
}
