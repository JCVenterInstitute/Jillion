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
/**
 * {@code Bin} is an object
 * representation of a single bin
 * in the binning scheme used to index
 * reads in a reference alignment.
 * A distinct bin uniquely corresponds to a distinct internal node in a R-tree
 * 
 * @author dkatzel
 *
 */
public interface Bin {

	/**
	 * Get the number for this Bin.
	 * @return a positive number.
	 */
	int getBinNumber();
	/**
	 * Get the {@link Chunk}s
	 * associated with this bin.
	 * @return the List of {@link Chunk}s
	 * will never be null but may be empty.
	 */
	List<Chunk> getChunks();
	
	@Override
	int hashCode();
	/**
	 * Two bins are equal if they 
	 * have the same bin number 
	 * and the an equal list of Chunks.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	boolean equals(Object o);

}
