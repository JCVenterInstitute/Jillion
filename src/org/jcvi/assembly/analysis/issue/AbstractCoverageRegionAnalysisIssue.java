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
 * Created on Feb 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.issue;

import org.jcvi.Range;
import org.jcvi.Range.CoordinateSystem;
import org.jcvi.assembly.Placed;
import org.jcvi.assembly.analysis.DefaultAnalysisIssue;
import org.jcvi.assembly.coverage.CoverageRegion;

public abstract class AbstractCoverageRegionAnalysisIssue <P extends Placed> extends DefaultAnalysisIssue{

    public AbstractCoverageRegionAnalysisIssue(Severity severity, CoverageRegion<P> coverageRegion, String type) {
        super(severity, String.format("%s has a %s coverage of %d", 
                Range.buildRange(coverageRegion.getStart(),coverageRegion.getEnd()).convertRange(CoordinateSystem.RESIDUE_BASED), type, coverageRegion.getCoverage()));
    }
}
