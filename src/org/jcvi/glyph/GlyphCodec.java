/*
 * Created on Jan 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph;

import java.util.List;


public interface GlyphCodec<T extends Glyph> {

    byte[] encode(List<T> glyphs);
    
    List<T> decode(byte[] encodedGlyphs);
    T decode(byte[] encodedGlyphs, int index);
    
    int decodedLengthOf(byte[] encodedGlyphs);
}
