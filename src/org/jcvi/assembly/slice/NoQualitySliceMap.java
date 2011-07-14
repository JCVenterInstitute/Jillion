/*
 * Created on Feb 23, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.glyph.Sequence;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class NoQualitySliceMap extends DefaultSliceMap{
    public static final PhredQuality DEFAULT_PHRED_QUALITY = PhredQuality.valueOf(0);
    private final PhredQuality phredQuality;
    
    public NoQualitySliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap, 
                    PhredQuality phredQuality) {
        super(coverageMap, null, null);
        this.phredQuality = phredQuality;
    }
    
    public NoQualitySliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap) {
        this(coverageMap,DEFAULT_PHRED_QUALITY);
    }
    @Override
    protected DefaultSliceElement createSliceElementFor(
            QualityValueStrategy qualityValueStrategy, int gappedIndex,
            PlacedRead realRead, Sequence<PhredQuality> qualities) {
        final NucleotideGlyph calledBase = realRead.getEncodedGlyphs().get(gappedIndex);
        return new DefaultSliceElement(realRead.getId(), calledBase, 
                phredQuality, 
                realRead.getSequenceDirection());
    }
    

}
