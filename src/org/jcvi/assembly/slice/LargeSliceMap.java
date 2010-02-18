/*
 * Created on Oct 15, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import java.util.Iterator;

import org.jcvi.Range;
import org.jcvi.RangeIterator;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
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
    private final LRUCache<Long, Slice> cache;
    
    public LargeSliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap, 
            DataStore<? extends EncodedGlyphs<PhredQuality>> qualityDataStore,
            QualityValueStrategy qualityValueStrategy, Range range, int cacheSize){
        this.coverageMap = coverageMap;
        this.qualityDataStore = qualityDataStore;
        this.qualityValueStrategy = qualityValueStrategy;
        this.range = range;
        cache = new LRUCache<Long, Slice>(cacheSize);
    }
    
    public LargeSliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap, 
            DataStore<? extends EncodedGlyphs<PhredQuality>> qualityDataStore,
            QualityValueStrategy qualityValueStrategy, int cacheSize){
        this(coverageMap, qualityDataStore, qualityValueStrategy, 
                Range.buildRange(0,coverageMap.getRegion(coverageMap.getSize()-1).getEnd()),cacheSize);
    }
    public LargeSliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap, 
            DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore,
            QualityValueStrategy qualityValueStrategy, Range range){
        this(coverageMap, qualityDataStore,qualityValueStrategy, range, DEFAULT_CACHE_SIZE);
    }
    public LargeSliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap, 
            DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore,
            QualityValueStrategy qualityValueStrategy){
        this(coverageMap, qualityDataStore, qualityValueStrategy, 
                Range.buildRange(0,coverageMap.getRegion(coverageMap.getSize()-1).getEnd()));
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
            Slice s =new DefaultSlice(createSliceElementsFor(region, i, qualityDataStore, qualityValueStrategy));
            if(i==offset){
                result = s;
            }
            cache.put(i,s);
        }
        return result;
        
    }

    @Override
    public Iterator<Slice> iterator() {
        return new SliceIterator(new RangeIterator(range), this);
    }

}
