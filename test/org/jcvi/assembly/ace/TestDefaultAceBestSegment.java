/*
 * Created on Oct 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import org.jcvi.Range;
import org.jcvi.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestDefaultAceBestSegment {

    String name = "name";
    Range range = Range.buildRange(1,10);
    DefaultAceBestSegment sut = new DefaultAceBestSegment(name, range);
    
    @Test(expected = NullPointerException.class)
    public void nullNameShouldThrowNPE(){
        new DefaultAceBestSegment(null, range);
    }
    @Test(expected = NullPointerException.class)
    public void nullRangeShouldThrowNPE(){
        new DefaultAceBestSegment(name,null);
    }
    
    @Test
    public void constructor(){
        assertEquals(name, sut.getReadName());
        assertEquals(range, sut.getGappedConsensusRange());
    }
    @Test
    public void differentClassNotEqual(){
        assertFalse(sut.equals("not a best segment"));
    }
    @Test
    public void notEqualToNull(){
        assertFalse(sut.equals(null));
    }
    @Test
    public void sameRefIsEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    
    @Test
    public void sameValuesIsEqual(){
        DefaultAceBestSegment sameValues = new DefaultAceBestSegment(name, range);
        TestUtil.assertEqualAndHashcodeSame(sut, sameValues);
    }
    @Test
    public void differentNameIsNotEqual(){
        DefaultAceBestSegment differentName = new DefaultAceBestSegment("different"+name, range);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentName);
    }
    
    @Test
    public void differentRangeIsNotEqual(){
        DefaultAceBestSegment differentRange = new DefaultAceBestSegment(name, range.shiftLeft(1));
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, differentRange);
    }
}
