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
 * Created on Jun 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class DefaultSliceMap extends AbstractSliceMap{

    private final Map<Long, Slice> sliceMap = new HashMap<Long, Slice>();
    private final long size;
    public DefaultSliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap, 
                        QualityDataStore qualityDataStore,
                        QualityValueStrategy qualityValueStrategy){
        this.size = coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).getEnd()+1;
        for(CoverageRegion<?  extends PlacedRead> region : coverageMap){
            for(long i=region.getStart(); i<=region.getEnd(); i++ ){
                List<SliceElement> sliceElements = createSliceElementsFor(region, i, qualityDataStore, qualityValueStrategy);
                sliceMap.put(Long.valueOf(i),new DefaultSlice.Builder()
                                            .addAll(sliceElements)
                                            .build());
            }
        }
    }
    
    public DefaultSliceMap(List<Slice> slices){
        size = slices.size();
        for(int i=0; i< size; i++){
            sliceMap.put(Long.valueOf(i), slices.get(i));
        }
    }
    @Override
    public Slice getSlice(long offset) {
        return sliceMap.get(Long.valueOf(offset));
    }
    @Override
    public long getSize() {
        return size;
    }
    @Override
    public Iterator<Slice> iterator() {
        return new SliceIterator(sliceMap.keySet().iterator(), this);
    }

    
   
}
