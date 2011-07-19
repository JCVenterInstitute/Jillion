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

package org.jcvi.assembly.trim;

import java.util.List;

import org.jcvi.Range;
import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.coverage.CoverageMap;
import org.jcvi.common.core.assembly.coverage.CoverageRegion;

/**
 * @author dkatzel
 *
 *
 */
public class MinimumEndCoverageTrimmer<P extends PlacedRead, C extends Contig<P>> implements PlacedReadTrimmer<P, C>{

    private final int minimumEndCoverage;    
    private Range trimmedContigRange;
    
    /**
     * @param minimumEndCoverage
     */
    public MinimumEndCoverageTrimmer(int minimumEndCoverage) {
        this.minimumEndCoverage = minimumEndCoverage;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public Range trimRead(P placedRead,
            Range currentRangeOfBases) {
        
        if(currentRangeOfBases.isEmpty()){
            return currentRangeOfBases;
        }
        Range currentValidRangeOnReference =Range.buildRange(
                placedRead.convertValidRangeIndexToReferenceIndex(currentRangeOfBases.getStart()),
                placedRead.convertValidRangeIndexToReferenceIndex(currentRangeOfBases.getEnd()));
        
        Range placedReadTrimRange =trimmedContigRange.intersection(currentValidRangeOnReference);
        //short circut if returns empty range
        //no need to do anymore checking if we will just throw it out.
        if(placedReadTrimRange.isEmpty()){
            return placedReadTrimRange;
        }
        int gappedLeftIndex = (int)placedRead.convertReferenceIndexToValidRangeIndex(placedReadTrimRange.getStart());
        int gappedRightIndex =(int)placedRead.convertReferenceIndexToValidRangeIndex(placedReadTrimRange.getEnd());
        int newLeftStart =AssemblyUtil.getRightFlankingNonGapIndex(placedRead.getEncodedGlyphs(), gappedLeftIndex);
        int newRightStart =AssemblyUtil.getLeftFlankingNonGapIndex(placedRead.getEncodedGlyphs(), gappedRightIndex);
        
        return Range.buildRange(newLeftStart,newRightStart);
    }

    @Override
    public void initializeContig(C contig,
            CoverageMap<CoverageRegion<P>> coverageMap) {
        List<CoverageRegion<P>> regions = coverageMap.getRegions();
        long trimLeftIndex=Long.MAX_VALUE;
        long trimRightIndex=Long.MIN_VALUE;
        for(int i=0; i<regions.size(); i++){
            CoverageRegion<P> region = regions.get(i);
            if(meetsTrimmingRequirements(region)){
                trimLeftIndex=region.getStart();
                break;
            }
        }
        for(int i=regions.size()-1; i>=0; i--){
            CoverageRegion<P> region = regions.get(i);
            if(meetsTrimmingRequirements(region)){
                trimRightIndex=region.getEnd();
                break;
            }
        }
        if(trimRightIndex < trimLeftIndex -1){
            trimmedContigRange = Range.buildEmptyRange();
        }else{
            trimmedContigRange = Range.buildRange(trimLeftIndex, trimRightIndex);
        }
    }

    /**
     * @param region
     * @return
     */
    protected boolean meetsTrimmingRequirements(CoverageRegion<P> region) {
        return region.getCoverage()>=minimumEndCoverage;
    }

    @Override
    public void clear() {
        
        
    }

}
