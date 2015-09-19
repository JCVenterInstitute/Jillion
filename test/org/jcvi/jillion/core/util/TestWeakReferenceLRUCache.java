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
package org.jcvi.jillion.core.util;

import java.util.Collections;
import java.util.Map;

import org.jcvi.jillion.internal.core.util.Caches;
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
