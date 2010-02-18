/*
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import java.util.List;

import org.jcvi.Range;
import org.jcvi.glyph.EncodedGlyphs;

public interface NucleotideEncodedGlyphs extends EncodedGlyphs<NucleotideGlyph>{

    List<Integer> getGapIndexes();    
    Range getValidRange();
    int convertGappedValidRangeIndexToUngappedValidRangeIndex(int gappedValidRangeIndex);
    int convertUngappedValidRangeIndexToGappedValidRangeIndex(int ungappedValidRangeIndex);
    boolean isGap(int index);
    long getUngappedLength();
    List<NucleotideGlyph> decodeUngapped();
}
