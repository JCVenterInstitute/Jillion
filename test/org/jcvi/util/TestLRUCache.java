/*
 * Created on Apr 7, 2009
 *
 * @author dkatzel
 */
package org.jcvi.util;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestLRUCache {

    String first = "first";
    String second = "second";
    String third = "third";
    String fourth = "fourth";
    
    LRUCache<String, String> sut;
    @Before
    public void setup(){
        sut = new LRUCache<String, String>(3);
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
    }
    @Test
    public void remove(){
        sut.remove(third);
        sut.put(fourth, fourth);
        assertTrue(sut.containsKey(first));
        assertTrue(sut.containsKey(second));
        assertTrue(sut.containsKey(fourth));
    }
}
