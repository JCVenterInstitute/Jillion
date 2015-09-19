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
 * Created on Jan 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.nt.ReferenceMappedNucleotideSequence;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.jcvi.jillion.internal.assembly.DefaultAssembledRead;
import org.junit.Before;
import org.junit.Test;
public class TestDefaultPlacedRead {

    /**
     * 
     */
    private static final int ungappedLength = 500;
    ReferenceMappedNucleotideSequence sequence;
    Direction dir = Direction.FORWARD;
    long start = 100;
    long length = 200L;
    Range validRange = Range.of(start, length);
    DefaultAssembledRead sut ;
    		String id = "id";
    @Before
    public void setup(){
        sequence = createMock(ReferenceMappedNucleotideSequence.class);
        expect(sequence.getLength()).andStubReturn(length);
        replay(sequence);
        sut = new DefaultAssembledRead(id,sequence, start,dir,ungappedLength,validRange);
    
    }
    @Test
    public void constructor(){
    	
    	
        assertEquals(dir,sut.getDirection());
        assertEquals(start, sut.getGappedStartOffset());
        assertEquals(id, sut.getId());
        assertEquals(sequence, sut.getNucleotideSequence());
        assertEquals(length, sut.getGappedLength());
        assertEquals(start+ length-1 , sut.getGappedEndOffset());
        assertEquals(validRange, sut.getReadInfo().getValidRange());
        assertEquals(start+5, sut.toReferenceOffset(5));
        assertEquals(5, sut.toGappedValidRangeOffset(start+5));
        verify(sequence);        
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void toGappedValidRangeGivenOffsetPastEndOfReadShouldThrowException(){
    	sut.toGappedValidRangeOffset(start+length);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void toGappedValidRangeGivenOffsetBeforeStartOfReadShouldThrowException(){
    	sut.toGappedValidRangeOffset(start-1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void toReferenceOffsetGivenOffsetPastEndOfReadShouldThrowException(){
    	sut.toReferenceOffset(length);
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void toReferenceOffsetGivenOffsetIsNegativeShouldThrowException(){
    	sut.toReferenceOffset(-1);
    }
    
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void notEqualsDifferentClass(){
        assertFalse(sut.equals("not a DefaultPlacedRead"));
    }
    @Test
    public void sameRefIsEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    
    @Test
    public void sameValuesAreEqual(){
        AssembledRead sameValues =  new DefaultAssembledRead(id, sequence, start,dir,500,validRange);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentReadIsNotEqual(){
        ReferenceMappedNucleotideSequence differentSequence = createMock(ReferenceMappedNucleotideSequence.class);
        AssembledRead hasDifferentRead =  new DefaultAssembledRead(id, differentSequence, start,dir,500,validRange);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentRead);
    }
    @Test
    public void differentIdIsNotEqual(){
         AssembledRead hasDifferentRead =  new DefaultAssembledRead("different"+id, sequence, start,dir,500,validRange);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentRead);
    }
    @Test
    public void differentStartIsNotEqual(){
        AssembledRead hasDifferentStart =  new DefaultAssembledRead(id,sequence, start-1,dir,500,validRange);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, hasDifferentStart);
    }
    
    
}
