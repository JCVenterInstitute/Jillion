/*
 * Created on Feb 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.contig;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.VirtualPlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.datastore.DataStore;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.Glyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.qualClass.QualityClass;

public interface QualityClassComputer<P extends PlacedRead, G extends Glyph> {

    QualityClass computeQualityClass(CoverageMap<CoverageRegion<VirtualPlacedRead<P>>> coverageMap,
            DataStore<EncodedGlyphs<PhredQuality>> qualityFastaMap,
            EncodedGlyphs<G> consensus,int index);
}
