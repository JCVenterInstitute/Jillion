/*
 * Created on Jan 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;

public  class  SequenceCoverageAnalyzer<R extends PlacedRead> extends AbstractCoverageAnalyzer<R,R>{
    
    
    /**
     * @param lowCoverageThreshold
     * @param highCoverageTheshold
     */
    public SequenceCoverageAnalyzer(int lowCoverageThreshold,
            int highCoverageTheshold) {
        super(lowCoverageThreshold,highCoverageTheshold);
    }

    @Override
    protected CoverageMap<CoverageRegion<R>> buildCoverageMap(ContigCheckerStruct<R> struct) {
        return struct.getSequenceCoverageMap();
    }
    
    
}
