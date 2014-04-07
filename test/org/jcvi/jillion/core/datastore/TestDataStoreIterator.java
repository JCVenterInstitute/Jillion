/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * Created on Aug 12, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.datastore;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.internal.core.datastore.DataStoreIterator;
import org.junit.Before;
import org.junit.Test;
public class TestDataStoreIterator {
    private DataStore<Object> mockDataStore;
    private StreamingIterator<String> mockIterator;
    
    private DataStoreIterator<Object> sut;
    @Before
    public void setup() throws DataStoreException{
        mockDataStore = createMock(DataStore.class);
        mockIterator = createMock(StreamingIterator.class);
        expect(mockDataStore.idIterator()).andReturn(mockIterator);
        replay(mockDataStore);
        sut = new DataStoreIterator<Object>(mockDataStore);
        reset(mockDataStore);
    }
    @Test
    public void constructorFails() throws DataStoreException{
        DataStoreException expectedException = new DataStoreException("expected");
        expect(mockDataStore.idIterator()).andThrow(expectedException);
        replay(mockDataStore);
        try{
            new DataStoreIterator<Object>(mockDataStore);
            fail("should throw IllegalStateException");
        }catch(IllegalStateException e){
            assertEquals("could not iterate over ids", e.getMessage());
            assertEquals(expectedException, e.getCause());
        }
        verify(mockDataStore);
    }
    @Test
    public void hasNext(){
        expect(mockIterator.hasNext()).andReturn(true);
        replay(mockDataStore,mockIterator);
        assertTrue(sut.hasNext());
        verify(mockDataStore,mockIterator);
    }
    
    @Test
    public void doesNotHaveNext(){
        expect(mockIterator.hasNext()).andReturn(false);
        replay(mockDataStore,mockIterator);
        assertFalse(sut.hasNext());
        verify(mockDataStore,mockIterator);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void removeThrowsUnsupportedOpException(){
        sut.remove();
    }
    @Test
    public void next() throws DataStoreException{
        String nextId = "nextId";
        Object expectedObj = new Object();
        expect(mockIterator.next()).andReturn(nextId);
        expect(mockDataStore.get(nextId)).andReturn(expectedObj);
        replay(mockDataStore,mockIterator);       
        assertSame(expectedObj, sut.next());
           
        verify(mockDataStore,mockIterator);
    }
    @Test
    public void nextThrowsDataStoreExceptionShouldThrowIllegalStateException() throws DataStoreException, IOException{
        DataStoreException expectedException = new DataStoreException("expected");
        String nextId = "nextId";
        expect(mockIterator.next()).andReturn(nextId);
        expect(mockDataStore.get(nextId)).andThrow(expectedException);
        //exception should close iterator
        mockIterator.close();
        replay(mockDataStore,mockIterator);       
        try{
            sut.next();
            fail("should throw DataStoreException");
        }catch(IllegalStateException e){
            assertEquals("could not get next element", e.getMessage());
            assertEquals(expectedException, e.getCause());
        }
           
        verify(mockDataStore,mockIterator);
    }
    @Test
    public void nextThrowsNoSuchElementException(){
        NoSuchElementException expectedException = new NoSuchElementException("expected");
        expect(mockIterator.next()).andThrow(expectedException);
        replay(mockDataStore,mockIterator);
        try{
            sut.next();
            fail("should throw NoSuchelementException");
        }catch(NoSuchElementException e){
            assertEquals(expectedException, e);
        }
        verify(mockDataStore,mockIterator);
    }
}
