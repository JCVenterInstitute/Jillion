/*
 * Created on Mar 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.processors;

import org.jcvi.assembly.analysis.ContigCheckReportBuilder;
import org.jcvi.assembly.analysis.ContigCheckerStruct;
import org.jcvi.assembly.analysis.GapsInConsensusContigAnalysis;
import org.jcvi.assembly.analysis.GapsInConsensusContigAnalyzer;
import org.jcvi.assembly.analysis.AnalysisIssue.Severity;
import org.jcvi.assembly.analysis.issue.GapInConsensusContigAnalysisIssue;

public class GapsInConsensusContigAnalysisProcess extends AbstractContigAnalysisProcess{
    private static final GapsInConsensusContigAnalyzer ANALYZER= new GapsInConsensusContigAnalyzer();
    public GapsInConsensusContigAnalysisProcess(ContigCheckerStruct struct,
            ContigCheckReportBuilder builder) {
        super(struct, builder);
    }
    
    @Override
    public void run() {
        GapsInConsensusContigAnalysis analysis =ANALYZER.analyize(getStruct());
        for(Integer index : analysis.getGapIndexes()){
            getBuilder().addAnalysisIssue(new GapInConsensusContigAnalysisIssue(Severity.HIGH, index));
        }
        
    }

}
