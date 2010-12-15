/*
 * Created on Feb 19, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.analysis.processors.QualityClassContigMapAnalysisProcess;
import org.jcvi.glyph.qualClass.QualityClass;
import org.jcvi.pipeline.Process;

public class InternalSangerContigChecker<R extends PlacedRead> extends ContigChecker<R> {

    public InternalSangerContigChecker(InternalSangerContigCheckerStruct<R> struct,
            int percentReverseComplimentedDifferenceThreshold,
            int lowSequenceCoverageThreshold, int highSequenceCoverageThreshold) {
        super(struct, percentReverseComplimentedDifferenceThreshold,
                lowSequenceCoverageThreshold, highSequenceCoverageThreshold);
    }

    @Override
    protected List<Process> createProcesses() {
        List<Process> analyizers = new ArrayList<Process>();
        analyizers.addAll(super.createProcesses());
        analyizers.add(new QualityClassContigMapAnalysisProcess<R>(this.getContigCheckBuilder(), this.getStruct(),QualityClass.valueOf(10)));
        return analyizers;
    }

}
