package org.jcvi.common.core.assembly.util.coverage;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Rangeable;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.util.iter.CloseableIterator;

public final class CoverageMapUtil {

	public static long getLastCoveredOffsetIn(CoverageMap<?> coverageMap){
	        if(coverageMap.isEmpty()){
	            return -1L;
	        }
	        return coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).asRange().getEnd();
	}
	 
    public static <V extends Rangeable> List<CoverageRegion<V>> getRegionsWhichIntersect(CoverageMap<V> coverageMap, Range range) {
        List<CoverageRegion<V>> selectedRegions = new ArrayList<CoverageRegion<V>>();
        CloseableIterator<CoverageRegion<V>> iter =null;
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
        Range range = Range.create(consensusIndex, consensusIndex);
        final List<CoverageRegion<V>> intersectedRegion = getRegionsWhichIntersect(coverageMap, range);
        if(intersectedRegion.isEmpty()){
            return null;
        }
        return intersectedRegion.get(0);
    }
}
