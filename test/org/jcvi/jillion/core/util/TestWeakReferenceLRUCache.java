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

package org.jcvi.jillion.core.util;

import java.util.Collections;
import java.util.Map;

import org.jcvi.jillion.core.internal.util.Caches;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestWeakReferenceLRUCache {

    @Test
    public void valueThatIsWeaklyReachableGetsRemoved() throws InterruptedException{
        
        Map<String, Object> weakCache = Caches.createWeakReferencedValueLRUCache();
        weakCache.put("test", createObject());
        assertEquals(1, weakCache.size());
        assertEquals(Collections.singleton("test"),weakCache.keySet());
        assertTrue(weakCache.containsKey("test"));
        System.gc();
        //need to wait for gc to do stuff
        Thread.sleep(500);
        assertEquals(0,weakCache.size());
    }
    
    @Test
    public void removesLeastRecentlyUsedStrongReference() throws InterruptedException{
        Map<String, Object> weakCache = Caches.createWeakReferencedValueLRUCache(2);
        weakCache.put("test1", createObject());
        weakCache.put("test2", createObject());
        weakCache.put("test3", createObject());
        
        assertEquals(2, weakCache.size());
        assertTrue(weakCache.containsKey("test2"));
        assertTrue(weakCache.containsKey("test3"));
        System.gc();
        //need to wait for gc to do stuff
        Thread.sleep(500);
        assertEquals(0,weakCache.size());
        
    }
    
    private Object createObject(){
        return new Object();
    }
}
