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

package org.jcvi.fasta;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.nuc.datastore.H2NucleotideDataStore;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestNucleotideFastaH2DataStore {
    private static final String FASTA_FILE_PATH = "files/19150.fasta";
    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestQualityFastaH2DataStore.class);
   
    
    private H2NucleotideDataStore h2NucleotideDataStore;
    private NucleotideFastaH2DataStore sut;
    private NucleotideFastaDataStore expectedDataStore;
    @Before
    public void setup() throws DataStoreException, FileNotFoundException, IOException{
        h2NucleotideDataStore = new H2NucleotideDataStore();
        final File fastaFile = RESOURCES.getFile(FASTA_FILE_PATH);
        expectedDataStore = new LargeNucleotideFastaFileDataStore(fastaFile);
        sut = new NucleotideFastaH2DataStore(fastaFile, h2NucleotideDataStore);
    }
    
    @After
    public void tearDown() throws IOException{
        h2NucleotideDataStore.close();
    }
    
    @Test
    public void _1() throws DataStoreException{
        String id= "1";
        assertEquals(expectedDataStore.get(id).getValues().decode(), sut.get(id).decode());
    }
    @Test
    public void _2() throws DataStoreException{
        String id= "2";
        assertEquals(expectedDataStore.get(id).getValues().decode(), sut.get(id).decode());
    }
    @Test
    public void _6() throws DataStoreException{
        String id= "6";
        assertEquals(expectedDataStore.get(id).getValues().decode(), sut.get(id).decode());
    }
    @Test
    public void _9() throws DataStoreException{
        String id= "9";
        assertEquals(expectedDataStore.get(id).getValues().decode(), sut.get(id).decode());
    }
}
