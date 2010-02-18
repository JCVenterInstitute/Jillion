/*
 * Created on Aug 9, 2007
 *
 * @author dkatzel
 */
package org.jcvi;


import static org.junit.Assert.*;

import org.junit.Test;
public class TestEmptyRange{

    private Range emptyRange = Range.buildEmptyRange();
    private Range nonEmptyRange = Range.buildRange(5,5);


    @Test
    public void testSize(){
        assertEquals(0,emptyRange.size());
    }
    @Test
    public void testIsEmpty(){
        assertTrue(emptyRange.isEmpty());
    }
    @Test
    public void testIsSubRangeOf(){
        assertFalse(emptyRange.isSubRangeOf(nonEmptyRange));
    }
    @Test
    public void testEndsBefore(){
        assertFalse(emptyRange.endsBefore(nonEmptyRange));
    }
    @Test
    public void testIntersects(){
        assertFalse(emptyRange.intersects(nonEmptyRange));
    }


    @Test
    public void testIntersection(){
        assertSame(emptyRange,emptyRange.intersection(nonEmptyRange));
    }
    @Test
    public void testStartsBefore(){
        assertFalse(emptyRange.startsBefore(nonEmptyRange));
    }
    @Test
    public void testUnion(){
        Range[] unionRanges = emptyRange.union(nonEmptyRange);
        assertEquals(1,unionRanges.length);
        assertSame(nonEmptyRange, unionRanges[0]);
    }
}
