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
 * Created on Aug 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.common.core.util.ArrayIterable;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestArrayIterable {

    String[] stooges = new String[]{"Larry", "Curly", "Moe","Shemp", "Joe Besser","Curly-Joe"};
    
    @Test
    public void iterator(){
        ArrayIterable<String> sut = new ArrayIterable<String>(stooges);
        Iterator<String> iter = sut.iterator();
        int counter =0;
        while(iter.hasNext()){
            String actual = iter.next();
            assertEquals(stooges[counter++], actual);
        }
    }
    @Test
    public void throwsNoSuchElementExceptionWhenHasNextIsFalse(){
        ArrayIterable<String> sut = new ArrayIterable<String>(stooges);
        Iterator<String> iter = sut.iterator();
        while(iter.hasNext()){
            iter.next();
        }
        
        try{
            iter.next();
            fail("should throw NoSuchElementException when has next =false");
        }catch(NoSuchElementException e){
            
        }
    }
    @Test
    public void emptyArray(){
        Iterator iter = new ArrayIterable(new Object[]{}).iterator();
        assertFalse(iter.hasNext());
    }
    
    @Test(expected= NullPointerException.class)
    public void nullArrayShouldThrowNullPointerException(){
        new ArrayIterable(null);
    }
}
