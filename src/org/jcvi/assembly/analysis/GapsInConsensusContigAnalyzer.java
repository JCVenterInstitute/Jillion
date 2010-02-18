/*
 * Created on Mar 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.analysis;

public class GapsInConsensusContigAnalyzer implements ContigAnalyzer{

    @Override
    public GapsInConsensusContigAnalysis analyize(ContigCheckerStruct struct) {
        return new GapsInConsensusContigAnalysis(struct.getContig());
    }

}
