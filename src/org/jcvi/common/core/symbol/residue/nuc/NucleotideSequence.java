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
package org.jcvi.common.core.symbol.residue.nuc;

import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.symbol.Sequence;
/**
 * {@code NucleotideSequence} an interface to abstract
 * how {@link Nucleotide}s are encoded in memory.  Nucleotide data
 * can be stored in many different ways depending
 * on the use case and particular circumstances of how this data is to be used.
 * Different encoding implementations can take up more or less memory or require
 * more computations to decode.  This interface hides implementation details
 * regarding the decoding so users don't have to worry about it.
 * @author dkatzel
 *
 *
 */
public interface NucleotideSequence extends Sequence<Nucleotide>{
    /**
     * Get a List of all the gap indexes into the gapped basecalls
     * which are Gaps.  The size of the returned list should be
     * the same as the value returned by {@link #getNumberOfGaps()}.
     * @return a List of gap indexes as Integers.
     */
    List<Integer> getGapIndexes();    
    /**
     * Get the number of gaps in this sequence.
     * @return the number of gaps; will always be {@code >=0}.
     */
    int getNumberOfGaps();
    /**
     * Get the valid {@link Range} which is ungapped "good" part of the basecalls.  Depending
     * on what this {@link NucleotideSequence} represents can change the 
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
     * Is the {@link Nucleotide} at the given gapped index a gap?
     * @param gappedIndex the gappedIndex to check.
     * @return {@code true} is it is a gap; {@code false} otherwise.
     */
    boolean isGap(int gappedIndex);
    /**
     * Get the number of {@link Nucleotide}s in this {@link NucleotideSequence} 
     * that are not gaps.
     * @return the number of non gaps as a long.
     */
    long getUngappedLength();
    /**
     * Decode only the ungapped bases and return them as a List of
     * {@link Nucleotide}s.
     * @return a List of {@link Nucleotide}s containing only the 
     * ungapped bases.
     */
    List<Nucleotide> decodeUngapped();
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
    
    int toUngappedIndex(int gappedIndex);
    
    int toGappedIndex(int ungappedIndex);
}
