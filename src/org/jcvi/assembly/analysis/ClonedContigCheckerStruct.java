/*
 * Created on Mar 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.Placed;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityDataStore;

public abstract class ClonedContigCheckerStruct<R extends PlacedRead, C extends Placed> extends ContigCheckerStruct<R> {
    private CoverageMap<CoverageRegion<C>> cloneCoverageMap;
    public ClonedContigCheckerStruct(Contig<R> contig,
            QualityDataStore qualityDataStore, PhredQuality qualityThreshold) {
        super(contig, qualityDataStore,qualityThreshold);
    }
    
    public synchronized CoverageMap<CoverageRegion<C>> getCloneCoverageMap(){
        if(cloneCoverageMap ==null){
            cloneCoverageMap = createCloneCoverageMap();
        }
        return cloneCoverageMap;
    }

    protected abstract CoverageMap<CoverageRegion<C>> createCloneCoverageMap();

}
