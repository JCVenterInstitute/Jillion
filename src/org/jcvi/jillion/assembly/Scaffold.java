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
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import java.util.Iterator;
import java.util.Set;

import org.jcvi.jillion.assembly.util.CoverageMap;
import org.jcvi.jillion.core.Range;
/**
 * A scaffold (also known as a super-contig) is a layout
 * of {@link Contig}s placed and oriented together such that 
 * it can be determined the distance and orientation between contigs.
 * Scaffold usually do not contain overlapping contigs so therefore 
 * often have gaps where there is no genomic data.
 * @author dkatzel
 *
 *
 */
public interface Scaffold {
    /**
     * Get the id of this scaffold.
     * @return the id of this scaffold; never null.
     */
    String getId();
    /**
     * Get the {@link PlacedContig}
     * representation for the given contig id.
     * @param contigId the id of the contig to get.
     * @return a PlacedContig object if it exsits;
     * null if it does not exist in the scaffold.
     */
    PlacedContig getPlacedContig(String contigId);
    /**
     * Does this scaffold contain a {@link PlacedContig}
     * object with the given contig id?
     * @param contigId the contig id to check.
     * @return {@code true} if the contig is in the scaffold;
     * {@code false} otherwise.
     */
    boolean hasContig(String contigId);
    /**
     * Get a set of all the {@link PlacedContig}s
     * in this scaffold.
     * @return a Set of {@link PlacedContig}s; will never be null and will only 
     * be empty if there are no contigs in this scaffold.
     */
    Set<PlacedContig> getPlacedContigs();
    /**
     * Get the {@link CoverageMap} of the {@link PlacedContig}s
     * in this scaffold.
     * @return a {@link CoverageMap}; never null.
     */
    CoverageMap<PlacedContig> getContigCoverageMap();
    /**
     * Get the number of contigs in this scaffold.
     * @return a positive number.
     */
    int getNumberOfContigs();
    /**
     * Get the length of this scaffold in ungapped bases.
     * @return
     */
    long getLength();

    /**
     *  converts contig based coordinates into scaffold coordinates.
     */
    Range convertContigRangeToScaffoldRange(String placedContigId, Range placedContigRange);
    /**
     * Get an iterator of all the contig ids in this scaffold.
     * @return an iterator of the contig ids; never null.
     */
    Iterator<String> getContigIds();
}
