/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Jan 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.util.coverage;

import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Rangeable;


public interface CoverageMap <T extends Rangeable> extends Iterable<CoverageRegion<T>>{

    int getNumberOfRegions();

    CoverageRegion<T> getRegion(int i);
    List<CoverageRegion<T>> getRegions();
    boolean isEmpty();
    List<CoverageRegion<T>> getRegionsWithin(Range range);
    List<CoverageRegion<T>> getRegionsWhichIntersect(Range range);
    List<CoverageRegion<T>> getRegionsWithCoverage(int coverageDepth);
    
    CoverageRegion<T> getRegionWhichCovers(long consensusIndex);
    
    int getRegionIndexWhichCovers(long consensusIndex);
    
    double getAverageCoverage();
    int getMaxCoverage();
    int getMinCoverage();
    long getLength();
    
    int getNumberOfRegionsWithCoverage(int coverageDepth);
    
    int getNumberOfRegionsWithAtLeastCoverage(int coverageDepth);
    
    long getLengthOfRegionsWithCoverage(int coverageDepth);
    
    long getLengthOfRegionsWithAtLeastCoverage(int coverageDepth);

}
