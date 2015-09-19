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
