/*
 * Created on Nov 5, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.IOException;

import org.jcvi.trace.TraceDataStore;
import org.jcvi.trace.sanger.SangerTrace;
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
    public void getCached() throws DataStoreException{        
        expect(delegate.get(id_1)).andReturn(trace1);
        replay(delegate);
        assertEquals(trace1,cache.get(id_1));
        assertEquals(trace1,cache.get(id_1));
        verify(delegate);
    }
    @Test
    public void get() throws DataStoreException{        
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
    public void kickedOutOfCache() throws DataStoreException{        
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
    public void close() throws DataStoreException, IOException{
        expect(delegate.get(id_1)).andReturn(trace1).times(2);
        delegate.close();
        replay(delegate);
        assertEquals(trace1,cache.get(id_1));
        cache.close();
        assertEquals(trace1,cache.get(id_1));
        verify(delegate);
    }
}
