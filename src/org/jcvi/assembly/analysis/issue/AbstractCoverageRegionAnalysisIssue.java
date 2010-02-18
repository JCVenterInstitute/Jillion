/*
 * Created on Feb 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.issue;

import org.jcvi.Range;
import org.jcvi.assembly.Placed;
import org.jcvi.assembly.analysis.DefaultAnalysisIssue;
import org.jcvi.assembly.coverage.CoverageRegion;

public abstract class AbstractCoverageRegionAnalysisIssue <P extends Placed> extends DefaultAnalysisIssue{

    public AbstractCoverageRegionAnalysisIssue(Severity severity, CoverageRegion<P> coverageRegion, String type) {
        super(severity, String.format("%s has a %s coverage of %d", 
                Range.buildRange(coverageRegion.getStart(),coverageRegion.getEnd()), type, coverageRegion.getCoverage()));
    }
}
