/*
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import org.jcvi.glyph.num.EncodedShortGlyph;

public interface PeakMap {

    EncodedShortGlyph getPeaksFor(String id);
}
