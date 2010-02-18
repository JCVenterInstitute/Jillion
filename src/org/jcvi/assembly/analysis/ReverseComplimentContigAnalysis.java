/*
 * Created on Jan 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import org.jcvi.assembly.Contig;

public class ReverseComplimentContigAnalysis implements ContigAnalysis{
    private final Contig contig;
    private final int numberOfReverseComplimentedReads;
    private final float percentReversecomplimented;
    
    
    public ReverseComplimentContigAnalysis(Contig contig, int numberOfReverseComplimentedReads){
        this.contig = contig;
        this.numberOfReverseComplimentedReads = numberOfReverseComplimentedReads;
        this.percentReversecomplimented = computePercentReverseComplimented(contig);
    }

    private float computePercentReverseComplimented(Contig contig) {
        float numberOfReads = contig.getNumberOfReads();
        return numberOfReverseComplimentedReads/numberOfReads * 100;
    }

    @Override
    public Contig getContig() {
        return contig;
    }

    public final int getNumberOfReverseComplimentedReads() {
        return numberOfReverseComplimentedReads;
    }

    public float getPercentReverseComplimented() {
        return percentReversecomplimented;
    }
    
    
    
    
}
