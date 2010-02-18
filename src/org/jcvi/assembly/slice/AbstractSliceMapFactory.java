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

public abstract class AbstractSliceMapFactory implements SliceMapFactory{

  

    private final QualityValueStrategy qualityValueStrategy;
    /**
     * @param qualityValueStrategy
     */
    public AbstractSliceMapFactory(QualityValueStrategy qualityValueStrategy) {
        this.qualityValueStrategy = qualityValueStrategy;
    }
    @Override
    public SliceMap createNewSliceMap(
            CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap,
            DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore) {
        return createNewSliceMap(coverageMap, qualityDataStore, qualityValueStrategy);
    }
    
    protected abstract SliceMap createNewSliceMap(
            CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap,
                    DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore, QualityValueStrategy qualityValueStrategy);
}
