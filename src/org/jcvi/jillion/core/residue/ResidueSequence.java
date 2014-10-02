/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.core.residue;

import java.util.Iterator;
import java.util.List;

import org.jcvi.jillion.core.Sequence;
/**
 * {@code ResidueSequence} is a {@link Sequence}
 * of {@link Residue} that may contain gaps.  There are extra
 * methods to get the gap locations and convert from gap offsets to 
 * ungapped offsets and vice versa.
 * @author dkatzel
 *
 * @param <R> the Type of {@link Residue} in this {@link Sequence}.
 */
public interface ResidueSequence<R extends Residue> extends Sequence<R> {

	 /**
     * Get a List of all the offsets into this
     * sequence which are gaps.  This list SHOULD be
     * sorted by offset in ascending order.  The size of the returned list should be
     * the same as the value returned by {@link #getNumberOfGaps()}.
     * @return a List of gap offsets as Integers.
     */
    List<Integer> getGapOffsets();    
    /**
     * Get the number of gaps in this sequence.
     * @return the number of gaps; will always be {@code >=0}.
     */
    int getNumberOfGaps();
   
    /**
     * Is the {@link Nucleotide} at the given gapped index a gap?
     * @param gappedOffset the gappedOffset to check.
     * @return {@code true} is it is a gap; {@code false} otherwise.
     */
    boolean isGap(int gappedOffset);
    /**
     * Get the number of {@link Nucleotide}s in this {@link NucleotideSequence} 
     * that are not gaps.
     * @return the number of non gaps as a long.
     */
    long getUngappedLength();
    /**
     * Get the number of gaps in the valid range until AND INCLUDING the given
     * gapped index.
     * @param gappedOffset the index to count the number of gaps until.
     * @return the number of gaps in the valid range until AND INCLUDING the given
     * gapped index.
     */
    int getNumberOfGapsUntil(int gappedOffset);
    /**
     * Get the corresponding ungapped offset into
     * this sequence for the given
     * gapped offset.
     * @param gappedOffset the offset into the gapped coordinate
     * system of the desired nucleotide.  This value must be
     * a non-negative value that is less than the sequence length.
     * @return the corresponding offset for the equivalent
     * location in the ungapped sequence.
     * @throws IndexOutOfBoundsException if the gappedOffset
     * is negative or beyond the sequence length.
     */
    int getUngappedOffsetFor(int gappedOffset);
    /**
     * Get the corresponding gapped offset into
     * this sequence for the given
     * ungapped offset. For example
     * calling this method passing in a value of {@code 0}
     * will return the number of leading gaps in this sequence.
     * @param ungappedOffset the offset into the ungapped coordinate
     * system of the desired nucleotide.  This value must be
     * a non-negative value that is less than the sequence ungapped length.
     * @return the corresponding offset for the equivalent
     * location in the gapped sequence.
     * @throws IndexOutOfBoundsException if the ungappedOffset
     * is negative or if the computed
     * gapped offset would extend beyond the sequence length.
     */
    int getGappedOffsetFor(int ungappedOffset);
    /**
     * Get this sequence as a single long string
     * of characters with no whitespace.
     * @return the full sequence as a long string.
     */
    @Override
    String toString();
    /**
     * Two {@link ResidueSequence}s are equal
     * if they contain the same residues 
     * in the same order. 
     * {@inheritDoc}.
     * 
     * @see #isEqualIgnoringGaps(ResidueSequence)
     */
    @Override
    boolean equals(Object o);
    
    
    default boolean isEqualToIgnoringGaps(ResidueSequence<? extends R> other){
    	if(other ==null){
    		return false;
    	}
    	if(getUngappedLength() != other.getUngappedLength()){
    		return false;
    	}
    	Iterator<R> iter = iterator();
    	Iterator<? extends R> otherIter = other.iterator(); 
    	while(iter.hasNext()){
    		//have to duplicate get non-gap
    		//code because can't use private helper method
    		//inside a default method.
    		R nextNonGap;
    		do{
    			nextNonGap =iter.next();
    		}while(nextNonGap.isGap() && iter.hasNext());
    		
    		R nextOtherNonGap=null;
    		
    		if(!nextNonGap.isGap()){    			
    			//haven't reached the end of our sequence
    			//yet so check the other sequence for equality
	    		do{
	    			nextOtherNonGap =otherIter.next();
	    		}while(nextOtherNonGap.isGap() && otherIter.hasNext());
	    		
	    		//if we get this far,
	    		//then the our next base is NOT a gap
	    		//so the other seq better equal
	    		if(!nextNonGap.equals(nextOtherNonGap)){
	    			return false;
	    		}
    		}
    		
    	}
    	//if we get this far then our entire sequences
    	//matched. because we previously
    	//checked that the ungapped lengths matched
    	//so if either iterator still has elements
    	//they must all be gaps.
    	return true;
    }
    /**
     * The HashCode of a {@link ResidueSequence}
     * is computed by summing the hashcodes
     * of the residues of this sequence
     * in sequential order. 
     */
    @Override
    int hashCode();
    
    /**
     * Create a new Builder object that is initialized
     * to the current sequence.  Any changes made to the returned Builder
     * will <strong>NOT</strong> affect this immutable Sequence.
     * @return a new Builder instance, will never be null.
     * @see 5.0
     */
    ResidueSequenceBuilder<R, ? extends ResidueSequence<R>> toBuilder();

}
