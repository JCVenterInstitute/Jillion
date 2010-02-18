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
 * Created on Jul 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.traceArchive;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.trace.TraceDecoderException;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestCachedTraceArchiveMultiTrace {

    TraceArchiveDataStore mockTraceArchive;
    CachedTraceArchiveDataStore sut;
    String id = "id";
    DataStoreException expectedDataStoreException = new DataStoreException("expected");
    @Before
    public void setup(){
        mockTraceArchive = createMock(TraceArchiveDataStore.class);
        sut = new CachedTraceArchiveDataStore(mockTraceArchive, 3);        
    }
    
    @Test
    public void contains() throws DataStoreException{
        expect(mockTraceArchive.contains(id)).andReturn(true);
        replay(mockTraceArchive);
        assertTrue(sut.contains(id));
        verify(mockTraceArchive);
    }
    @Test
    public void containsThrowsTraceDecoderException() throws DataStoreException{
        expect(mockTraceArchive.contains(id)).andThrow(expectedDataStoreException);
        replay(mockTraceArchive);
        try{
            sut.contains(id);
            fail("should throw expectedTraceDecoderException");
        }catch( DataStoreException e){
            assertEquals(expectedDataStoreException, e);
        }
        verify(mockTraceArchive);
    }
    
    @Test
    public void numberOfTraces() throws DataStoreException{
        int numTraces =100;
        expect(mockTraceArchive.size()).andReturn(numTraces);
        replay(mockTraceArchive);
        assertEquals(numTraces, sut.size());
        verify(mockTraceArchive);
    }
    @Test
    public void numberOfTracesThrowsTraceDecoderException() throws DataStoreException{
        expect(mockTraceArchive.size()).andThrow(expectedDataStoreException);
        replay(mockTraceArchive);
        try{
            sut.size();
            fail("should throw expectedTraceDecoderException");
        }catch( DataStoreException e){
            assertEquals(expectedDataStoreException, e);
        }
        verify(mockTraceArchive);
    }
    
    @Test
    public void iterator(){
        Iterator<String> expectedIterator = Arrays.asList("one","two","three").iterator();
        expect(mockTraceArchive.iterator()).andReturn(expectedIterator);
        replay(mockTraceArchive);
        assertSame(expectedIterator, sut.iterator());
        verify(mockTraceArchive);
    }
    
    @Test
    public void close() throws IOException{
        mockTraceArchive.close();
        replay(mockTraceArchive);
        sut.close();
        verify(mockTraceArchive);
        
    }
    @Test
    public void closethrowsIOException() throws IOException{
        IOException expected = new IOException("expected");
        mockTraceArchive.close();
        expectLastCall().andThrow(expected);
        replay(mockTraceArchive);
        try {
            sut.close();
            fail("should throw IOException");
        } catch (IOException e) {
           assertEquals(expected, e);
        }
        verify(mockTraceArchive);
        
    }
    
    @Test
    public void getTrace() throws DataStoreException{
        TraceArchiveTrace trace = createMock(TraceArchiveTrace.class);
        expect(mockTraceArchive.get(id)).andReturn(trace);
        replay(mockTraceArchive);
        assertEquals(trace, sut.get(id));
        verify(mockTraceArchive);
    }
    @Test
    public void getTraceThrowsTraceDecoderException() throws DataStoreException {
        expect(mockTraceArchive.get(id)).andThrow(expectedDataStoreException);
        replay(mockTraceArchive);
        try {
            sut.get(id);
            fail("should throw TraceDecoderException");
        } catch (DataStoreException e) {
            assertEquals(expectedDataStoreException, e);
        }
        verify(mockTraceArchive);
    }
    @Test
    public void getTraceAlreadyInCache() throws DataStoreException{
        TraceArchiveTrace trace = createMock(TraceArchiveTrace.class);
        expect(mockTraceArchive.get(id)).andReturn(trace).once();
        replay(mockTraceArchive);
        assertEquals(trace, sut.get(id));
        assertEquals(trace, sut.get(id));
        verify(mockTraceArchive);
    }
}
