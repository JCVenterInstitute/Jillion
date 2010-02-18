/*
 * Created on Mar 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

import org.jcvi.Range;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.sequence.Library;
import org.jcvi.sequence.Read;
import org.jcvi.trace.Trace;

public interface Fragment extends Trace, Read<NucleotideEncodedGlyphs>{

    String getId();
    Range getValidRange();
    Range getVectorClearRange();
    String getComment();
    Library getLibrary();
    String getLibraryId();
}
