/*
 * Created on Feb 23, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.slice;

import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.contig.QualityValueStrategy;
import org.jcvi.common.core.assembly.coverage.CoverageMap;
import org.jcvi.common.core.assembly.coverage.CoverageRegion;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;

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
        final Nucleotide calledBase = realRead.getSequence().get(gappedIndex);
        return new DefaultSliceElement(realRead.getId(), calledBase, 
                phredQuality, 
                realRead.getDirection());
    }
    

}
