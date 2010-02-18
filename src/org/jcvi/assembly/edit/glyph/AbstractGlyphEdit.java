/*
 * Created on Dec 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.edit.glyph;

import java.util.List;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;

public abstract class AbstractGlyphEdit <G extends Glyph,E extends EncodedGlyphs<G>> implements EncodedGlyphEdit<G,E> {

    protected abstract E encode(List<G> updatedGlyphs);
}
