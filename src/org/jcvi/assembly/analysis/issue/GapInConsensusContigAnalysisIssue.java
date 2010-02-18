/*
 * Created on Mar 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.issue;


import org.jcvi.assembly.analysis.DefaultAnalysisIssue;

public class GapInConsensusContigAnalysisIssue extends DefaultAnalysisIssue{

    public GapInConsensusContigAnalysisIssue(Severity severity, int gapIndex) {
        super(severity, String.format("gap in consensus at offset %d", gapIndex));
    }

}
