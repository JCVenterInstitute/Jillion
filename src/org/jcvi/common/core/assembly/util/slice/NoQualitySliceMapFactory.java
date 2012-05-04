/*
 * Created on Feb 23, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.util.slice;

import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.qual.QualityDataStore;

public class NoQualitySliceMapFactory<P extends AssembledRead> implements SliceMapFactory<P>{
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
            CoverageMap<P> coverageMap,
                    QualityDataStore qualityDataStore) {
        return new NoQualitySliceMap(coverageMap,phredQuality);
    }

}
