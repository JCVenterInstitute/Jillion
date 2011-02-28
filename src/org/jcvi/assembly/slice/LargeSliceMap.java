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
 * Created on Oct 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import java.util.Iterator;
import java.util.Map;

import org.jcvi.Range;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;
import org.jcvi.util.LRUCache;

public class LargeSliceMap extends AbstractSliceMap{
    /**
     * Default size of LRU Cache of previously fetched slices.
     */
    public static final int DEFAULT_CACHE_SIZE = 1000;
    
    private final CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap;
    private final        DataStore<? extends EncodedGlyphs<PhredQuality>> qualityDataStore;
    private final        QualityValueStrategy qualityValueStrategy;
    private final Range range;
    private final Map<Long, Slice> cache;
    
    public LargeSliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap, 
            QualityDataStore qualityDataStore,
            QualityValueStrategy qualityValueStrategy, Range range, int cacheSize){
        this.coverageMap = coverageMap;
        this.qualityDataStore = qualityDataStore;
        this.qualityValueStrategy = qualityValueStrategy;
        this.range = range;
        cache = LRUCache.createLRUCache(cacheSize);
    }
    
    public LargeSliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap, 
            QualityDataStore qualityDataStore,
            QualityValueStrategy qualityValueStrategy, int cacheSize){
        this(coverageMap, qualityDataStore, qualityValueStrategy, 
                Range.buildRange(0,coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).getEnd()),cacheSize);
    }
    public LargeSliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap, 
            QualityDataStore qualityDataStore,
            QualityValueStrategy qualityValueStrategy, Range range){
        this(coverageMap, qualityDataStore,qualityValueStrategy, range, DEFAULT_CACHE_SIZE);
    }
    public LargeSliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap, 
            QualityDataStore qualityDataStore,
            QualityValueStrategy qualityValueStrategy){
        this(coverageMap, qualityDataStore, qualityValueStrategy, 
                Range.buildRange(0,coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).getEnd()));
    }
    @Override
    public long getSize() {
        return range.size();
    }

    @Override
    public synchronized Slice getSlice(long offset) {
        if(cache.containsKey(offset)){
            return cache.get(offset);
        }
        CoverageRegion<? extends PlacedRead> region =coverageMap.getRegionWhichCovers(offset);
        if(region ==null){
            return null;
        }
        Slice result=null;
        for(long i= region.getStart(); i<=region.getEnd(); i++){
            Slice s =new DefaultSlice.Builder()
                    .addAll(createSliceElementsFor(region, i, qualityDataStore, qualityValueStrategy))
                    .build();
            if(i==offset){
                result = s;
            }
            cache.put(i,s);
        }
        return result;
        
    }

    @Override
    public Iterator<Slice> iterator() {
        return new SliceIterator(range.iterator(), this);
    }

}
