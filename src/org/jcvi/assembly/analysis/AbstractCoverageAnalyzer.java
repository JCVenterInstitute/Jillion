/*
 * Created on Jan 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.Placed;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;


public  abstract class  AbstractCoverageAnalyzer<T extends Placed,R extends PlacedRead> implements CoverageAnalyzer<T,R>{
    private final int lowCoverageThreshold;
    private final int highCoverageTheshold;
    
    
    /**
     * @param lowCoverageThreshold
     * @param highCoverageTheshold
     */
    public AbstractCoverageAnalyzer(int lowCoverageThreshold,
            int highCoverageTheshold) {
        this.lowCoverageThreshold = lowCoverageThreshold;
        this.highCoverageTheshold = highCoverageTheshold;
    }


    @Override
    public ContigCoverageAnalysis<T> analyize(ContigCheckerStruct<R> struct){
        CoverageMap<CoverageRegion<T>> coverageMap = buildCoverageMap(struct);
        return buildContigCoverageAnalysis(struct.getContig(), coverageMap);
    }


    private ContigCoverageAnalysis<T> buildContigCoverageAnalysis(
            Contig<R> contig, CoverageMap<CoverageRegion<T>> coverageMap) {
        ContigCoverageAnalysis.Builder<T> analysisBuilder = 
                                    new ContigCoverageAnalysis.Builder<T>(contig);
        for(CoverageRegion<T> region : coverageMap){
            int coverage = region.getCoverage();
            if(coverage <=lowCoverageThreshold){
                analysisBuilder.addLowCoverageRegion(region);
            }
            else if(coverage >= highCoverageTheshold){
                analysisBuilder.addHighCoverageRegion(region);
            }
        }
        return analysisBuilder.build();
    }


    protected abstract CoverageMap<CoverageRegion<T>> buildCoverageMap(ContigCheckerStruct<R> struct);

}
