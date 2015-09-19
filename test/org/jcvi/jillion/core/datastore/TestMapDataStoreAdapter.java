/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Dec 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.core.datastore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jcvi.jillion.core.datastore.DataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.datastore.DataStoreUtil;
import org.junit.Before;
import org.junit.Test;
public class TestMapDataStoreAdapter {

    private static final Map<String, Integer> MAP = new HashMap<String, Integer>();
    
    static{
        MAP.put("key1", 1);
        MAP.put("key2", 2);
    }
    private DataStore<Integer> sut;
    @Before
    public void setup(){
        sut = DataStoreUtil.adapt(MAP);
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
