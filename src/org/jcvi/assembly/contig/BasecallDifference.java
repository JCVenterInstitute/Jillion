/*
 * Created on Apr 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import org.jcvi.assembly.Location;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public interface BasecallDifference {

    Location<PlacedRead> getReadLocation();
    Location<EncodedGlyphs<NucleotideGlyph>> getReferenceLocation();
}
