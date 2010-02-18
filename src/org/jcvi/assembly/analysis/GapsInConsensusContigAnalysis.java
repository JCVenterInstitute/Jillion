/*
 * Created on Mar 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import java.util.List;

import org.jcvi.assembly.Contig;

public class GapsInConsensusContigAnalysis implements ContigAnalysis{
    private final Contig contig;
    
    public GapsInConsensusContigAnalysis(Contig contig){
        this.contig = contig;
    }
    @Override
    public Contig getContig() {
        return contig;
    }
    
    public List<Integer> getGapIndexes(){
        return contig.getConsensus().getGapIndexes();
    }

}
