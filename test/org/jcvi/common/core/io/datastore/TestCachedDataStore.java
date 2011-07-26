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
 * Created on Nov 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.io.datastore;

import java.io.IOException;

import org.jcvi.common.core.datastore.Cacheable;
import org.jcvi.common.core.datastore.CachedDataStore;
import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

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
        cache = CachedDataStore.createCachedDataStore(DataStoreSubInterface.class, delegate, 2);
        
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
        assertTrue(cache instanceof Cacheable);
        expect(delegate.get(id_1)).andReturn(value1).times(2);
        replay(delegate);
        assertEquals(value1,cache.get(id_1));
        assertEquals(value1,cache.get(id_1));
        CachedDataStore.clearCacheFrom(cache);
        assertEquals(value1,cache.get(id_1));
        verify(delegate);
    }
    
    interface DataStoreSubInterface extends DataStore<Long>{
        
    }
    
}
