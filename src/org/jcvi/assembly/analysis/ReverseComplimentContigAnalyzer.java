/*
 * Created on Jan 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

import org.jcvi.assembly.PlacedRead;
import org.jcvi.sequence.SequenceDirection;

public class ReverseComplimentContigAnalyzer<R extends PlacedRead> implements ContigAnalyzer<R>{

    
    @Override
    public ReverseComplimentContigAnalysis analyize(ContigCheckerStruct<R> struct) {

        int numReverseComplimentedReads = 0;
        for(R placedRead : struct.getContig().getPlacedReads()){
            if(placedRead.getSequenceDirection() == SequenceDirection.REVERSE){
                numReverseComplimentedReads++;
            }
        }
        return new ReverseComplimentContigAnalysis(struct.getContig(), numReverseComplimentedReads);
    }

}
