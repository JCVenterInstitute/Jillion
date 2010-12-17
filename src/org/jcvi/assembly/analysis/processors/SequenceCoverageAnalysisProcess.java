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
package org.jcvi.assembly.analysis.processors;

import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.analysis.ContigCheckReportBuilder;
import org.jcvi.assembly.analysis.ContigCheckerStruct;
import org.jcvi.assembly.analysis.ContigCoverageAnalysis;
import org.jcvi.assembly.analysis.SequenceCoverageAnalyzer;
import org.jcvi.assembly.analysis.AnalysisIssue.Severity;
import org.jcvi.assembly.analysis.issue.HighCoverageRegionAnalysisIssue;
import org.jcvi.assembly.analysis.issue.LowCoverageRegionAnalysisIssue;
import org.jcvi.assembly.coverage.CoverageRegion;
import org.jcvi.assembly.coverage.DefaultCoverageRegion;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;

public class SequenceCoverageAnalysisProcess<PR extends PlacedRead> extends AbstractContigAnalysisProcess<PR>{

    private SequenceCoverageAnalyzer<PR> sequenceCoverageAnalyzer;
    public SequenceCoverageAnalysisProcess(ContigCheckerStruct<PR> struct,  
            ContigCheckReportBuilder builder, SequenceCoverageAnalyzer<PR> sequenceCoverageAnalyzer){
        super(struct,builder);
        this.sequenceCoverageAnalyzer = sequenceCoverageAnalyzer;
    }
    @Override
    public void run() {
        final ContigCheckerStruct<PR> struct = getStruct();
        NucleotideEncodedGlyphs consensus =struct.getContig().getConsensus();
        ContigCoverageAnalysis<PR> analysis =sequenceCoverageAnalyzer.analyize(struct);
        for(CoverageRegion<PR> lowCoverageRegion: analysis.getLowCoverageRegions()){

            CoverageRegion<PR> ungappedCoverageRegion = convertToUngappedCoverageRegion(consensus, lowCoverageRegion);
            getBuilder().addAnalysisIssue(new LowCoverageRegionAnalysisIssue<PR>(Severity.MEDIUM, ungappedCoverageRegion, "sequence"));
        }
        for(CoverageRegion<PR> highCoverageRegion: analysis.getHighCoverageRegions()){
            CoverageRegion<PR> ungappedCoverageRegion = convertToUngappedCoverageRegion(consensus, highCoverageRegion);
            getBuilder().addAnalysisIssue(new HighCoverageRegionAnalysisIssue<PR>(Severity.MEDIUM, ungappedCoverageRegion, "sequence"));
        }
    }
    private CoverageRegion<PR> convertToUngappedCoverageRegion(
            NucleotideEncodedGlyphs consensus,
            CoverageRegion<PR> highCoverageRegion) {
        return  new DefaultCoverageRegion.Builder(
                consensus.convertGappedValidRangeIndexToUngappedValidRangeIndex(AssemblyUtil.getLeftFlankingNonGapIndex(consensus, (int)highCoverageRegion.getStart())), highCoverageRegion)
                .end(
                        consensus.convertGappedValidRangeIndexToUngappedValidRangeIndex(AssemblyUtil.getLeftFlankingNonGapIndex(consensus, (int)highCoverageRegion.getEnd())))
                .build();
    }

}
