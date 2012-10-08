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
 * Created on Mar 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core;


import java.util.Iterator;

import org.jcvi.common.core.Range;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestRangeIterator {

    Range range = Range.of(1, 10);
    Iterator<Long> sut;
    @Before
    public void setup(){
        sut = range.iterator();
    }
    @Test
    public void iterate(){
        for(long i= range.getBegin(); i<=range.getEnd(); i++){
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
    /**
     * All longs are <= Long.MAX_VALUE
     * if the iteration code is naively 
     * written it will infinite loop if end is max_value.
     * See Java Puzzlers Puzzle# 26 for more details.
     */
    @Test
    public void testEndIsInMax(){
    	Range r = Range.of(Long.MAX_VALUE-10, Long.MAX_VALUE);
    	Iterator<Long> sut = r.iterator();
    	for(int i =0; i< 11; i++){    		
            assertEquals(Long.MAX_VALUE-10+i, sut.next().longValue());
        }
        assertFalse(sut.hasNext());
    }
    @Test
    public void testEndIsMaxEmptyRange(){
    	Range r = new Range.Builder()
    				.shift(Long.MAX_VALUE)
    				.build();
    	Iterator<Long> sut = r.iterator();
    	assertFalse(sut.hasNext());
    }
}
