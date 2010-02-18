/*
 * Created on Feb 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.processors;

import org.jcvi.assembly.Placed;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.analysis.ContigCheckReportBuilder;
import org.jcvi.assembly.analysis.ContigCheckerStruct;
import org.jcvi.assembly.analysis.ContigCoverageAnalysis;
import org.jcvi.assembly.analysis.CoverageAnalyzer;
import org.jcvi.assembly.analysis.AnalysisIssue.Severity;
import org.jcvi.assembly.analysis.issue.HighCoverageRegionAnalysisIssue;
import org.jcvi.assembly.analysis.issue.LowCoverageRegionAnalysisIssue;
import org.jcvi.assembly.coverage.CoverageRegion;

public class CloneCoverageAnalysisProcess<P extends Placed,PR extends PlacedRead> extends AbstractContigAnalysisProcess<PR>{


    private CoverageAnalyzer<P,PR> cloneCoverageAnalyzer;
    
    public CloneCoverageAnalysisProcess(ContigCheckerStruct<PR> struct,  
            ContigCheckReportBuilder builder, CoverageAnalyzer<P,PR> cloneCoverageAnalyzer){
        super(struct, builder);
        this.cloneCoverageAnalyzer = cloneCoverageAnalyzer;
    }
    @Override
    public void run() {
        ContigCoverageAnalysis<P> analysis =cloneCoverageAnalyzer.analyize(getStruct());
        for(CoverageRegion<P> lowCoverageRegion: analysis.getLowCoverageRegions()){
            getBuilder().addAnalysisIssue(new LowCoverageRegionAnalysisIssue<P>(Severity.MEDIUM, lowCoverageRegion, "clone"));
        }
        for(CoverageRegion<P> highCoverageRegion: analysis.getHighCoverageRegions()){
            getBuilder().addAnalysisIssue(new HighCoverageRegionAnalysisIssue<P>(Severity.MEDIUM, highCoverageRegion,"clone"));
        }
    }


}
