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

package org.jcvi.fastX.fasta.seq;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.fastX.IncludeFastXIdFilter;
import org.jcvi.fastX.fasta.qual.TestQualityFastaH2DataStore;
import org.jcvi.fastX.fasta.seq.LargeNucleotideFastaFileDataStore;
import org.jcvi.fastX.fasta.seq.NucleotideFastaDataStore;
import org.jcvi.fastX.fasta.seq.NucleotideFastaH2DataStore;
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
public class TestFilteredNucleotideFastaH2DataStore {

    private static final String FASTA_FILE_PATH = "files/19150.fasta";
    private static final ResourceFileServer RESOURCES = new ResourceFileServer(TestFilteredNucleotideFastaH2DataStore.class);
   
    
    private H2NucleotideDataStore h2NucleotideDataStore;
    private NucleotideFastaH2DataStore sut;
    private NucleotideFastaDataStore expectedDataStore;
    private IncludeFastXIdFilter filter= new IncludeFastXIdFilter(Arrays.asList("1", "2", "9"));
    @Before
    public void setup() throws DataStoreException, FileNotFoundException, IOException{
        h2NucleotideDataStore = new H2NucleotideDataStore();
        final File fastaFile = RESOURCES.getFile(FASTA_FILE_PATH);
        expectedDataStore = new LargeNucleotideFastaFileDataStore(fastaFile);
        sut = new NucleotideFastaH2DataStore(fastaFile, h2NucleotideDataStore,filter);
    }
    
    @After
    public void tearDown() throws IOException{
        h2NucleotideDataStore.close();
    }
    @Test
    public void size() throws DataStoreException{
        assertEquals(3, sut.size());
    }
    @Test
    public void _1() throws DataStoreException{
        String id= "1";
        assertEquals(expectedDataStore.get(id).getValue().decode(), sut.get(id).decode());
    }
    @Test
    public void _2() throws DataStoreException{
        String id= "2";
        assertEquals(expectedDataStore.get(id).getValue().decode(), sut.get(id).decode());
    }
    @Test
    public void _6HasBeenFiltered() throws DataStoreException{
        assertFalse(sut.contains("6"));
    }
    @Test
    public void _9() throws DataStoreException{
        String id= "9";
        assertEquals(expectedDataStore.get(id).getValue().decode(), sut.get(id).decode());
    }
}
