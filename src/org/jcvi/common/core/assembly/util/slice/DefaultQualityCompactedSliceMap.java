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

import java.util.Iterator;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.util.iter.ArrayIterator;

/**
 * @author dkatzel
 *
 *
 */
public class DefaultQualityCompactedSliceMap extends AbstractCompactedSliceMap{

    private final CompactedSlice[] slices;
    private final PhredQuality defaultPhredQuality;
    /**
     * @param coverageMap
     */
    public <PR extends AssembledRead> DefaultQualityCompactedSliceMap(Contig<PR> contig,
                    PhredQuality defaultPhredQuality) {
        this.defaultPhredQuality =defaultPhredQuality;
        CoverageMap<CoverageRegion<PR>> coverageMap = DefaultCoverageMap.buildCoverageMap(contig);
        this.slices = createSlices(coverageMap);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected CompactedSlice createSlice(
            CoverageRegion<? extends AssembledRead> region, int i) {
        CompactedSlice.Builder builder= new CompactedSlice.Builder();
        for(AssembledRead read : region){
            int indexIntoRead = (int) (i - read.getGappedStartOffset());
            builder.addSliceElement(read.getId(),
                    read.getNucleotideSequence().get(indexIntoRead),
                    defaultPhredQuality,
                    read.getDirection());
        }
        return builder.build();
    }
    
    /**
     * {@inheritDoc}
     */
     @Override
     public Iterator<IdedSlice> iterator() {
         return new ArrayIterator<IdedSlice>(slices);
     }

     /**
     * {@inheritDoc}
     */
     @Override
     public IdedSlice getSlice(long offset) {
         return slices[(int)offset];
     }

     /**
     * {@inheritDoc}
     */
     @Override
     public long getSize() {
         return slices.length;
     }

}
