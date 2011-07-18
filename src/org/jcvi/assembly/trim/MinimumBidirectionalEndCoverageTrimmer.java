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

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.common.core.seq.read.SequenceDirection;

/**
 * @author dkatzel
 *
 *
 */
public class MinimumBidirectionalEndCoverageTrimmer<P extends PlacedRead, C extends Contig<P>> extends MinimumEndCoverageTrimmer<P, C>{
    private final int maxCoverageToConsider;
    /**
     * @param minimumEndCoverage
     */
    public MinimumBidirectionalEndCoverageTrimmer(int minimumBiDirectionalCoverage, int maxCoverageToConsider) {
        super(minimumBiDirectionalCoverage);
        this.maxCoverageToConsider = maxCoverageToConsider;
    }

    @Override
    protected boolean meetsTrimmingRequirements(CoverageRegion<P> region) {
        if(super.meetsTrimmingRequirements(region)){
            if(region.getCoverage()<=maxCoverageToConsider){
                int forwardCount=0;
                int reverseCount=0;
                for(P read : region){
                    if(read.getSequenceDirection()==SequenceDirection.FORWARD){
                        forwardCount++;
                    }else{
                        reverseCount++;
                    }
                }
                return forwardCount >0 && reverseCount>0;
            }
            return true;
            
        }
        return false;
    }

}
