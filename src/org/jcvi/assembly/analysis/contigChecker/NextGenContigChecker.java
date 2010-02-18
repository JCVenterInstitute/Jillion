/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
