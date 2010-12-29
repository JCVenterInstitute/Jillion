/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jan 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.glyph.nuc;

import java.util.List;

import org.jcvi.Range;
import org.jcvi.glyph.EncodedGlyphs;
/**
 * {@code NucleotideEncodedGlyphs} an interface to abstract
 * how {@link NucleotideGlyph}s are encoded in memory.  Nucleotide data
 * can be stored in many different ways depending
 * on the use case and particular circumstances of how this data is to be used.
 * Different encoding implementations can take up more or less memory or require
 * more computations to decode.  This interface hides implementation details
 * regarding the decoding so users don't have to worry about it.
 * @author dkatzel
 *
 *
 */
public interface NucleotideEncodedGlyphs extends EncodedGlyphs<NucleotideGlyph>{
    /**
     * Get a List of all the gap indexes into the gapped basecalls
     * which are Gaps.  The size of the returned list should be
     * the same as the value returned by {@link #getNumberOfGaps()}.
     * @return a List of gap indexes as Integers.
     */
    List<Integer> getGapIndexes();    
    /**
     * Get the number of gaps in the gapped basecalls.
     * @return the number of gaps; will always be {@code >=0}.
     */
    int getNumberOfGaps();
    /**
     * Get the valid {@link Range} which is ungapped "good" part of the basecalls.  Depending
     * on what this {@link NucleotideEncodedGlyphs} represents can change the 
     * meaning of valid range some possible meanings include:
     * <ul>
     * <li>the high quality region<li>
     * <li>the region that aligns to a reference</li>
     * <li>the region used to compute assembly consensus</li>
     * </ul>
     * @return
     */
    Range getValidRange();
    /**
     * Convert the given gapped valid range index into its
     * ungapped equivalent.
     * @param gappedValidRangeIndex the gapped valid range index to convert.
     * @return the ungapped equivalent to the given gapped index.
     * @see #convertUngappedValidRangeIndexToGappedValidRangeIndex(int)
     */
    int convertGappedValidRangeIndexToUngappedValidRangeIndex(int gappedValidRangeIndex);
    /**
     * Convert the given ungapped valid range index into its
     * gapped equivalent.
     * @param ungappedValidRangeIndex the ungapped valid range index to convert.
     * @return the gapped equivalent to the given ungapped index.
     * @see #convertGappedValidRangeIndexToUngappedValidRangeIndex(int)
     */
    int convertUngappedValidRangeIndexToGappedValidRangeIndex(int ungappedValidRangeIndex);
    /**
     * Convert the given gapped valid range into an ungapped valid range.
     * @param gappedValidRange the gapped valid range to convert.
     * @return the ungapped equivalent.
     */
    Range convertGappedValidRangeToUngappedValidRange(Range gappedValidRange);
    
    Range convertUngappedValidRangeToGappedValidRange(Range ungappedValidRange);
    
    /**
     * Is the {@link NucleotideGlyph} at the given gapped index a gap?
     * @param gappedIndex the gappedIndex to check.
     * @return {@code true} is it is a gap; {@code false} otherwise.
     */
    boolean isGap(int gappedIndex);
    /**
     * Get the number of {@link NucleotideGlyph}s in this {@link NucleotideEncodedGlyphs} 
     * that are not gaps.
     * @return the number of non gaps as a long.
     */
    long getUngappedLength();
    /**
     * Decode only the ungapped bases and return them as a List of
     * {@link NucleotideGlyph}s.
     * @return a List of {@link NucleotideGlyph}s containing only the 
     * ungapped bases.
     */
    List<NucleotideGlyph> decodeUngapped();
    /**
     * Compute the number of gaps in the valid range until AND INCLUDING the given
     * gapped index.
     * @param gappedValidRangeIndex the index to count the number of gaps until.
     * @return the number of gaps in the valid range until AND INCLUDING the given
     * gapped index.
     */
    int computeNumberOfInclusiveGapsInGappedValidRangeUntil(int gappedValidRangeIndex);
    /**
     * Compute the number of gaps in the valid range until AND INCLUDING the given
     * UNgapped index.
     * @param ungappedValidRangeIndex the index to count the number of gaps until.
     * @return the number of gaps in the valid range until AND INCLUDING the given
     * UNgapped index.
     */
    int computeNumberOfInclusiveGapsInUngappedValidRangeUntil(int ungappedValidRangeIndex);
}
