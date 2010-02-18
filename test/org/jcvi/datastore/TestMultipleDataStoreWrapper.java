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
 * Created on Dec 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
public class TestMultipleDataStoreWrapper {

    DataStore<String> datastore1, datastore2;
    
    DataStore<String> sut;
    String id = "id";
    DataStoreException datastoreException = new DataStoreException("expected exception");
    @Before
    public void setup(){
        datastore1 = createMock(DataStore.class);
        datastore2 = createMock(DataStore.class);
        sut = MultipleDataStoreWrapper.createMultipleDataStoreWrapper(DataStore.class,datastore1, datastore2);
    }
    
    @Test
    public void voidMethodShouldGetCalledForAll() throws IOException{
        datastore1.close();
        datastore2.close();
        replay(datastore1, datastore2);
        sut.close();
        verify(datastore1, datastore2);
    }
    @Test
    public void intMethodShouldSumTotal() throws DataStoreException{
        expect(datastore1.size()).andReturn(100);
        expect(datastore2.size()).andReturn(50);
        replay(datastore1, datastore2);
        assertEquals(150, sut.size());
        verify(datastore1, datastore2);
    }
    @Test
    public void booleanShouldReturnFirstTrue_lastIsTrue() throws DataStoreException{
        expect(datastore1.contains(id)).andReturn(false);
        expect(datastore2.contains(id)).andReturn(true);
        replay(datastore1, datastore2);
        assertTrue(sut.contains(id));
        verify(datastore1, datastore2);
    }
    @Test
    public void booleanShouldReturnFirstTrue_FirstIsTrue() throws DataStoreException{
        expect(datastore1.contains(id)).andReturn(true);
        replay(datastore1, datastore2);
        assertTrue(sut.contains(id));
        verify(datastore1, datastore2);
    }
    @Test
    public void booleanShouldReturnFirstTrue_NonAreTrue() throws DataStoreException{
        expect(datastore1.contains(id)).andReturn(false);
        expect(datastore2.contains(id)).andReturn(false);
        replay(datastore1, datastore2);
        assertFalse(sut.contains(id));
        verify(datastore1, datastore2);
    }
    
    @Test
    public void iteratorShouldIterateOverAll(){
        Iterator<String> iter1 = Arrays.asList("one","two").iterator();
        Iterator<String> iter2 = Arrays.asList("three","four").iterator();
        
        Iterator<String> expectedIterator = Arrays.asList("one","two","three","four").iterator();
        expect(datastore1.iterator()).andReturn(iter1);
        expect(datastore2.iterator()).andReturn(iter2);
        replay(datastore1, datastore2);
        Iterator<String> actualIterator = sut.iterator();
        while(expectedIterator.hasNext()){
            assertEquals(expectedIterator.next(), actualIterator.next());
        }
        assertFalse(actualIterator.hasNext());
        verify(datastore1, datastore2);
    }
    @Test
    public void getShouldGetFirstValidFirstThowsExceptionShouldIgnore() throws DataStoreException{
        expect(datastore1.get(id)).andThrow(datastoreException);
        expect(datastore2.get(id)).andReturn(id);
        replay(datastore1, datastore2);
        assertEquals(id, sut.get(id));
        verify(datastore1, datastore2);
    }
    @Test
    public void getShouldGetFirstValidFirstFirstHasIt() throws DataStoreException{
        expect(datastore1.get(id)).andReturn(id);
        replay(datastore1, datastore2);
        assertEquals(id, sut.get(id));
        verify(datastore1, datastore2);
    }
    @Test
    public void getShouldGetFirstValidAllThowsExceptionShouldReturnNull() throws DataStoreException{
        expect(datastore1.get(id)).andThrow(datastoreException);
        expect(datastore2.get(id)).andThrow(datastoreException);
        replay(datastore1, datastore2);
        assertNull(id, sut.get(id));
        verify(datastore1, datastore2);
    }
}
