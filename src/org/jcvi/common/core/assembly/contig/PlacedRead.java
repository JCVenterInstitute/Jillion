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
 * Created on Jan 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig;

import java.util.Map;

import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Placed;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.seq.read.Read;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;

public interface PlacedRead extends Read, Placed<PlacedRead>{


    Map<Integer, Nucleotide> getSnps();
    Range getValidRange();
    Direction getDirection();
    long convertReferenceIndexToValidRangeIndex(long referenceIndex);
    long convertValidRangeIndexToReferenceIndex(long validRangeIndex);
    
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
    
}
