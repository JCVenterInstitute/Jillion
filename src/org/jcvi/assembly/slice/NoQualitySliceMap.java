/*
 * Created on Feb 23, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.contig.QualityValueStrategy;
import org.jcvi.common.core.assembly.coverage.CoverageMap;
import org.jcvi.common.core.assembly.coverage.CoverageRegion;
import org.jcvi.common.core.seq.Sequence;
import org.jcvi.common.core.seq.nuc.NucleotideGlyph;
import org.jcvi.common.core.seq.qual.PhredQuality;

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
