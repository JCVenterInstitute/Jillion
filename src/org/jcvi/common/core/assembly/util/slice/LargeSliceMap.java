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
package org.jcvi.common.core.assembly.util.slice;

import java.util.Iterator;
import java.util.Map;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualityDataStore;
import org.jcvi.common.core.util.Caches;

public class LargeSliceMap extends AbstractSliceMap{
    /**
     * Default size of LRU Cache of previously fetched slices.
     */
    public static final int DEFAULT_CACHE_SIZE = 1000;
    
    private final CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap;
    private final        DataStore<? extends Sequence<PhredQuality>> qualityDataStore;
    private final        QualityValueStrategy qualityValueStrategy;
    private final Range range;
    private final Map<Long, IdedSlice> cache;

    public static <PR extends PlacedRead,C extends Contig<PR>> LargeSliceMap  createFromContig(C contig,  QualityDataStore qualityDataStore,
            QualityValueStrategy qualityValueStrategy, int cacheSize){
        CoverageMap<? extends CoverageRegion<PR>> coverageMap = DefaultCoverageMap.buildCoverageMap(contig);
        return new LargeSliceMap(coverageMap,qualityDataStore,qualityValueStrategy,cacheSize);
    }
    public static <PR extends PlacedRead,C extends Contig<PR>> LargeSliceMap  createFromContig(C contig,  QualityDataStore qualityDataStore,
            QualityValueStrategy qualityValueStrategy){
        return createFromContig(contig,qualityDataStore,qualityValueStrategy,DEFAULT_CACHE_SIZE);
    }
    
    public static <PR extends PlacedRead, R extends CoverageRegion<PR>, M extends CoverageMap<R>> LargeSliceMap create(M coverageMap,QualityDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy){
        //compiler complained about types when I tried to delegate to fuller factory method
        //so I need to explicitly create instance here
        return new LargeSliceMap(coverageMap, qualityDataStore, qualityValueStrategy,DEFAULT_CACHE_SIZE);
        
    }
    public static <PR extends PlacedRead, R extends CoverageRegion<PR>, M extends CoverageMap<R>> LargeSliceMap create(
            M coverageMap,QualityDataStore qualityDataStore,QualityValueStrategy qualityValueStrategy, int cacheSize){
        return new LargeSliceMap(coverageMap, qualityDataStore, qualityValueStrategy,cacheSize);
    }
    public LargeSliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap, 
            QualityDataStore qualityDataStore,
            QualityValueStrategy qualityValueStrategy, Range range, int cacheSize){
        this.coverageMap = coverageMap;
        this.qualityDataStore = qualityDataStore;
        this.qualityValueStrategy = qualityValueStrategy;
        this.range = range;
        cache = Caches.createLRUCache(cacheSize);
    }
    
    public LargeSliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap, 
            QualityDataStore qualityDataStore,
            QualityValueStrategy qualityValueStrategy, int cacheSize){
        this(coverageMap, qualityDataStore, qualityValueStrategy, 
                Range.create(0,coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).getEnd()),cacheSize);
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
                Range.create(0,coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).getEnd()));
    }
    @Override
    public long getSize() {
        return range.getLength();
    }

    @Override
    public synchronized IdedSlice getSlice(long offset) {
        if(cache.containsKey(offset)){
            return cache.get(offset);
        }
        CoverageRegion<? extends PlacedRead> region =coverageMap.getRegionWhichCovers(offset);
        if(region ==null){
            return null;
        }
        IdedSlice result=null;
        for(long i= region.getBegin(); i<=region.getEnd(); i++){
        	IdedSlice s =new DefaultSlice.Builder()
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
    public Iterator<IdedSlice> iterator() {
        return new SliceIterator(range.iterator(), this);
    }

}
