/*
 * Created on Feb 22, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.util.slice;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.AssembledRead;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nt.Nucleotide;

public class LargeNoQualitySliceMap extends LargeSliceMap{
    public static final PhredQuality DEFAULT_PHRED_QUALITY = PhredQuality.valueOf(0);
    private final PhredQuality phredQuality;
    
    public LargeNoQualitySliceMap(
            CoverageMap<? extends CoverageRegion<? extends AssembledRead>> coverageMap,
             Range range, int cacheSize,PhredQuality phredQuality) {
        super(coverageMap, null, null, range,cacheSize);
        this.phredQuality = phredQuality;
    }
    public LargeNoQualitySliceMap(
            CoverageMap<? extends CoverageRegion<? extends AssembledRead>> coverageMap,
             Range range, PhredQuality phredQuality) {
        this(coverageMap,range, LargeSliceMap.DEFAULT_CACHE_SIZE,phredQuality);
    }
    public LargeNoQualitySliceMap(
            CoverageMap<? extends CoverageRegion<? extends AssembledRead>> coverageMap,
             Range range) {
        this(coverageMap,range, DEFAULT_PHRED_QUALITY);
    }
    public LargeNoQualitySliceMap(
            CoverageMap<? extends CoverageRegion<? extends AssembledRead>> coverageMap
             ) {
        this(coverageMap,DEFAULT_PHRED_QUALITY);
    }
    public LargeNoQualitySliceMap(
            CoverageMap<? extends CoverageRegion<? extends AssembledRead>> coverageMap,
             PhredQuality phredQuality) {
        this(coverageMap,
                Range.create(0,coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).asRange().getEnd()),
                phredQuality);
    }
    public LargeNoQualitySliceMap(
            CoverageMap<? extends CoverageRegion<? extends AssembledRead>> coverageMap,
                    int cacheSize,
             PhredQuality phredQuality) {
        this(coverageMap,
                Range.create(0,coverageMap.getRegion(coverageMap.getNumberOfRegions()-1).asRange().getEnd()),
                cacheSize,phredQuality);
    }

    @Override
    protected DefaultSliceElement createSliceElementFor(
            QualityValueStrategy qualityValueStrategy, int gappedIndex,
            AssembledRead realRead, Sequence<PhredQuality> qualities) {
        final Nucleotide calledBase = realRead.getNucleotideSequence().get(gappedIndex);
        return new DefaultSliceElement(realRead.getId(), calledBase, 
                phredQuality, 
                realRead.getDirection());
    }

   
}
