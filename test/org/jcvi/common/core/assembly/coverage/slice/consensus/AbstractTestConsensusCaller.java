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
 * Created on Jun 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.coverage.slice.consensus;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.common.core.assembly.util.slice.Slice;
import org.jcvi.common.core.assembly.util.slice.consensus.ConsensusCaller;
import org.jcvi.common.core.assembly.util.slice.consensus.ConsensusResult;
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
