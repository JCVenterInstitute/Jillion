/*
 * Created on Feb 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import org.jcvi.assembly.PlacedRead;

public interface ContigCheckReportBuilder<P extends PlacedRead> {

    ContigCheckReportBuilder<P> addAnalysisIssue(AnalysisIssue issue);
    
    ContigCheckReport<P> build();
}
