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
package org.jcvi.datastore;

import java.io.IOException;

import org.jcvi.common.core.seq.read.trace.TraceDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.SangerTrace;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class TestCachedDataStore {

    TraceDataStore<SangerTrace> delegate;
    TraceDataStore<SangerTrace> cache;
    SangerTrace trace1 = createMock(SangerTrace.class);
    SangerTrace trace2 = createMock(SangerTrace.class);
    SangerTrace trace3 = createMock(SangerTrace.class);
    String id_1 = "id_1";
    String id_2 = "id_2";
    String id_3 = "id_3";
    @Before
    public void setup(){
        delegate = createMock(TraceDataStore.class);
       // cache = CachedDataStore.<SangerTrace,TraceDataStore>createCachedDataStore(TraceDataStore.class, delegate, 2);
        cache = CachedDataStore.createCachedDataStore(TraceDataStore.class, delegate, 2);
        
    }
    
    @Test
    public void getInCacheShouldGetFromCached() throws DataStoreException{        
        expect(delegate.get(id_1)).andReturn(trace1);
        replay(delegate);
        assertEquals(trace1,cache.get(id_1));
        assertEquals(trace1,cache.get(id_1));
        verify(delegate);
    }
    @Test
    public void getNotInCacheShouldGetFromDelegate() throws DataStoreException{        
        expect(delegate.get(id_1)).andReturn(trace1);
        expect(delegate.get(id_2)).andReturn(trace2);
        expect(delegate.get(id_3)).andReturn(trace3);
        replay(delegate);
        assertEquals(trace1,cache.get(id_1));
        assertEquals(trace2,cache.get(id_2));
        assertEquals(trace3,cache.get(id_3));
        verify(delegate);
    }
    @Test
    public void tooManyGetsCausesLRUToKickedLeastRecentlyUsedFromCache() throws DataStoreException{        
        expect(delegate.get(id_1)).andReturn(trace1).times(2);
        expect(delegate.get(id_2)).andReturn(trace2);
        expect(delegate.get(id_3)).andReturn(trace3);
        replay(delegate);
        assertEquals(trace1,cache.get(id_1));
        assertEquals(trace2,cache.get(id_2));
        assertEquals(trace3,cache.get(id_3));
        assertEquals(trace1,cache.get(id_1));
        verify(delegate);
    }
    
    @Test
    public void closeShouldCloseDelegateAndClearCache() throws DataStoreException, IOException{
        expect(delegate.get(id_1)).andReturn(trace1).times(2);
        delegate.close();
        replay(delegate);
        assertEquals(trace1,cache.get(id_1));
        cache.close();
        assertEquals(trace1,cache.get(id_1));
        verify(delegate);
    }
    
    @Test
    public void clearCacheEarly() throws DataStoreException{
        assertTrue(cache instanceof Cacheable);
        expect(delegate.get(id_1)).andReturn(trace1).times(2);
        replay(delegate);
        assertEquals(trace1,cache.get(id_1));
        assertEquals(trace1,cache.get(id_1));
        CachedDataStore.clearCacheFrom(cache);
        assertEquals(trace1,cache.get(id_1));
        verify(delegate);
    }
    
}
