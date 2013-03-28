/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Apr 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.util;

import java.util.Map;

import org.jcvi.jillion.internal.core.util.Caches;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestLRUCache {

    String first = "first";
    String second = "second";
    String third = "third";
    String fourth = "fourth";
    
    protected Map<String, String> sut;
    
    protected Map<String,String> createLRUCache(int size){
        return Caches.createLRUCache(size);
    }
    @Before
    public void setup(){
        sut = createLRUCache(3);
        sut.put(first, first);
        sut.put(second, second);
        sut.put(third, third);
    }
    @Test
    public void initalState(){
        assertEquals(sut.size(), 3);
        assertTrue(sut.containsKey(first));
        assertTrue(sut.containsKey(second));
        assertTrue(sut.containsKey(third));
        
    }
    @Test
    public void insertOverflowShouldRemoveEldest(){
        sut.put(fourth, fourth);
        assertEquals(sut.size(), 3);
        assertTrue(sut.containsKey(second));
        assertTrue(sut.containsKey(third));
        assertTrue(sut.containsKey(fourth));
        
        assertEquals(second,sut.get(second));
        assertEquals(third,sut.get(third));
        assertEquals(fourth,sut.get(fourth));
    }
    @Test
    public void remove(){
        sut.remove(third);
        sut.put(fourth, fourth);
        assertTrue(sut.containsKey(first));
        assertTrue(sut.containsKey(second));
        assertTrue(sut.containsKey(fourth));
        
        assertEquals(first,sut.get(first));
        assertEquals(second,sut.get(second));
        assertEquals(fourth,sut.get(fourth));
    }
}
