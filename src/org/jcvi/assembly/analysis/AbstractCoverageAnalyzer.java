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
 * Created on Jan 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.Placed;
import org.jcvi.assembly.PlacedRead;
import org.jcvi.assembly.coverage.CoverageMap;
import org.jcvi.assembly.coverage.CoverageRegion;


public  abstract class  AbstractCoverageAnalyzer<T extends Placed,R extends PlacedRead> implements CoverageAnalyzer<T,R>{
    private final int lowCoverageThreshold;
    private final int highCoverageTheshold;
    
    
    /**
     * @param lowCoverageThreshold
     * @param highCoverageTheshold
     */
    public AbstractCoverageAnalyzer(int lowCoverageThreshold,
            int highCoverageTheshold) {
        this.lowCoverageThreshold = lowCoverageThreshold;
        this.highCoverageTheshold = highCoverageTheshold;
    }


    @Override
    public ContigCoverageAnalysis<T> analyize(ContigCheckerStruct<R> struct){
        CoverageMap<CoverageRegion<T>> coverageMap = buildCoverageMap(struct);
        return buildContigCoverageAnalysis(struct.getContig(), coverageMap);
    }


    private ContigCoverageAnalysis<T> buildContigCoverageAnalysis(
            Contig<R> contig, CoverageMap<CoverageRegion<T>> coverageMap) {
        ContigCoverageAnalysis.Builder<T> analysisBuilder = 
                                    new ContigCoverageAnalysis.Builder<T>(contig);
        for(CoverageRegion<T> region : coverageMap){
            int coverage = region.getCoverage();
            if(coverage <=lowCoverageThreshold){
                analysisBuilder.addLowCoverageRegion(region);
            }
            else if(coverage >= highCoverageTheshold){
                analysisBuilder.addHighCoverageRegion(region);
            }
        }
        return analysisBuilder.build();
    }


    protected abstract CoverageMap<CoverageRegion<T>> buildCoverageMap(ContigCheckerStruct<R> struct);

}
