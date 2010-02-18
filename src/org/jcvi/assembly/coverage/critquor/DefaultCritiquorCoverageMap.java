/*
 * Created on May 29, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.coverage.critquor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jcvi.Range;
import org.jcvi.assembly.Placed;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageMap;

public class DefaultCritiquorCoverageMap implements CritiquorCovereageMap{

    private final Map<String,CoverageMap<CoverageRegion<Placed>>> coverageMaps;
   
    public  DefaultCritiquorCoverageMap(Map<String,CoverageMap<CoverageRegion<Placed>>> coverageMaps){
        this.coverageMaps = coverageMaps;
    }
    @Override
    public CoverageMap<CoverageRegion<Placed>> getTargetCoverageMap(String key) {
        return coverageMaps.get(key);
    }

    @Override
    public Set<String> getKeySet() {
        return coverageMaps.keySet();
    }

    public static class Builder{
        private final Map<String,List<Placed>> targetRangesByKey = new HashMap<String, List<Placed>>();
        
        public Builder addTargetRange(String key, Range targetRange){
            if(!targetRangesByKey.containsKey(key)){
                targetRangesByKey.put(key, new ArrayList<Placed>());
            }
            targetRangesByKey.get(key).add(targetRange);
            return this;
        }
        
        public DefaultCritiquorCoverageMap build(){
            Map<String,CoverageMap<CoverageRegion<Placed>>> critquorCoverageMaps = new HashMap<String, CoverageMap<CoverageRegion<Placed>>>();
            
            for(Entry<String, List<Placed>> entry : targetRangesByKey.entrySet()){
                CoverageMap<CoverageRegion<Placed>> coverageMap = new DefaultCoverageMap.Builder<Placed>(entry.getValue()).build();
                critquorCoverageMaps.put(entry.getKey(), coverageMap);
            }
            
            return new DefaultCritiquorCoverageMap(critquorCoverageMaps);
        }
    }
}
