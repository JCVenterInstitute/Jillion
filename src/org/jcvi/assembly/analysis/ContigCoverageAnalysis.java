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
