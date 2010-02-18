/*
 * Created on Feb 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.processors;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.analysis.ContigCheckReportBuilder;
import org.jcvi.assembly.analysis.ContigCheckerStruct;
import org.jcvi.assembly.analysis.ContigCoverageAnalysis;
import org.jcvi.assembly.analysis.SequenceCoverageAnalyzer;
import org.jcvi.assembly.analysis.AnalysisIssue.Severity;
import org.jcvi.assembly.analysis.issue.HighCoverageRegionAnalysisIssue;
import org.jcvi.assembly.analysis.issue.LowCoverageRegionAnalysisIssue;
import org.jcvi.assembly.coverage.CoverageRegion;

public class SequenceCoverageAnalysisProcess<PR extends PlacedRead> extends AbstractContigAnalysisProcess<PR>{

    private SequenceCoverageAnalyzer<PR> sequenceCoverageAnalyzer;
    public SequenceCoverageAnalysisProcess(ContigCheckerStruct<PR> struct,  
            ContigCheckReportBuilder builder, SequenceCoverageAnalyzer<PR> sequenceCoverageAnalyzer){
        super(struct,builder);
        this.sequenceCoverageAnalyzer = sequenceCoverageAnalyzer;
    }
    @Override
    public void run() {
        ContigCoverageAnalysis<PR> analysis =sequenceCoverageAnalyzer.analyize(getStruct());
        for(CoverageRegion<PR> lowCoverageRegion: analysis.getLowCoverageRegions()){
            getBuilder().addAnalysisIssue(new LowCoverageRegionAnalysisIssue<PR>(Severity.MEDIUM, lowCoverageRegion, "sequence"));
        }
        for(CoverageRegion<PR> highCoverageRegion: analysis.getHighCoverageRegions()){
            getBuilder().addAnalysisIssue(new HighCoverageRegionAnalysisIssue<PR>(Severity.MEDIUM, highCoverageRegion, "sequence"));
        }
    }

}
