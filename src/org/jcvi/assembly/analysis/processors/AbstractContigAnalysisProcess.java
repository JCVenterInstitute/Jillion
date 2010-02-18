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
