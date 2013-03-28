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
 * Created on Jul 14, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.archive;

import java.io.IOException;
import java.util.Arrays;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.IteratorUtil;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.archive.CachedTraceArchiveDataStore;
import org.jcvi.jillion.trace.archive.TraceArchiveDataStore;
import org.jcvi.jillion.trace.archive.TraceArchiveTrace;
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
        long numTraces =100;
        expect(mockTraceArchive.getNumberOfRecords()).andReturn(numTraces);
        replay(mockTraceArchive);
        assertEquals(numTraces, sut.getNumberOfRecords());
        verify(mockTraceArchive);
    }
    @Test
    public void numberOfTracesThrowsTraceDecoderException() throws DataStoreException{
        expect(mockTraceArchive.getNumberOfRecords()).andThrow(expectedDataStoreException);
        replay(mockTraceArchive);
        try{
            sut.getNumberOfRecords();
            fail("should throw expectedTraceDecoderException");
        }catch( DataStoreException e){
            assertEquals(expectedDataStoreException, e);
        }
        verify(mockTraceArchive);
    }
    
    @Test
    public void iterator() throws DataStoreException{
        StreamingIterator<String> expectedIterator = IteratorUtil.createStreamingIterator(
                        Arrays.asList("one","two","three").iterator());
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
