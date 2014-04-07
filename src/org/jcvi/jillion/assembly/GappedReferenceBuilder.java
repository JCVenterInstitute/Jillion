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
package org.jcvi.jillion.assembly;


import java.util.Arrays;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
/**
 * {@code GappedReferenceBuilder} builds 
 * a GAPPED reference {@link NucleotideSequence}
 * from an initial ungapped reference sequence and 
 * lots of insertions to various offsets made by 
 * aligned reads.
 * 
 * If many reads have insertions at the same ungapped offset,
 * the the largest insertion is used. 
 * @author dkatzel
 *
 */
public final class GappedReferenceBuilder {

	private final NucleotideSequence ungappedReference;
	private final Insertion[] insertions;
	
	/**
	 * Create a new instance using the given initial
	 * ungapped reference.
	 * @param ungappedReference the ungapped reference sequence
	 * to use; can not be null or have any gaps or have a length
	 * longer than {@link Integer#MAX_VALUE}
	 * @throws NullPointerException if ungappedReference is null.
	 * @throws IllegalArgumentException if ungappedReference is gapped or if
	 * the length > Integer.MAX_VALUE
	 */
	public GappedReferenceBuilder(NucleotideSequence ungappedReference){
		if(ungappedReference ==null){
			throw new NullPointerException("ungapped reference can not be null");
		}
		if(ungappedReference.getNumberOfGaps() >0){
			throw new IllegalArgumentException("reference can not have gaps");
		}
		long length = ungappedReference.getLength();
		if(length > Integer.MAX_VALUE){
			throw new IllegalArgumentException("reference too big > int MAX");
		}
		this.ungappedReference = ungappedReference;
		insertions = new Insertion[(int)length];
	}
	/**
	 * Add an insertion at the given ungapped offset
	 * of the given insertion size.
	 * @param offset the ungapped offset into the reference.
	 * @param insertionSize the insertion size this read has against the reference.
	 * @return this
	 * @throws IllegalArgumentException if insertionSize is negative.
	 */
	public GappedReferenceBuilder addReadInsertion(int offset, int insertionSize){
		if(insertions[offset] ==null){
        	insertions[offset] = new Insertion(insertionSize);
        }else{
        	insertions[offset].updateSize(insertionSize);
        }
		return this;
	}
	
	public NucleotideSequence build(){
		//compute total number of gaps
		//first so we don't have to keep resizing builder
		int numberOfGaps = computeTotalNumberOfGaps();		
		NucleotideSequenceBuilder gappedSequenceBuilder = new NucleotideSequenceBuilder(numberOfGaps+ insertions.length);
		gappedSequenceBuilder.append(ungappedReference);
		//iterates in reverse to keep offsets in sync
		for(int i= insertions.length-1; i>=0; i--){
    		Insertion insertion = insertions[i];
			if(insertion !=null){
    			gappedSequenceBuilder.insert(i, createGapStringOf(insertion.getSize()));
    		}    		
    	}
		return gappedSequenceBuilder.build();
	}

	private int computeTotalNumberOfGaps() {
		int numberOfGaps = 0;
		for(int i=0; i<insertions.length; i++){
			Insertion insertion = insertions[i];
			if(insertion !=null){
				numberOfGaps += insertion.getSize();
			}
		}
		return numberOfGaps;
	}
	
	
	private char[] createGapStringOf(int maxGapSize) {
		char[] gaps = new char[maxGapSize];
		Arrays.fill(gaps, '-');
		return gaps;
	}


	private static class Insertion{
	        private int size=0;
	        
	        public  Insertion(int initialSize){
	        	if(initialSize<0){
	        		throw new IllegalArgumentException("insertion size can not be negative : " + initialSize);
	        	}
	            this.size = initialSize;
	        }
	        public void updateSize(int newSize){
	            if(newSize > size){
	                this.size = newSize;
	            }
	        }
	        public int getSize(){
	            return size;
	        }
	    }
}
