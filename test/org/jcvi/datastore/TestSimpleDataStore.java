/*
 * Created on Dec 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.datastore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
        sut = new SimpleDataStore<Integer>(MAP);
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
        assertEquals(2, sut.size());
    }
    @Test
    public void close() throws IOException{
        sut.close();
        try{
            sut.get("key1");
            fail("should throw dataStoreException after closing");
        }catch(DataStoreException e){
            //pass
        }
    }
    @Test
    public void getIds() throws DataStoreException{
        Iterator<String> expectedIter = MAP.keySet().iterator();
        Iterator<String> actualIter = sut.getIds();
        while(actualIter.hasNext()){
            assertEquals(expectedIter.next(), actualIter.next());
        }
        assertFalse(expectedIter.hasNext());
    }
    @Test
    public void iterator(){
        Iterator<String> idIter = MAP.keySet().iterator();
        Iterator<Integer> actualIter = sut.iterator();
        while(actualIter.hasNext()){
            assertEquals(MAP.get(idIter.next()), actualIter.next());
        }
        assertFalse(idIter.hasNext());
    }
}
