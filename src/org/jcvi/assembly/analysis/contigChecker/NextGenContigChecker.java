/*
 * Created on Feb 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.contigChecker;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.analysis.ContigChecker;
import org.jcvi.assembly.analysis.ContigCheckerStruct;
import org.jcvi.assembly.analysis.SequenceCoverageAnalyzer;
import org.jcvi.assembly.analysis.processors.SequenceCoverageAnalysisProcess;
import org.jcvi.pipeline.Process;

public class NextGenContigChecker<R extends PlacedRead> extends ContigChecker<R>{

    public NextGenContigChecker(ContigCheckerStruct<R> struct,
            int percentReverseComplimentedDifferenceThreshold,
            int lowSequenceCoverageThreshold, int highSequenceCoverageThreshold) {
        super(struct, percentReverseComplimentedDifferenceThreshold,
                lowSequenceCoverageThreshold, highSequenceCoverageThreshold);
    }

    @Override
    protected List<Process> createProcesses() {
        List<Process> processes = new ArrayList<Process>();
        processes.add(new SequenceCoverageAnalysisProcess<R>(getStruct(), getContigCheckBuilder(), 
                new SequenceCoverageAnalyzer<R>(this.getLowSequenceCoverageThreshold(),getHighSequenceCoverageThreshold())));
       return processes;
    }

}
