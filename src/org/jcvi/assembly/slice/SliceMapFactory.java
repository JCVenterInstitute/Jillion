/*
 * Created on Jan 5, 2010
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

public interface SliceMapFactory {

    SliceMap createNewSliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap, 
            DataStore<EncodedGlyphs<PhredQuality>> qualityDataStore);
}
