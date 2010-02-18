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
import java.util.Collections;
import java.util.List;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.analysis.processors.HighQualityDifferenceContigProcess;
import org.jcvi.assembly.analysis.processors.QualityClassContigMapAnalysisProcess;
import org.jcvi.assembly.analysis.processors.ReverseComplimentContigAnalysisProcess;
import org.jcvi.assembly.analysis.processors.SequenceCoverageAnalysisProcess;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.qualClass.QualityClass;
import org.jcvi.pipeline.AbstractPipeline;
import org.jcvi.pipeline.Process;


public class ContigChecker<R extends PlacedRead> extends AbstractPipeline {
    private final DefaultContigCheckReport.Builder contigCheckBuilder;
    private final int percentReverseComplimentedDifferenceThreshold;
    private final int lowSequenceCoverageThreshold;
    private final int highSequenceCoverageThreshold;
   
    private ContigCheckReport report;

    private ContigCheckerStruct<R> struct;
    
    public ContigChecker(ContigCheckerStruct<R> struct,int percentReverseComplimentedDifferenceThreshold, int lowSequenceCoverageThreshold, int highSequenceCoverageThreshold) {
        super();
        this.struct = struct;
        contigCheckBuilder = new DefaultContigCheckReport.Builder(struct.getContig());
        this.percentReverseComplimentedDifferenceThreshold =  percentReverseComplimentedDifferenceThreshold;
        this.lowSequenceCoverageThreshold =lowSequenceCoverageThreshold;
        this.highSequenceCoverageThreshold =highSequenceCoverageThreshold;
      
    }

    public int getLowSequenceCoverageThreshold() {
        return lowSequenceCoverageThreshold;
    }

    public int getHighSequenceCoverageThreshold() {
        return highSequenceCoverageThreshold;
    }

    public ContigCheckerStruct<R> getStruct() {
        return struct;
    }

    public DefaultContigCheckReport.Builder getContigCheckBuilder() {
        return contigCheckBuilder;
    }

    @Override
    public void run() {
        super.run();
        report = contigCheckBuilder.build();
    }
    public ContigCheckReport<R> getContigCheckReport() {
        return report;
    }
    @Override
    protected List<Process> createPostProcesses() {
        return Collections.<Process>emptyList();
    }

    @Override
    protected List<Process> createPreProcesses() {
        return Collections.<Process>emptyList();
    }

    @Override
    protected List<Process> createProcesses() {
        List<Process> analyizers = new ArrayList<Process>();
        final PhredQuality highqualityThreshold = PhredQuality.valueOf((byte)30);
       // analyizers.add(new GapsInConsensusContigAnalysisProcess(struct,contigCheckBuilder));
        analyizers.add(new QualityClassContigMapAnalysisProcess<R>(contigCheckBuilder, struct,QualityClass.valueOf((byte)10)));
        
        analyizers.add(new ReverseComplimentContigAnalysisProcess<R>(struct, percentReverseComplimentedDifferenceThreshold, contigCheckBuilder));
        analyizers.add(new SequenceCoverageAnalysisProcess<R>(struct, contigCheckBuilder, new SequenceCoverageAnalyzer<R>(lowSequenceCoverageThreshold,highSequenceCoverageThreshold)));
        analyizers.add(new HighQualityDifferenceContigProcess(contigCheckBuilder, struct, highqualityThreshold));
       return analyizers;
    }
    
    

}
