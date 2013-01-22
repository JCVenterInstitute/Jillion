/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jun 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.util.slice.consensus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.jcvi.jillion.assembly.util.slice.IdedSlice;
import org.jcvi.jillion.assembly.util.slice.Slice;
import org.jcvi.jillion.assembly.util.slice.consensus.ConicConsensusCaller;
import org.jcvi.jillion.assembly.util.slice.consensus.ConsensusCaller;
import org.jcvi.jillion.assembly.util.slice.consensus.ConsensusResult;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.junit.runners.Parameterized.Parameters;
public class TestConicConsensusCaller extends AbstractTestConsensusCaller{
   
    public TestConicConsensusCaller(List<Slice<?>> slices,
            List<ConsensusResult> expectedConsensus) {
        super(slices, expectedConsensus);
    }
    
    @Parameters
    public static Collection<?> data(){
       List<Object[]> data = new ArrayList<Object[]>();
       for(Entry<List<IdedSlice>, List<ConsensusResult>> entry: ConsensusCallerTestUtil.generateConicData().entrySet()){
           data.add(new Object[]{entry.getKey(), entry.getValue()});
       }
        
        return data;
        
    }
   

    @Override
    protected ConsensusCaller getConsensusCaller() {
        return new ConicConsensusCaller(PhredQuality.valueOf(30));
    }

}
