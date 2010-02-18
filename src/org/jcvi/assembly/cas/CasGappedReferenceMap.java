/*
 * Created on Dec 31, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.cas;

import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public interface CasGappedReferenceMap {

    NucleotideEncodedGlyphs getGappedReferenceFor(long referenceId);
}
