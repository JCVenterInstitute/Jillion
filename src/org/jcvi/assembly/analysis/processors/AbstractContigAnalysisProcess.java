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
 * Created on Mar 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis.processors;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.analysis.ContigCheckReportBuilder;
import org.jcvi.assembly.analysis.ContigCheckerStruct;
import org.jcvi.pipeline.Process;

public abstract class AbstractContigAnalysisProcess<PR extends PlacedRead> implements Process{
    private final ContigCheckReportBuilder builder;
    private final ContigCheckerStruct<PR> struct;
    /**
     * @param struct
     * @param builder
     */
    public AbstractContigAnalysisProcess(ContigCheckerStruct<PR> struct,
            ContigCheckReportBuilder builder) {
        this.struct = struct;
        this.builder = builder;
    }
    protected ContigCheckReportBuilder getBuilder() {
        return builder;
    }
    protected ContigCheckerStruct<PR> getStruct() {
        return struct;
    }
    
    
}
