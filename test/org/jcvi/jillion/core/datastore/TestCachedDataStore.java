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
 * Created on Nov 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.datastore;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.junit.Before;
import org.junit.Test;

public class TestCachedDataStore {

    DataStoreSubInterface delegate;
    DataStoreSubInterface cache;
    Long value1 = Long.valueOf(1);
    Long value2 = Long.valueOf(2);
    Long value3 = Long.valueOf(3);

    String id_1 = "id_1";
    String id_2 = "id_2";
    String id_3 = "id_3";
    @Before
    public void setup(){
        delegate = createMock(DataStoreSubInterface.class);
        cache = DataStoreUtil.createNewCachedDataStore(DataStoreSubInterface.class, delegate, 2);
        
    }
    
    @Test
    public void getInCacheShouldGetFromCached() throws DataStoreException{        
        expect(delegate.get(id_1)).andReturn(value1);
        replay(delegate);
        assertEquals(value1,cache.get(id_1));
        assertEquals(value1,cache.get(id_1));
        verify(delegate);
    }
    @Test
    public void getNotInCacheShouldGetFromDelegate() throws DataStoreException{        
        expect(delegate.get(id_1)).andReturn(value1);
        expect(delegate.get(id_2)).andReturn(value2);
        expect(delegate.get(id_3)).andReturn(value3);
        replay(delegate);
        assertEquals(value1,cache.get(id_1));
        assertEquals(value2,cache.get(id_2));
        assertEquals(value3,cache.get(id_3));
        verify(delegate);
    }
    @Test
    public void tooManyGetsCausesLRUToKickedLeastRecentlyUsedFromCache() throws DataStoreException{        
        expect(delegate.get(id_1)).andReturn(value1).times(2);
        expect(delegate.get(id_2)).andReturn(value2);
        expect(delegate.get(id_3)).andReturn(value3);
        replay(delegate);
        assertEquals(value1,cache.get(id_1));
        assertEquals(value2,cache.get(id_2));
        assertEquals(value3,cache.get(id_3));
        assertEquals(value1,cache.get(id_1));
        verify(delegate);
    }
    
    @Test
    public void closeShouldCloseDelegateAndClearCache() throws DataStoreException, IOException{
        expect(delegate.get(id_1)).andReturn(value1).times(2);
        delegate.close();
        replay(delegate);
        assertEquals(value1,cache.get(id_1));
        cache.close();
        assertEquals(value1,cache.get(id_1));
        verify(delegate);
    }
    
    @Test
    public void clearCacheEarly() throws DataStoreException{
        assertTrue(DataStoreUtil.isACachedDataStore(cache));
        expect(delegate.get(id_1)).andReturn(value1).times(2);
        replay(delegate);
        assertEquals(value1,cache.get(id_1));
        assertEquals(value1,cache.get(id_1));
        DataStoreUtil.clearCacheFrom(cache);
        assertEquals(value1,cache.get(id_1));
        verify(delegate);
    }
    
    interface DataStoreSubInterface extends DataStore<Long>{
        
    }
    
}
