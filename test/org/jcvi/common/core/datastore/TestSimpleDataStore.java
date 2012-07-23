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
 * Created on Dec 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.datastore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.common.core.datastore.DataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.MapDataStoreAdapter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestSimpleDataStore {

    private static final Map<String, Integer> MAP = new HashMap<String, Integer>();
    
    static{
        MAP.put("key1", 1);
        MAP.put("key2", 2);
    }
    private DataStore<Integer> sut;
    @Before
    public void setup(){
        sut = MapDataStoreAdapter.adapt(MAP);
    }
    
    @Test
    public void contains() throws DataStoreException{
        assertTrue(sut.contains("key1"));
    }
    @Test
    public void get() throws DataStoreException{
        assertEquals(Integer.valueOf(1), sut.get("key1"));
    }
    @Test
    public void size() throws DataStoreException{
        assertEquals(2, sut.getNumberOfRecords());
    }
    @Test
    public void gettingAfterCloseShouldThrowIllegalStateException() throws IOException, DataStoreException{
        sut.close();
        try{
            sut.get("key1");
            fail("should throw dataStoreException after closing");
        }catch(IllegalStateException e){
            //pass
        }
    }
    @Test
    public void getIds() throws DataStoreException{
        Iterator<String> expectedIter = MAP.keySet().iterator();
        Iterator<String> actualIter = sut.idIterator();
        while(actualIter.hasNext()){
            assertEquals(expectedIter.next(), actualIter.next());
        }
        assertFalse(expectedIter.hasNext());
    }
    @Test
    public void iterator() throws DataStoreException{
        Iterator<String> idIter = MAP.keySet().iterator();
        Iterator<Integer> actualIter = sut.iterator();
        while(actualIter.hasNext()){
            assertEquals(MAP.get(idIter.next()), actualIter.next());
        }
        assertFalse(idIter.hasNext());
    }
}
