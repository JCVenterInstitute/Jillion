/*
 * Created on Jan 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;

import java.util.Map;

import org.jcvi.Range;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.sequence.Read;
import org.jcvi.sequence.SequenceDirection;

public interface PlacedRead extends Read, Placed{


    Map<Integer, NucleotideGlyph> getSnps();
    Range getValidRange();
    SequenceDirection getSequenceDirection();
    long convertReferenceIndexToValidRangeIndex(long referenceIndex);
    long convertValidRangeIndexToReferenceIndex(long validRangeIndex);
}