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
package org.jcvi.jillion.assembly.consed.phd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jcvi.jillion.assembly.consed.phd.Phd;
import org.jcvi.jillion.assembly.consed.phd.PhdDataStore;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractTestPhdDataStore extends AbstractTestPhd{

    protected abstract PhdDataStore createPhdDataStore(File phdfile) throws IOException;
    PhdDataStore sut;
    @Before
    public void createPhdDataStore() throws IOException{
        sut = createPhdDataStore(RESOURCE.getFile(PHD_FILE));
    }
    
    @Test
    public void get() throws DataStoreException{
       
        Phd actual = sut.get("1095595674585");
        phdRecordMatchesExpected(actual);
    }

    protected void phdRecordMatchesExpected(Phd actual) {
        assertEquals(expectedQualities, actual.getQualitySequence());        
        assertEquals(expectedPositions, actual.getPositionSequence());      
        assertEquals(expectedBasecalls, actual.getNucleotideSequence().toString());
        assertEquals(expectedProperties, actual.getComments());
    }
    
    @Test
    public void size() throws DataStoreException{
        assertEquals(1, sut.getNumberOfRecords());
    }

    @Test
    public void idIterator() throws DataStoreException{
        Iterator<String> iter = sut.idIterator();
        assertTrue(iter.hasNext());
        assertEquals("1095595674585", iter.next());
        assertFalse(iter.hasNext());
    }
    @Test
    public void iterator() throws DataStoreException{
        Iterator<Phd> iter = sut.iterator();
        assertTrue(iter.hasNext());
        Phd actual = iter.next();
        phdRecordMatchesExpected(actual);
        assertFalse(iter.hasNext());
        try{
            iter.next();
            fail("should throw nosuchElementException when done iterating");
        }catch(NoSuchElementException expected){
            //expected
        }
    }
    
    @Test
    public void getAfterCloseShouldIllegalStateException() throws IOException, DataStoreException{
        sut.close();
        try {
            sut.get("1095595674585");
            fail("get should throw exception after close()");
        } catch (IllegalStateException expected) {
        }
    }
}
