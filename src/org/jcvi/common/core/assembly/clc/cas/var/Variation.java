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

package org.jcvi.common.core.assembly.clc.cas.var;

import java.util.List;
import java.util.Map;

import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;

/**
 * {@code Variation} represents a single row in 
 * a variations file produced by the {@code find_variations} program.
 * @author dkatzel
 *
 *
 */
public interface Variation extends Comparable<Variation>{
    /**
     * {@code Type} describes the type of change
     * this variation is compared to the reference.
     * @author dkatzel
     *
     *
     */
    public enum Type{
        /**
         * Consensus did not change with respect to 
         * its reference (but underlying reads have variations).
         */
        NO_CHANGE,
        /**
         * Enough underlying reads are different from the reference
         * that the consensus changed.
         */
        DIFFERENCE,
        /**
         * This consensus has a deletion with respect to the reference.
         */
        DELETION,
        /**
         * This consensus has a insertion with respect to the reference.
         */
        INSERT;
        /**
         * Parse the type from the string name inside the variations file.
         * @param typeName the type as a string.
         * @return the {@link Type} that corresponds to the given typeName.
         * @throws NullPointerException if typeName is null
         * @throws IllegalArgumentException if the given typeName does
         * not correspond to a known {@link Type}. 
         */
        public static Type getType(String typeName){
            String upperCase = typeName.toUpperCase();
            if("NOCHANGE".equals(upperCase)){
                return NO_CHANGE;
            }
            return valueOf(upperCase);
        }
    }
    
    /**
     * Get the (ungapped) coordinate of this variation.
     * @return the coordinate as a long.
     */
    long getCoordinate();
    /**
     * Get the {@link Type} of this Variation.
     * @return the Type (never null)
     */
    Type getType();
    /**
     * Get the {@link Nucleotide} of the reference at this coordinate.
     * @return the reference {@link Nucleotide} (never null).
     */
    Nucleotide getReferenceBase();
    /**
     * Get the list of {@link Nucleotide}s for the consensus at this coordinate.
     * The returned list may have more than one {@link Nucleotide} for
     * multiple base insertions.
     * @return a list (not null) containing possibly multiple {@link Nucleotide}s.
     */
    List<Nucleotide> getConsensusBase();
    /**
     * Get the histogram counts of variations at this coordinate.  Again, Lists
     * of {@link Nucleotide}s in the case of multibase insertions.
     * @return a Map (not null) of the variation counts.
     */
    Map<List<Nucleotide>, Integer> getHistogram();
}
