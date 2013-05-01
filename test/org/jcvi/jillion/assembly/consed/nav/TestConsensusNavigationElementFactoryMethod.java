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
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.consed.nav;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.AssemblyUtil;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.assembly.consed.nav.ConsensusNavigationElement;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
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
    Range gappedFeatureRange = Range.of(5,10);
    NucleotideSequence consensus = new NucleotideSequenceBuilder("ACGT-ACGTACGTACGT-ACGT").build();
    Range ungappedFeatureRange= AssemblyUtil.toUngappedRange(consensus, gappedFeatureRange);
    
    Contig<AssembledRead> mockContig;
    String id = "contigId";
    @Before
    public void setup(){
        mockContig = createMock(Contig.class);
        expect(mockContig.getConsensusSequence()).andStubReturn(consensus);
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
