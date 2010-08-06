/*
 * Created on Feb 23, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public class NoQualitySliceMapFactory implements SliceMapFactory{
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
            CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap,
                    QualityDataStore qualityDataStore) {
        return new NoQualitySliceMap(coverageMap,phredQuality);
    }

}
