/*
 * Created on Apr 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import java.util.List;

import org.jcvi.assembly.Location;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public interface ContigSlice<T extends PlacedRead> {
    List<SliceLocation<T>> getUnderlyingSliceLocations();
    Location<EncodedGlyphs<NucleotideGlyph>> getConsensus();
}
