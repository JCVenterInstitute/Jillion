/*
 * Created on Feb 23, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.coverage.CoverageMap;
import org.jcvi.common.core.assembly.coverage.CoverageRegion;
import org.jcvi.common.core.seq.qual.PhredQuality;
import org.jcvi.common.core.seq.qual.QualityDataStore;

public class NoQualitySliceMapFactory<P extends PlacedRead, R extends CoverageRegion<P>, M extends CoverageMap<R>> implements SliceMapFactory<P,R,M>{
    private final PhredQuality phredQuality;
    public NoQualitySliceMapFactory(){
        this(NoQualitySliceMap.DEFAULT_PHRED_QUALITY);
    }
    /**
     * @param phredQuality
     */
    public NoQualitySliceMapFactory(PhredQuality phredQuality) {
        this.phredQuality = phredQuality;
    }

    @Override
    public SliceMap createNewSliceMap(
            M coverageMap,
                    QualityDataStore qualityDataStore) {
        return new NoQualitySliceMap(coverageMap,phredQuality);
    }

}
