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
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.coverage;

import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

/**
 * A {@code CoverageMap}
 * @author dkatzel
 *
 * @param <T>
 */
public interface CoverageMap <T extends Rangeable> extends Iterable<CoverageRegion<T>>{

    int getNumberOfRegions();

    CoverageRegion<T> getRegion(int i);
    
    boolean isEmpty();
    
    StreamingIterator<CoverageRegion<T>> getRegionIterator();
   
    double getAverageCoverage();
    /**
     * Get a List of all the {@link CoverageRegion}s in this CoverageMap
     * that intersect the given {@link Range}.
     * @param range the Range to get all the coverageRegions for.
     * @return A List of CoverageRegions; if the coverage map does not contain
     * any CoverageRegions that intersect the given range, then the returned
     * List will be empty.  Will never return null.  It is possible that
     * CoverageRegions returned may have 0 depth of coverage.
     * @throws NullPointerException if range is null.
     */
    List<CoverageRegion<T>> getRegionsWhichIntersect(Range range);
    /**
     * Get the {@link CoverageRegion} that provides coverage
     * for the given offset (in 0-based).  If this coverage map
     * includes the given offset, then the returned {@link CoverageRegion}'s
     * {@link Range} will intersect the given offset.
     * @param offset the offset to 
     * @return a CoverageRegion if this CoverageMap contains this offset;
     * or {@code null} if this coverage map does not contain this offset. 
     * It is possible for the returned CoverageRegion to have 0 depth of coverage.
     */
    CoverageRegion<T> getRegionWhichCovers(long offset);
}
