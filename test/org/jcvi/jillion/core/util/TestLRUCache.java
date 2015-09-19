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
