/*
 * Created on Jun 8, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.jcvi.assembly.slice.Slice;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.junit.runners.Parameterized.Parameters;

public class TestBasicChurchillWatermanConsensusCaller extends
        TestConicConsensusCaller {

    public TestBasicChurchillWatermanConsensusCaller(List<Slice> slices,
            List<ConsensusResult> expectedConsensus) {
        super(slices, expectedConsensus);
    }
    @Override
    protected ConsensusCaller getConsensusCaller() {
        return new BasicChurchillWatermanConsensusCaller(PhredQuality.valueOf(30));
    }
    
    
    @Parameters
    public static Collection<?> data(){
       List<Object[]> data = new ArrayList<Object[]>();
       for(Entry<List<Slice>, List<ConsensusResult>> entry: ConsensusCallerTestUtil.generateChurchillWatermanData().entrySet()){
           data.add(new Object[]{entry.getKey(), entry.getValue()});
       }
        
        return data;
        
    }
}
