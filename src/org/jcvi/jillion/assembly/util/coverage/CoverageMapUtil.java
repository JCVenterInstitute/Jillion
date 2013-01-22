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
package org.jcvi.jillion.assembly.util.coverage;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Rangeable;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;

public final class CoverageMapUtil {

	private CoverageMapUtil(){
		//can not instantiate
		
	}
	public static long getLastCoveredOffsetIn(CoverageMap<?> coverageMap){
	        if(coverageMap.isEmpty()){
	            return -1L;
	        }
	        return coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).asRange().getEnd();
	}
	 
    public static <V extends Rangeable> List<CoverageRegion<V>> getRegionsWhichIntersect(CoverageMap<V> coverageMap, Range range) {
        List<CoverageRegion<V>> selectedRegions = new ArrayList<CoverageRegion<V>>();
        StreamingIterator<CoverageRegion<V>> iter =null;
        try{
        	iter = coverageMap.getRegionIterator();
        	boolean done=false;
        	while(!done && iter.hasNext()){
        		CoverageRegion<V> region = iter.next();
        		Range regionRange = region.asRange();
        		if(range.endsBefore(regionRange)){
                    done=true;
                }else if(regionRange.intersects(range)){
                    selectedRegions.add(region);
                }
        	}
        	return selectedRegions;
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
       
        
    }
    

    public static <V extends Rangeable> CoverageRegion<V> getRegionWhichCovers(CoverageMap<V> coverageMap, long consensusIndex) {
        Range range = Range.of(consensusIndex, consensusIndex);
        final List<CoverageRegion<V>> intersectedRegion = getRegionsWhichIntersect(coverageMap, range);
        if(intersectedRegion.isEmpty()){
            return null;
        }
        return intersectedRegion.get(0);
    }
}
