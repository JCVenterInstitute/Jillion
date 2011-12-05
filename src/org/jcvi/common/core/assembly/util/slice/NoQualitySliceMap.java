/*
 * Created on Feb 23, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.util.slice;

import org.jcvi.common.core.assembly.Contig;
import org.jcvi.common.core.assembly.PlacedRead;
import org.jcvi.common.core.assembly.QualityValueStrategy;
import org.jcvi.common.core.assembly.util.coverage.CoverageMap;
import org.jcvi.common.core.assembly.util.coverage.CoverageRegion;
import org.jcvi.common.core.assembly.util.coverage.DefaultCoverageMap;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.Nucleotide;

public class NoQualitySliceMap extends DefaultSliceMap{
    
    public static <P extends PlacedRead> SliceMap create(Contig<P> contig){
        return new NoQualitySliceMap(DefaultCoverageMap.buildCoverageMap(contig));
    }
    public static final PhredQuality DEFAULT_PHRED_QUALITY = PhredQuality.valueOf(30);
    
    public NoQualitySliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap, 
                    PhredQuality phredQuality) {
        super(coverageMap, null, null,phredQuality);
    }
    
    public NoQualitySliceMap(CoverageMap<? extends CoverageRegion<? extends PlacedRead>> coverageMap) {
        this(coverageMap,DEFAULT_PHRED_QUALITY);
    }
    @Override
    protected DefaultSliceElement createSliceElementFor(
            QualityValueStrategy qualityValueStrategy, int gappedIndex,
            PlacedRead realRead, Sequence<PhredQuality> qualities) {
        final Nucleotide calledBase = realRead.getNucleotideSequence().get(gappedIndex);
        return new DefaultSliceElement(realRead.getId(), calledBase, 
                getDefaultQuality(), 
                realRead.getDirection());
    }
    

}
