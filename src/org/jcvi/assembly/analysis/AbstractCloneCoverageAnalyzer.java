/*
 * Created on Jan 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import java.util.List;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedContigClone;
import org.jcvi.assembly.PlacedRead;

import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageMap;

public abstract class AbstractCloneCoverageAnalyzer<R extends PlacedRead> extends AbstractCoverageAnalyzer<PlacedContigClone,R>{

    public AbstractCloneCoverageAnalyzer(int lowCoverageThreshold,
            int highCoverageTheshold) {
        super(lowCoverageThreshold, highCoverageTheshold);
    }

    @Override
    protected CoverageMap<CoverageRegion<PlacedContigClone>> buildCoverageMap(ContigCheckerStruct<R> struct) {
        List<PlacedContigClone> placedContigClones = buildPlacedContigClonesList(struct.getContig());
        return new DefaultCoverageMap.Builder<PlacedContigClone>(placedContigClones).build();
    }
    
    protected abstract List<PlacedContigClone> buildPlacedContigClonesList(Contig<R> contig);


}
