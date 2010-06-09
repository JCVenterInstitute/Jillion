/*
 * Created on Feb 22, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice;

import org.jcvi.Range;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.contig.qual.QualityValueStrategy;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;

public class LargeNoQualitySliceMap extends LargeSliceMap{
    public static final PhredQuality DEFAULT_PHRED_QUALITY = PhredQuality.valueOf(0);
    private final PhredQuality phredQuality;
    
    public LargeNoQualitySliceMap(
            CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap,
             Range range, int cacheSize,PhredQuality phredQuality) {
        super(coverageMap, null, null, range,cacheSize);
        this.phredQuality = phredQuality;
    }
    public LargeNoQualitySliceMap(
            CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap,
             Range range, PhredQuality phredQuality) {
        this(coverageMap,range, LargeSliceMap.DEFAULT_CACHE_SIZE,phredQuality);
    }
    public LargeNoQualitySliceMap(
            CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap,
             Range range) {
        this(coverageMap,range, DEFAULT_PHRED_QUALITY);
    }
    public LargeNoQualitySliceMap(
            CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap
             ) {
        this(coverageMap,DEFAULT_PHRED_QUALITY);
    }
    public LargeNoQualitySliceMap(
            CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap,
             PhredQuality phredQuality) {
        this(coverageMap,
                Range.buildRange(0,coverageMap.getRegion(coverageMap.getSize()-1).getEnd()),
                phredQuality);
    }
    public LargeNoQualitySliceMap(
            CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap,
                    int cacheSize,
             PhredQuality phredQuality) {
        this(coverageMap,
                Range.buildRange(0,coverageMap.getRegion(coverageMap.getSize()-1).getEnd()),
                cacheSize,phredQuality);
    }

    @Override
    protected DefaultSliceElement createSliceElementFor(
            QualityValueStrategy qualityValueStrategy, int gappedIndex,
            PlacedRead realRead, EncodedGlyphs<PhredQuality> qualities) {
        final NucleotideGlyph calledBase = realRead.getEncodedGlyphs().get(gappedIndex);
        return new DefaultSliceElement(realRead.getId(), calledBase, 
                phredQuality, 
                realRead.getSequenceDirection());
    }

   
}
