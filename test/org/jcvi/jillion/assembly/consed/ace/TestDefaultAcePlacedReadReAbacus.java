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
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultAcePlacedReadReAbacus {
    String readId = "readId";
    Range validRange= Range.of(15,25);
    int ungappedFullLength =30;
    Direction dir = Direction.FORWARD;
    PhdInfo phdInfo = new PhdInfo("traceName","phdName",new Date());
    
    int originalStartOffset=5;
    NucleotideSequence consensus = new NucleotideSequenceBuilder("NNNNACGTTACGTTT").build();
    NucleotideSequence originalSequence =   new NucleotideSequenceBuilder("ACGT-ACG-T").build();
    AceAssembledReadBuilder sut = DefaultAceAssembledRead.createBuilder(
            readId, originalSequence, 
            originalStartOffset, 
            dir, validRange, phdInfo, ungappedFullLength,null);

    @Before
    public void createBuilder(){
        sut = DefaultAceAssembledRead.createBuilder(
                readId, originalSequence, 
                originalStartOffset, 
                dir, validRange, phdInfo, ungappedFullLength, null);
    }
    @Test
    public void confirmInitialValues(){
        assertEquals(readId, sut.getId());
        assertEquals(originalStartOffset, sut.getBegin());
        assertEquals(dir, sut.getDirection());
        assertEquals(phdInfo, sut.getPhdInfo());
        assertEquals(ungappedFullLength, sut.getUngappedFullLength());
        assertEquals(originalSequence, sut.getCurrentNucleotideSequence());
        assertEquals(validRange, sut.getClearRange());
    }
    
    @Test
    public void shiftBases(){
        sut.setStartOffset(originalStartOffset+5);
        assertEquals(originalStartOffset+5, sut.getBegin());
        
        assertEquals(readId, sut.getId());
        assertEquals(dir, sut.getDirection());
        assertEquals(phdInfo, sut.getPhdInfo());
        assertEquals(ungappedFullLength, sut.getUngappedFullLength());
        assertEquals(originalSequence, sut.getCurrentNucleotideSequence());
        assertEquals(validRange, sut.getClearRange());
    }
    
    @Test
    public void reAbacus(){
        sut.reAbacus(Range.of(3,9), parse("TACGT"));
        
        assertEquals(readId, sut.getId());       
        assertEquals(8, sut.getLength());
        assertEquals(originalStartOffset+7, sut.getEnd());
        assertEquals("ACGTACGT", sut.getCurrentNucleotideSequence().toString());
        assertEquals(validRange, sut.getClearRange());
        
    }
    @Test
    public void reAbacusDifferentNonGapBasesShouldThrowException(){
        try{
            sut.reAbacus(Range.of(3,9), parse("TRCGT"));
            fail("should throw Exception");
        }catch(IllegalArgumentException expected){
            assertEquals("reAbacusing must retain same ungapped basecalls! 'TACGT' vs 'TRCGT'",
                    expected.getMessage());
        }
    }
    
    static NucleotideSequence parse(String nucleotides){
        return new NucleotideSequenceBuilder(nucleotides).build();
        
    }
}
