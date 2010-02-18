/*
 * Created on Feb 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.issue;

import org.jcvi.assembly.analysis.DefaultAnalysisIssue;

public class ReverseComplimentContigAnalysisIssue extends DefaultAnalysisIssue{

    public ReverseComplimentContigAnalysisIssue(Severity severity,
            String message) {
        super(severity, message);
    }

}
