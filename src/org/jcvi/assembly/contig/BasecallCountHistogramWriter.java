/*
 * Created on May 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.io.Closeable;
import java.io.IOException;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.slice.ContigSliceMap;
import org.jcvi.assembly.slice.SliceMap;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public interface BasecallCountHistogramWriter extends Closeable {

    void write(SliceMap sliceMap, NucleotideEncodedGlyphs reference) throws IOException;
}
