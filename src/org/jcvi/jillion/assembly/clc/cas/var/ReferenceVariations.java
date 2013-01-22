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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.clc.cas.var;

import org.jcvi.jillion.core.util.iter.StreamingIterator;
/**
 * {@code ReferenceVariations} is an object representation
 * of all the {@link Variation}s found by the {@code find_variations}
 * program for a single reference assembly.
 * @author dkatzel
 *
 */
public interface ReferenceVariations {
	/**
	 * Get the id of the reference used by the assembly.
	 * @return a String; never null.
	 */
	String getReferenceId();
	/**
	 * Get a {@link StreamingIterator}
	 * of all the {@link Variation}s found for this
	 * assembly compared to the reference.
	 * @return a new {@link StreamingIterator}; never
	 * null but may contain no elements. if no variations 
	 * were found.
	 */
	StreamingIterator<Variation> getVariationIterator();
}
