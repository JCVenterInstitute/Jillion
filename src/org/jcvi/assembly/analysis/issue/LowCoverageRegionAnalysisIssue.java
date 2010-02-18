/*
 * Created on Feb 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.issue;

import org.jcvi.assembly.Placed;
import org.jcvi.assembly.coverage.CoverageRegion;

public class LowCoverageRegionAnalysisIssue<P extends Placed> extends AbstractCoverageRegionAnalysisIssue<P>{

    public LowCoverageRegionAnalysisIssue(Severity severity, CoverageRegion<P> coverageRegion, String coverageType) {
        super(severity, coverageRegion, "low " + coverageType);
    }

}
