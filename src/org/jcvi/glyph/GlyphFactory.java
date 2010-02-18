/*
 * Created on Jan 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph;

import java.util.List;

public interface GlyphFactory<T extends Glyph, V> {

    T getGlyphFor(V s);
    List<T> getGlyphsFor(List<V> s);
}
