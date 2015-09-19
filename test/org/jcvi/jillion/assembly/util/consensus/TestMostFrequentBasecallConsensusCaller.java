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
package org.jcvi.jillion.assembly.util.consensus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.jcvi.jillion.assembly.util.Slice;
import org.jcvi.jillion.assembly.util.consensus.ConsensusCaller;
import org.jcvi.jillion.assembly.util.consensus.ConsensusResult;
import org.jcvi.jillion.assembly.util.consensus.MostFrequentBasecallConsensusCaller;
import org.junit.runners.Parameterized.Parameters;

public class TestMostFrequentBasecallConsensusCaller extends AbstractTestConsensusCaller{

	public TestMostFrequentBasecallConsensusCaller(List<Slice> slices,
			List<ConsensusResult> expectedConsensus) {
		super(slices, expectedConsensus);
	}

	@Override
	protected ConsensusCaller getConsensusCaller() {
		return MostFrequentBasecallConsensusCaller.INSTANCE;
	}
	@Parameters
    public static Collection<?> data(){
       List<Object[]> data = new ArrayList<Object[]>();
       for(Entry<List<Slice>, List<ConsensusResult>> entry: ConsensusCallerTestUtil.generateMostCommonBasecallData().entrySet()){
           data.add(new Object[]{entry.getKey(), entry.getValue()});
       }
        
        return data;
        
    }
}
