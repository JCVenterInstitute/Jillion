/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue.nt;

import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.internal.core.GlyphCodec;

/**
 * @author dkatzel
 *
 *
 */
interface NucleotideCodec extends GlyphCodec<Nucleotide>{

    byte[] encode(int numberOfNucleotides,Iterator<Nucleotide> nucleotides);
    /**
     * Get a List of all the offsets into this
     * sequence which are gaps.  This list SHOULD be
     * sorted by offset in ascending order.  The size of the returned list should be
     * the same as the value returned by {@link #getNumberOfGaps(byte[])}.
     * @return a List of gap offsets as Integers.
     */
    List<Integer> getGapOffsets(byte[] encodedData);    
    /**
     * Get the number of gaps in this sequence.
     * @return the number of gaps; will always be {@code >=0}.
     */
    int getNumberOfGaps(byte[] encodedData);
   
    /**
     * Is the {@link Nucleotide} at the given gapped index a gap?
     * @param gappedOffset the gappedOffset to check.
     * @return {@code true} is it is a gap; {@code false} otherwise.
     */
    boolean isGap(byte[] encodedData,int gappedOffset);
    /**
     * Get the number of {@link Nucleotide}s in this {@link NucleotideSequence} 
     * that are not gaps.
     * @return the number of non gaps as a long.
     */
    long getUngappedLength(byte[] encodedData);
    /**
     * Compute the number of gaps in the valid range until AND INCLUDING the given
     * gapped index.
     * @param gappedOffset the index to count the number of gaps until.
     * @return the number of gaps in the valid range until AND INCLUDING the given
     * gapped index.
     */
    int getNumberOfGapsUntil(byte[] encodedData,int gappedOffset);
    /**
     * Get the ungapped offset equivalent of the 
     * given gapped offset.
     * @param encodedData the encoded bytes which contain
     * all the nucleotides.
     * @param gappedOffset the gapped offset to use 
     * to compute the ungapped offset.
     * @return an int representing the ungapped
     * offset; will always be >=0.
     */
    int getUngappedOffsetFor(byte[] encodedData,int gappedOffset);
    /**
     * Get the gapped offset equivalent of the 
     * given ungapped offset.
     * @param encodedData the encoded bytes which contain
     * all the nucleotides.
     * @param ungappedOffset the ungapped offset to use 
     * to compute the gapped offset.
     * @return an int representing the gapped
     * offset; will always be >=0.
     */
    int getGappedOffsetFor(byte[] encodedData,int ungappedOffset);
    
    /**
     * Convenience method to encode a single nucleotide.
     * @param nucleotide
     * @return the byte array which encodes the single given nucleotide.
     */
    byte[] encode(Nucleotide nucleotide);
    /**
     * Creates a new {@link Iterator}
     * in an efficent manner.
     * @return
     */
    Iterator<Nucleotide> iterator(byte[] encodedData);
    
    /**
     * Create a new {@link Iterator}
     * which only iterates over the specified
     * Range of elements in this sequence.
     * @param range the range to iterate over.
     * @return a new {@link Iterator}; will never
     * be null.
     * @throws NullPointerException if range is null.
     * @throws IndexOutOfBoundsException if Range contains
     * values outside of the possible sequence offsets.
     */
    Iterator<Nucleotide> iterator(byte[] encodedData, Range range);
    /**
     * Convert the encoded bytes into a String
     * of Nucleotides.
     * @param encodedData the byte array of encoded
     * nucleotide sequence data.
     * @return a new String that represents the 
     * same Nucleotide sequence; will never be null.
     */
    String toString(byte[] encodedData);
}
