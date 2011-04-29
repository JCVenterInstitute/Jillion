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
        assertTrue(emptyRange.startsBefore(nonEmptyRange));
    }
    @Test
    public void testUnion(){
        Range[] unionRanges = emptyRange.union(nonEmptyRange);
        assertEquals(1,unionRanges.length);
        assertSame(nonEmptyRange, unionRanges[0]);
    }
}
