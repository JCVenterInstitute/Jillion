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
package org.jcvi.jillion.sam.index;

import java.util.List;

import org.jcvi.jillion.sam.VirtualFileOffset;

/**
 * {@code ReferenceIndex} is an object
 * representations of a BAM index file's
 * list of bins and linear intervals
 * used for indexing.
 * 
 * @author dkatzel
 *
 */
public interface ReferenceIndex {
	/**
	 * Get an unmodifiable List of {@link Bin}s
	 * in this reference.
	 * @return a List of Bins, will never
	 * be null but may be empty.
	 */
	List<Bin> getBins();
	/**
	 * Get (defensive copy of) the array of intervals for this
	 * index.  
	 * @return an array of {@link VirtualFileOffset}s,
	 * which will never be empty, although
	 * there might be null cells in the array.
	 */
	VirtualFileOffset[] getIntervals();
	
	boolean hasMetaData();
	
	Long getNumberOfUnAlignedReads();

	Long getNumberOfAlignedReads();
	
	VirtualFileOffset getLowestStartOffset();



	VirtualFileOffset getHighestEndOffset();
	int getNumberOfBins();

}
