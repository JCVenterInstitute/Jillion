/*
 * Created on Feb 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import java.util.List;

import org.jcvi.assembly.Placed;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.analysis.processors.CloneCoverageAnalysisProcess;
import org.jcvi.pipeline.Process;

public class ContigCheckerForClonedReads<P extends Placed, R extends PlacedRead> extends ContigChecker<R>{
    private CoverageAnalyzer<P, R> cloneCoverageAnalyzer;
    
    public ContigCheckerForClonedReads(ContigCheckerStruct struct,  int percentReverseComplimentedDifferenceThreshold,
            int lowSequenceCoverageThreshold, int highSequenceCoverageThreshold,
            CoverageAnalyzer cloneCoverageAnalyzer) {
        
        super(struct, percentReverseComplimentedDifferenceThreshold,lowSequenceCoverageThreshold, highSequenceCoverageThreshold);
        this.cloneCoverageAnalyzer = cloneCoverageAnalyzer;
    }

    @Override
    protected List<Process> createProcesses() {
        List<Process> analyizers= super.createProcesses();
        analyizers.add(new CloneCoverageAnalysisProcess<P, R>(getStruct(), getContigCheckBuilder(),cloneCoverageAnalyzer ));
    
        return analyizers;
    }
    
}
