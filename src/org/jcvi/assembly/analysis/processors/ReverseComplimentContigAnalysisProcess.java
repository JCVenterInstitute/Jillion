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
