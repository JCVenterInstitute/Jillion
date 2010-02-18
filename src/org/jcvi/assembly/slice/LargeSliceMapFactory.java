/*
 * Created on Jan 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class LargeSliceMapFactory extends AbstractSliceMapFactory{

    private final int cacheSize;
    public LargeSliceMapFactory(QualityValueStrategy qualityValueStrategy,int cacheSize) {
        super(qualityValueStrategy);
        this.cacheSize = cacheSize;
    }
    public LargeSliceMapFactory(QualityValueStrategy qualityValueStrategy){
        this(qualityValueStrategy, LargeSliceMap.DEFAULT_CACHE_SIZE);
    }

    @Override
    protected SliceMap createNewSliceMap(
            CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap,
            DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore,
            QualityValueStrategy qualityValueStrategy) {
        return new LargeSliceMap(coverageMap, qualityDataStore, qualityValueStrategy,cacheSize);
    }

}
