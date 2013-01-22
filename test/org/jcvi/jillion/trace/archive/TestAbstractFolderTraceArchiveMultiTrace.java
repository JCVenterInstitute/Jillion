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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Jul 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.archive;

import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.trace.archive.AbstractFolderTraceArchiveDataStore;
import org.jcvi.jillion.trace.archive.TraceArchiveInfo;
import org.jcvi.jillion.trace.archive.TraceArchiveTrace;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestAbstractFolderTraceArchiveMultiTrace {

    private static class FolderTraceArchiveMultiTraceTestDouble extends AbstractFolderTraceArchiveDataStore{

        public FolderTraceArchiveMultiTraceTestDouble(String rootDirPath,
                TraceArchiveInfo traceArchiveInfo) {
            super(rootDirPath, traceArchiveInfo);
        }

        @Override
        public TraceArchiveTrace get(String id)
                throws DataStoreException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void close() throws IOException {
            // TODO Auto-generated method stub
            
        }

        @Override
        protected TraceArchiveTrace createTraceArchiveTrace(String id)
                throws DataStoreException {
            // TODO Auto-generated method stub
            return null;
        }
        
    }
    
    FolderTraceArchiveMultiTraceTestDouble sut;
    TraceArchiveInfo mockTraceArchiveInfo;
    String rootDir = "rootDir";
    DataStoreException expectedDataStoreException = new DataStoreException("expected");
    
    @Before
    public void setup(){
        mockTraceArchiveInfo = createMock(TraceArchiveInfo.class);
        sut = new FolderTraceArchiveMultiTraceTestDouble(rootDir,mockTraceArchiveInfo);
    }
    @Test
    public void constructor(){
        assertSame(rootDir, sut.getRootDirPath());
        assertSame(mockTraceArchiveInfo, sut.getTraceArchiveInfo());
    }
    @Test(expected = NullPointerException.class)
    public void nullRootFolderShouldThrowNPE(){
        new FolderTraceArchiveMultiTraceTestDouble(null,mockTraceArchiveInfo);
    }
    @Test(expected = NullPointerException.class)
    public void nullTraceInfoShouldThrowNPE(){
        new FolderTraceArchiveMultiTraceTestDouble(rootDir,null);
    }
    
    @Test
    public void contains() throws DataStoreException{
        String hasId = "HasId";
        String doesntHaveId = "DoesntHaveId";
        expect(mockTraceArchiveInfo.contains(hasId)).andReturn(true);
        expect(mockTraceArchiveInfo.contains(doesntHaveId)).andReturn(false);
        
        replay(mockTraceArchiveInfo);
        assertTrue(sut.contains(hasId));
        assertFalse(sut.contains(doesntHaveId));
        verify(mockTraceArchiveInfo);
    }
    
    @Test
    public void containsThrowsDataStoreExceptionShouldWrapInTraceDecoderException() throws DataStoreException{
        String errorId = "this id will throw a DataStoreException";
        expect(mockTraceArchiveInfo.contains(errorId)).andThrow(expectedDataStoreException);
        
        replay(mockTraceArchiveInfo);
        try {
            sut.contains(errorId);
            fail("should throw TraceDecoderException on error");
        } catch (DataStoreException e) {
            assertEquals(expectedDataStoreException.getMessage(), e.getMessage());
        }

        verify(mockTraceArchiveInfo);
    }
    
    @Test
    public void getNumberOfTraces() throws DataStoreException{
        long size = 1234;
        expect(mockTraceArchiveInfo.getNumberOfRecords()).andReturn(size);
        replay(mockTraceArchiveInfo);
        assertEquals(size, sut.getNumberOfRecords());
        verify(mockTraceArchiveInfo);
    }
    @Test
    public void exceptionThrownOnGetNumberOfTraces() throws DataStoreException{
        expect(mockTraceArchiveInfo.getNumberOfRecords()).andThrow(expectedDataStoreException);
        replay(mockTraceArchiveInfo);
        try {
            sut.getNumberOfRecords();
            fail("should throw TraceDecoderException on error");
        } catch (DataStoreException e) {
            assertEquals(expectedDataStoreException.getMessage(), e.getMessage());
        }

        verify(mockTraceArchiveInfo);
        verify(mockTraceArchiveInfo);
    }
    
    
}
