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
