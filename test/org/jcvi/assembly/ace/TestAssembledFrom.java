/*
 * Created on Feb 18, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import org.jcvi.TestUtil;
import org.jcvi.sequence.SequenceDirection;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestAssembledFrom {

    SequenceDirection dir = SequenceDirection.FORWARD;
    String id = "assembled from id";
    int offset = 12345;
    
    AssembledFrom sut = new AssembledFrom(id, offset, dir);
    
    @Test
    public void constructor(){
        assertEquals(id, sut.getId());
        assertEquals(offset, sut.getStartOffset());
        assertEquals(dir, sut.getSequenceDirection());
    }
    @Test
    public void nullIdShouldThrowIllegalArgumentException(){
        try{
            new AssembledFrom(null, offset, dir);
            fail("should throw IllegalArgumentException when id is null");
        }catch(IllegalArgumentException e){
            assertEquals("id can not be null", e.getMessage());
        }
    }
    @Test
    public void equalsSameRef(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void notEqualsNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void differentClassNotEquals(){
        assertFalse(sut.equals("not an AssembledFrom"));
    }
    
    @Test
    public void equalsSameValues(){
        AssembledFrom sameValues = new AssembledFrom(id, offset, dir);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    
    @Test
    public void differentIdShouldNotBeEqual(){
        AssembledFrom differentId = new AssembledFrom("different"+id, offset, dir);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentId);
    }
    /**
     * Only id is used to calculate equality.
     */
    @Test
    public void differentOffsetShouldStillBeEqual(){
        AssembledFrom differentOffset = new AssembledFrom(id, offset+1, dir);
        TestUtil.assertEqualAndHashcodeSame(sut, differentOffset);
    }
    /**
     * Only id is used to calculate equality.
     */
    @Test
    public void differentComlimentShouldStillBeEqual(){
        AssembledFrom differentCompliment = new AssembledFrom(id, offset, SequenceDirection.REVERSE);
        TestUtil.assertEqualAndHashcodeSame(sut, differentCompliment);
    }
    
    @Test
    public void testToString(){
        String expected = id + " " + offset + "is complimented? "+(dir ==SequenceDirection.REVERSE);
        assertEquals(expected, sut.toString());
    }
}
