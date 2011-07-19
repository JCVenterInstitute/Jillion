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

package org.jcvi.common.core.assembly.contig.ace.consed;

import org.jcvi.Range;
import org.jcvi.assembly.AssemblyUtil;
import org.jcvi.common.core.assembly.contig.Contig;
import org.jcvi.common.core.assembly.contig.PlacedRead;
import org.jcvi.common.core.assembly.contig.ace.consed.ConsensusNavigationElement;
import org.jcvi.common.core.seq.nuc.DefaultNucleotideSequence;
import org.jcvi.common.core.seq.nuc.NucleotideSequence;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestConsensusNavigationElementFactoryMethod {
    Range gappedFeatureRange = Range.buildRange(5,10);
    NucleotideSequence consensus = new DefaultNucleotideSequence("ACGT-ACGTACGTACGT-ACGT");
    Range ungappedFeatureRange= AssemblyUtil.convertGappedRangeIntoUngappedRange(consensus, gappedFeatureRange);
    
    Contig<PlacedRead> mockContig;
    String id = "contigId";
    @Before
    public void setup(){
        mockContig = createMock(Contig.class);
        expect(mockContig.getConsensus()).andStubReturn(consensus);
        expect(mockContig.getId()).andStubReturn(id);
        replay(mockContig);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullContigShouldThrowNPE(){
        ConsensusNavigationElement.buildConsensusNavigationElement(null, gappedFeatureRange);
    }
    
    @Test(expected = NullPointerException.class)
    public void nullRangeShouldthrowNPE(){
        ConsensusNavigationElement.buildConsensusNavigationElement(mockContig, null);
    }
    @Test
    public void noComment(){
        ConsensusNavigationElement expected = new ConsensusNavigationElement(id, ungappedFeatureRange);
        ConsensusNavigationElement actual = ConsensusNavigationElement.buildConsensusNavigationElement(mockContig, gappedFeatureRange);
        
        assertEquals(expected, actual);
    }
    @Test
    public void comment(){
        String comment = "this is a comment";
        ConsensusNavigationElement expected = new ConsensusNavigationElement(id, ungappedFeatureRange,comment);
        ConsensusNavigationElement actual = ConsensusNavigationElement.buildConsensusNavigationElement(mockContig, gappedFeatureRange,comment);
        
        assertEquals(expected, actual);
    }
    
}
