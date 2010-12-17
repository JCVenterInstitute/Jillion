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

import java.util.Iterator;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageMap;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.util.ArrayIterator;

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
    public <PR extends PlacedRead> DefaultQualityCompactedSliceMap(Contig<PR> contig,
                    PhredQuality defaultPhredQuality) {
        this.defaultPhredQuality =defaultPhredQuality;
        CoverageMap<CoverageRegion<PR>> coverageMap = DefaultCoverageMap.buildCoverageMap(contig.getPlacedReads());
        this.slices = createSlices(coverageMap);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    protected CompactedSlice createSlice(
            CoverageRegion<? extends PlacedRead> region, int i) {
        CompactedSlice.Builder builder= new CompactedSlice.Builder();
        for(PlacedRead read : region.getElements()){
            int indexIntoRead = (int) (i - read.getStart());
            builder.addSliceElement(read.getId(),
                    read.getEncodedGlyphs().get(indexIntoRead),
                    defaultPhredQuality,
                    read.getSequenceDirection());
        }
        return builder.build();
    }
    
    /**
     * {@inheritDoc}
     */
     @Override
     public Iterator<Slice> iterator() {
         return new ArrayIterator<Slice>(slices);
     }

     /**
     * {@inheritDoc}
     */
     @Override
     public Slice getSlice(long offset) {
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
