/*
 * Created on Feb 22, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.slice;

import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.coverage.CoverageMap;
import org.jcvi.common.core.assembly.coverage.CoverageRegion;
import org.jcvi.common.core.seq.qual.PhredQuality;
import org.jcvi.common.core.seq.qual.QualityDataStore;

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
