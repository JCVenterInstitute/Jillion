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
package org.jcvi.assembly.analysis;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;

public final class DefaultContigCheckReport<P extends PlacedRead> implements ContigCheckReport<P>{
    private List<AnalysisIssue> contigAnalyses;
    private Contig<P> contig;
    private DefaultContigCheckReport(Contig<P> contig, List<AnalysisIssue> contigAnalyses){
        this.contigAnalyses = contigAnalyses;
        this.contig = contig;
    }
    @Override
    public List<AnalysisIssue> getAnalysisIssues() {
        return contigAnalyses;
    }
    
    public static class Builder<P extends PlacedRead> implements ContigCheckReportBuilder<P>{
        private List<AnalysisIssue> contigAnalyses;
        private Contig<P> contig;
        public Builder(Contig<P> contig){
            this.contig = contig;
            contigAnalyses = new ArrayList<AnalysisIssue>();
        }
        @Override
        public ContigCheckReportBuilder addAnalysisIssue(
                AnalysisIssue issue) {
            contigAnalyses.add(issue);
            return this;
        }

        @Override
        public ContigCheckReport build() {
            return new DefaultContigCheckReport(contig, contigAnalyses);
        }
        
    }

    @Override
    public Contig<P> getContig() {
        return contig;
    }

}
