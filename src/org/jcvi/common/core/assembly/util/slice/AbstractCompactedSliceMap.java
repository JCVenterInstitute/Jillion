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

package org.jcvi.common.core.assembly.util.slice;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.Rangeable;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractCompactedSliceMap implements SliceMap{


    protected CompactedSlice[] createSlices(
            CoverageMap<? extends CoverageRegion<? extends AssembledRead>> coverageMap) {
        int size = (int)coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).asRange().getEnd()+1;
        CompactedSlice[] slices = new CompactedSlice[size];
        for(CoverageRegion<?  extends AssembledRead> region : coverageMap){
        	Range range = region.asRange();
            for(int i=(int)range.getBegin(); i<=range.getEnd(); i++ ){
                slices[i] =createSlice(region, i);                
            }
        }
        return slices;
    }
    /**
     * @param region
     * @param i
     * @return
     */
    protected abstract CompactedSlice createSlice(
            CoverageRegion<? extends AssembledRead> region, int i);
   

}
