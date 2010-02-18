/*
 * Created on Apr 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import java.util.Map;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.slice.ContigSlice;
import org.jcvi.assembly.slice.Slice;
import org.jcvi.glyph.nuc.NucleotideGlyph;

public interface BasecallCountHistogram {

    Slice getContigSlice();
    Map<NucleotideGlyph, Integer> getHistogram();
}
