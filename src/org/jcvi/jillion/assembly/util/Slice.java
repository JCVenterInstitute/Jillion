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
/*
 * Created on Jun 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util;

import java.util.Map;

import org.jcvi.jillion.core.residue.nt.Nucleotide;

/**
 * A {@code Slice} is a one base wide vertical cut of an assembly containing zero 
 * or more {@link SliceElement}s.
 * @author dkatzel
 *
 *
 */
public interface Slice extends Iterable<SliceElement>{
    /**
     * Get the coverage depth of this Slice.
     * @return the coverage depth of this slice, will
     * always be {@code >= 0}.
     */
    int getCoverageDepth();
    
	/**
     * Does this {@link Slice} contain a 
     * {@link SliceElement} with the given id.
     * @param elementId the id of the {@link SliceElement} being queried.
     * @return {@code true} if this Slice does contain
     * a {@link SliceElement} with the given id; {@code false} otherwise.
     */
    boolean containsElement(String elementId);
    /**
     * Get the SliceElement by id.
     * @param elementId the id of the SliceElement to get.
     * @return the {@link SliceElement} if exists; or {@code null}
     * if there is no {@link SliceElement} for this Slice with that id.
     */
    SliceElement getSliceElement(String elementId);
    /**
     * Get a Mapping of how many of each {@link Nucleotide}
     * is present in this slice.  
     * @return a new {@link Map} containing just
     * the the {@link Nucleotide}s in this slice
     * (there will be no keys with value of 0 );
     * will never return null, if {@link #getCoverageDepth()} ==0
     * then this method will return an empty map.
     */
    Map<Nucleotide, Integer> getNucleotideCounts();
   
}
