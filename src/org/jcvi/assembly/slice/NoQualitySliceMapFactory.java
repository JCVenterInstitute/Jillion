/*
 * Created on Feb 23, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;

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
            DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore) {
        return new NoQualitySliceMap(coverageMap,phredQuality);
    }

}
