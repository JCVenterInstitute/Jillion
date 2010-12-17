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

package org.jcvi.assembly.slice;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractCompactedSliceMap implements SliceMap{


    protected CompactedSlice[] createSlices(
            CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap) {
        int size = (int)coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).getEnd()+1;
        CompactedSlice[] slices = new CompactedSlice[size];
        for(CoverageRegion<?  extends PlacedRead> region : coverageMap){
            for(int i=(int)region.getStart(); i<=region.getEnd(); i++ ){
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
            CoverageRegion<? extends PlacedRead> region, int i);
   

}
