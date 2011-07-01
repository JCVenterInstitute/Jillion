/*
 * Created on Feb 22, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class LargeNoQualitySliceMapFactory<P extends PlacedRead, R extends CoverageRegion<P>, M extends CoverageMap<R>> implements SliceMapFactory<P,R,M>{

    private final int cacheSize;
    private final PhredQuality phredQuality;
    public LargeNoQualitySliceMapFactory(PhredQuality phredQuality,int cacheSize) {
        this.phredQuality = phredQuality;
        this.cacheSize = cacheSize;
    }
    public LargeNoQualitySliceMapFactory(){
        this(LargeNoQualitySliceMap.DEFAULT_PHRED_QUALITY, LargeNoQualitySliceMap.DEFAULT_CACHE_SIZE);
    }

    @Override
    public SliceMap createNewSliceMap(
            M coverageMap,
                    QualityDataStore qualityDataStore) {
        return new LargeNoQualitySliceMap(coverageMap, cacheSize,this.phredQuality);
    }
    

}
