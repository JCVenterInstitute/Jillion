/*
 * Created on Jun 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.slice.consensus;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.assembly.slice.Slice;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public abstract class AbstractTestConsensusCaller {

    private ConsensusCaller consensusCaller;
   
    @Before
    public void setup(){
        consensusCaller = getConsensusCaller();
    }
    
    protected abstract ConsensusCaller getConsensusCaller();
    
    public List<ConsensusResult> computeConsensus(List<Slice> slices){
        List<ConsensusResult> consensus = new ArrayList<ConsensusResult>();
        for(Slice slice : slices){
            consensus.add(consensusCaller.callConsensus(slice));
        }
        return consensus;
    }
    
    

    
    private List<Slice> slices;
    private  List<ConsensusResult> expectedConsensus;
    
    public AbstractTestConsensusCaller(List<Slice> slices, List<ConsensusResult> expectedConsensus){
        this.slices = slices;
        this.expectedConsensus = expectedConsensus;
    }
    @Test
    public void assertConsensusIsCorrect(){
        assertEquals(expectedConsensus, computeConsensus(slices));
    }
}
