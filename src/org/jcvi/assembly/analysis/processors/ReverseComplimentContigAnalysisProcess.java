/*
 * Created on Feb 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.processors;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.analysis.ContigCheckReportBuilder;
import org.jcvi.assembly.analysis.ContigCheckerStruct;
import org.jcvi.assembly.analysis.ReverseComplimentContigAnalysis;
import org.jcvi.assembly.analysis.ReverseComplimentContigAnalyzer;
import org.jcvi.assembly.analysis.AnalysisIssue.Severity;
import org.jcvi.assembly.analysis.issue.ReverseComplimentContigAnalysisIssue;

public class ReverseComplimentContigAnalysisProcess<PR extends PlacedRead> extends AbstractContigAnalysisProcess<PR>{
    

    private int percentDifferenceThreshold;
    private final ReverseComplimentContigAnalyzer analyzer = new ReverseComplimentContigAnalyzer<PR>();
    
    public ReverseComplimentContigAnalysisProcess(ContigCheckerStruct struct, int percentDifferenceThreshold, 
            ContigCheckReportBuilder builder){
        super(struct, builder);
        this.percentDifferenceThreshold = percentDifferenceThreshold;
    }
    @Override
    public void run() {
        ReverseComplimentContigAnalysis analysis= analyzer.analyize(getStruct());
        float percentReverseComplimented =analysis.getPercentReverseComplimented();
        float precentNotComplimented = 100 - percentReverseComplimented;
        float difference = Math.abs(precentNotComplimented - percentReverseComplimented);
        if(difference > percentDifferenceThreshold){
            getBuilder().addAnalysisIssue(createNewIssue(percentReverseComplimented));
        }        
    }
    private ReverseComplimentContigAnalysisIssue createNewIssue(float percentReverseComplimented) {
        final String message = String.format(
                            "percent of reverse complimented reads = %.2f%%" ,
                                percentReverseComplimented);
        
         return   new ReverseComplimentContigAnalysisIssue(Severity.HIGH, message);
    }

}
