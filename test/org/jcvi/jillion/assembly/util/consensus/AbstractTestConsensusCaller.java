/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jun 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.consensus;

import java.util.ArrayList;
import java.util.List;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.consensus.ConsensusCaller;
import org.jcvi.jillion.assembly.util.consensus.ConsensusResult;
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
