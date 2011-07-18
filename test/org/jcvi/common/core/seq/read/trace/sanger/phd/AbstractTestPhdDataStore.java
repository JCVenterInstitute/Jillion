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

package org.jcvi.common.core.seq.read.trace.sanger.phd;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.common.core.seq.read.trace.sanger.phd.Phd;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractTestPhdDataStore extends AbstractTestPhd{

    protected abstract PhdDataStore createPhdDataStore(File phdfile) throws FileNotFoundException;
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
        assertEquals(expectedQualities, actual.getQualities().decode());        
        assertEquals(expectedPositions, actual.getPeaks().getData().decode());      
        assertEquals(expectedBasecalls, NucleotideGlyph.convertToString(actual.getBasecalls().decode()));
        assertEquals(expectedProperties, actual.getComments());
    }
    
    @Test
    public void size() throws DataStoreException{
        assertEquals(1, sut.size());
    }

    @Test
    public void idIterator() throws DataStoreException{
        Iterator<String> iter = sut.getIds();
        assertTrue(iter.hasNext());
        assertEquals("1095595674585", iter.next());
        assertFalse(iter.hasNext());
    }
    @Test
    public void iterator(){
        Iterator<Phd> iter = sut.iterator();
        assertTrue(iter.hasNext());
        Phd actual = iter.next();
        phdRecordMatchesExpected(actual);
        assertFalse(iter.hasNext());
    }
    
    @Test
    public void getAfterCloseShouldThrowIllegalStateException() throws IOException, DataStoreException{
        sut.close();
        try {
            sut.get("1095595674585");
            fail("get should throw exception after close()");
        } catch (IllegalStateException expected) {
        }
    }
}
