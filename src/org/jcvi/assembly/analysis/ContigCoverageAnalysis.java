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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.Placed;
import org.jcvi.assembly.coverage.CoverageRegion;

public final class ContigCoverageAnalysis< T extends Placed> implements ContigAnalysis {
    private final List<CoverageRegion<T>> lowCoverageRegions;
    private final List<CoverageRegion<T>> highCoverageRegions;
    private final Contig contig;
    /**
     * @param lowCoverageRegions
     * @param highCoverageRegions
     */
    private ContigCoverageAnalysis(Contig contig, List<CoverageRegion<T>> lowCoverageRegions,
            List<CoverageRegion<T>> highCoverageRegions) {
        this.contig = contig;
        this.lowCoverageRegions = lowCoverageRegions;
        this.highCoverageRegions = highCoverageRegions;
    }
    public List<CoverageRegion<T>> getLowCoverageRegions() {
        return lowCoverageRegions;
    }
    public List<CoverageRegion<T>> getHighCoverageRegions() {
        return highCoverageRegions;
    }
    @Override
    public Contig getContig(){
        return contig;
    }
    
    public static final class Builder<T extends Placed>{
        private final List<CoverageRegion<T>> lowCoverageRegions;
        private final List<CoverageRegion<T>> highCoverageRegions;
        private final Contig contig;
        public Builder(Contig contig){
            this.contig = contig;
            this.lowCoverageRegions = new ArrayList<CoverageRegion<T>>();
            this.highCoverageRegions = new ArrayList<CoverageRegion<T>>();
        }
        
        public Builder addLowCoverageRegion(CoverageRegion<T> lowCoverageRegion){
            this.lowCoverageRegions.add(lowCoverageRegion);
            return this;
        }
        
        public Builder addHighCoverageRegion(CoverageRegion<T> highCoverageRegion){
            this.highCoverageRegions.add(highCoverageRegion);
            return this;
        }
        
        public ContigCoverageAnalysis<T> build(){
            return new ContigCoverageAnalysis<T>(contig,
                    Collections.unmodifiableList(lowCoverageRegions),
                    Collections.unmodifiableList(highCoverageRegions));
        }
        
    }
}
