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
package org.jcvi.jillion.assembly.util;

import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;

/**
 * A {@code CoverageMap} is an Object that contains coverage information
 * for a contiguous range of offset values. Coverage is defined
 * as the number of elements that are span a given offset.
 * The coverage at each offset
 * may be different since a different number of objects may 
 * span different locations.  Since adjacent offsets often 
 * have the same coverage information, contiguous regions that contain the same exact
 * elements (and therefore the same exact coverage) are combined into 
 * {@link CoverageRegion} objects. Adjacent {@link CoverageRegion}s may
 * have the same depth of coverage but will not contain the same exact
 * elements.
 * <p/>
 * For example, a CoverageMap of a  {@link org.jcvi.jillion.assembly.Contig} 
 * will show where each of its {@link org.jcvi.jillion.assembly.AssembledRead}s align
 * to the contig consensus. Each consensus offset will have a different level of coverage
 * because each consensus offset will have a different number of reads aligned to it.
 * @author dkatzel
 *
 * @param <T> The Type of element in the coverage map.
 */
public interface CoverageMap <T extends Rangeable> extends Iterable<CoverageRegion<T>>{
	/**
	 * Get the number of {@link CoverageRegion}s.
	 * @return the number of regions will always be 
	 * >=0.
	 */
    int getNumberOfRegions();
    /**
     * Get the ith {@link CoverageRegion}.
     * @param i the index into this coverage map;
     * where 0 <= i <= {@link #getNumberOfRegions()} -1
     * @return the ith {@link CoverageRegion} will never be null
     * but may have 0 depth of coverage. 
     * @throws IndexOutOfBoundsException if i <0 or i > {@link #getNumberOfRegions()} -1
     */
    CoverageRegion<T> getRegion(int i);
    /**
     * Does this CoverageMap have any CoverageRegions.
     * @return {@code true} if {@link #getNumberOfRegions()}>0;
     * {@code false} otherwise.
     */
    boolean isEmpty();

    /**
     * Get the average coverage depth at each offset
     * in the coverage map. This is the same as (but may be more
     * efficient than):
     * <pre>
     	long totalLength = 0L;
     	long totalCoverage =0L;
     	for(CoverageRegion<?> region : this){
        	long rangeLength =region.asRange().getLength();
        	totalLength += rangeLength;
        	totalCoverage += region.getCoverageDepth() * rangeLength;
        }
        avgCoverage = totalLength==0? 0D : totalCoverage/(double)totalLength;
        </pre>
     * @return the average coverage depth will always be 
     * >=0.
     */
    double getAverageCoverage();
    /**
     * Get the lowest coverage depth of 
     * any {@link CoverageRegion}
     * in the coverage map.
     */
    int getMinCoverage();
    /**
     * Get the highest coverage depth of 
     * any {@link CoverageRegion}
     * in the coverage map.
     */
    int getMaxCoverage();
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
