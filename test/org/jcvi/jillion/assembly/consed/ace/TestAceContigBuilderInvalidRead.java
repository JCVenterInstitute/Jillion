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
 * Created on Jun 19, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.consed.ace;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;

import org.jcvi.jillion.assembly.consed.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.consed.ace.PhdInfo;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Before;
import org.junit.Test;
public class TestAceContigBuilderInvalidRead {

    private final String consensus = "ACGT";
    private final String contigId = "id";
    private AceContigBuilder sut;
    @Before
    public void setup(){
        sut = new AceContigBuilder(contigId, consensus);
    }
    
    @Test(expected= IllegalArgumentException.class)
    public void readThatGoesOffTheReferenceShouldThrowException(){
        String readId = "readId";
        int offset =1;
        String validBases = consensus;
        
        Range clearRange = new Range.Builder(validBases.length()).build();
        PhdInfo phdInfo = createMock(PhdInfo.class);
        addReadToBuilder(readId, validBases, offset, Direction.FORWARD, clearRange, phdInfo);
        assertEquals(sut.numberOfReads(),0);
    }
    
    private void addReadToBuilder(String id,String validBases,int offset,Direction dir, Range validRange, PhdInfo phdInfo){
    	sut.addRead(id, new NucleotideSequenceBuilder(validBases).build(), offset, dir, 
    			validRange, phdInfo,validBases.length());
        
    }
}
