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

package org.jcvi.fastX.fasta.qual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.fastX.fasta.qual.LargeQualityFastaFileDataStore;
import org.jcvi.fastX.fasta.qual.QualityFastaDataStore;
import org.jcvi.fastX.fasta.qual.QualityFastaH2DataStore;
import org.jcvi.glyph.phredQuality.datastore.H2QualityDataStore;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestQualityFastaH2DataStore {
    private static final String QUAL_FILE_PATH = "files/19150.qual";
    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestQualityFastaH2DataStore.class);
   
    
    private H2QualityDataStore h2QualityDataStore;
    private QualityFastaH2DataStore sut;
    private QualityFastaDataStore expectedDataStore;
    @Before
    public void setup() throws DataStoreException, FileNotFoundException, IOException{
        h2QualityDataStore = new H2QualityDataStore();
        final File qualFile = RESOURCES.getFile(QUAL_FILE_PATH);
        expectedDataStore = new LargeQualityFastaFileDataStore(qualFile);
        sut = new QualityFastaH2DataStore(qualFile, h2QualityDataStore);
    }
    
    @After
    public void tearDown() throws IOException{
        h2QualityDataStore.close();
    }
    
    @Test
    public void JGBAA02T21A12PB1A1F() throws DataStoreException{
        String id= "JGBAA02T21A12PB1A1F";
        assertEquals(expectedDataStore.get(id).getValue().decode(), sut.get(id).decode());
    }
    @Test
    public void JGBAA05T21C11NP1BF() throws DataStoreException{
        String id= "JGBAA05T21C11NP1BF";
        assertEquals(expectedDataStore.get(id).getValue().decode(), sut.get(id).decode());
    }
    @Test
    public void JGBAA06T21G05NA1128RB() throws DataStoreException{
        String id= "JGBAA06T21G05NA1128RB";
        assertEquals(expectedDataStore.get(id).getValue().decode(), sut.get(id).decode());
    }
    @Test
    public void JGBAA01T21H05PB2A2341BRB() throws DataStoreException{
        String id= "JGBAA01T21H05PB2A2341BRB";
        assertEquals(expectedDataStore.get(id).getValue().decode(), sut.get(id).decode());
    }
}
