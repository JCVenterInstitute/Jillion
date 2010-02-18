/*
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi;


import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestRangeIterator {

    Range range = Range.buildRange(1, 10);
    RangeIterator sut;
    @Before
    public void setup(){
        sut = new RangeIterator(range);
    }
    @Test
    public void iterate(){
        for(long i= range.getStart(); i<=range.getEnd(); i++){
            assertTrue(sut.hasNext());
            assertEquals(Long.valueOf(i), sut.next());
        }
        assertFalse(sut.hasNext());
    }
    
    @Test
    public void removeShouldthrowUnsupportedOperationException(){
        
        try{
            sut.remove();
            fail("should throw Unsupported operation exception");
        }catch(UnsupportedOperationException e){
            assertEquals("can not remove from Range", e.getMessage());
        }
    }
    
    @Test
    public void iteratorOverRange(){
        for(Long i : range){
            assertEquals(i, sut.next());
        }
        assertFalse(sut.hasNext());
    }
}
