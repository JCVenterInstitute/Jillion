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
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.trace.sff;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.iter.StreamingIterator;
import org.jcvi.jillion.trace.TraceDecoderException;
import org.jcvi.jillion.trace.sff.Flowgram;
import org.jcvi.jillion.trace.sff.FlowgramDataStore;
import org.junit.Test;

import static org.junit.Assert.*;
public abstract class AbstractTestSffFileDataStore extends TestReadExampleSffFile{  

    private FlowgramDataStore dataStore;
    @Override
    protected void parseSff(File file) throws Exception{
        dataStore = parseDataStore(file);
    }
    
    protected abstract FlowgramDataStore parseDataStore(File f) throws Exception;
    
    @Override
    protected Flowgram getFlowgram(String id) throws TraceDecoderException, DataStoreException {
        return dataStore.get(id);
    }
    @Override
    protected long getNumberOfFlowgrams() throws TraceDecoderException, DataStoreException {
        return dataStore.getNumberOfRecords();
    }
    
    @Test
    public void contains() throws DataStoreException{
        assertTrue(dataStore.contains("FF585OX02HCMO2"));
        assertTrue(dataStore.contains("FF585OX02HCD8G"));
        assertTrue(dataStore.contains("FF585OX02FNE4N"));
        assertTrue(dataStore.contains("FF585OX02GMGGN"));
        assertTrue(dataStore.contains("FF585OX02FHO5X"));
        assertFalse(dataStore.contains("notAnId"));
    }
    
    @Test
    public void iterator() throws DataStoreException{
        Iterator<Flowgram> iter = dataStore.iterator();
        assertTrue(iter.hasNext());
        boolean foundFF585OX02HCMO2 =false;
        boolean foundFF585OX02HCD8G =false;
        boolean foundFF585OX02FNE4N =false;
        boolean foundFF585OX02GMGGN =false;
        boolean foundFF585OX02FHO5X =false;
        while(iter.hasNext()){
        	Flowgram flow =iter.next();
            if(!foundFF585OX02HCMO2 && FF585OX02HCMO2.equals(flow)){
                foundFF585OX02HCMO2=true;
            }else if(!foundFF585OX02HCD8G && FF585OX02HCD8G.equals(flow)){
                foundFF585OX02HCD8G=true;
            }else if(!foundFF585OX02FNE4N && FF585OX02FNE4N.equals(flow)){
                foundFF585OX02FNE4N=true;
            }else if(!foundFF585OX02GMGGN && FF585OX02GMGGN.equals(flow)){
                foundFF585OX02GMGGN=true;
            }else if(!foundFF585OX02FHO5X ){
                foundFF585OX02FHO5X = FF585OX02FHO5X.equals(flow);
            }
        }
        assertTrue(foundFF585OX02HCMO2);
        assertTrue(foundFF585OX02HCD8G);
        assertTrue(foundFF585OX02FNE4N);
        assertTrue(foundFF585OX02GMGGN);
        assertTrue(foundFF585OX02FHO5X);
    }
    
    @Test
    public void getIds() throws DataStoreException{
        Iterator<String> iter = dataStore.idIterator();
        assertTrue(iter.hasNext());
        boolean foundFF585OX02HCMO2 =false;
        boolean foundFF585OX02HCD8G =false;
        boolean foundFF585OX02FNE4N =false;
        boolean foundFF585OX02GMGGN =false;
        boolean foundFF585OX02FHO5X =false;
        while(iter.hasNext()){
        	String id =iter.next();
            if(!foundFF585OX02HCMO2 && "FF585OX02HCMO2".equals(id)){
                foundFF585OX02HCMO2=true;
            }else if(!foundFF585OX02HCD8G && "FF585OX02HCD8G".equals(id)){
                foundFF585OX02HCD8G=true;
            }else if(!foundFF585OX02FNE4N && "FF585OX02FNE4N".equals(id)){
                foundFF585OX02FNE4N=true;
            }else if(!foundFF585OX02GMGGN && "FF585OX02GMGGN".equals(id)){
                foundFF585OX02GMGGN=true;
            }else if(!foundFF585OX02FHO5X ){
                foundFF585OX02FHO5X = "FF585OX02FHO5X".equals(id);
            }
        }
        assertTrue(foundFF585OX02HCMO2);
        assertTrue(foundFF585OX02HCD8G);
        assertTrue(foundFF585OX02FNE4N);
        assertTrue(foundFF585OX02GMGGN);
        assertTrue(foundFF585OX02FHO5X);
    }
    
    @Test
    public void closeIteratorEarly() throws IOException, DataStoreException{
        StreamingIterator<Flowgram> iter = dataStore.iterator();
        assertTrue(iter.hasNext());
        iter.next();
        iter.close();
        assertFalse(iter.hasNext());
        try{
	        iter.next();
	        fail("should throw no such element exception");
        }catch(NoSuchElementException expected){
        	
        }
    }

}
