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

package org.jcvi.fastX.fastq;

import java.io.File;
import java.io.IOException;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.fastX.fastq.H2NucleotideFastQDataStore;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.datastore.H2NucleotideDataStore;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestH2NucleotideFastQDataStore {

    String filepath = "files/example.fastq";
    ResourceFileServer resources = new ResourceFileServer(
            TestDefaultFastQFileDataStore.class);
    
    private H2NucleotideFastQDataStore sut;
    
    @Before
    public void setup() throws IOException, DataStoreException{
        File fastqFile = resources.getFile(filepath);
        final H2NucleotideDataStore datastore = new H2NucleotideDataStore();
        sut = createSUT(fastqFile, datastore);
    }
    protected H2NucleotideFastQDataStore createSUT(File fastQFile, H2NucleotideDataStore datastore ) throws IOException{
        return new H2NucleotideFastQDataStore(fastQFile, datastore);
    }
    
    
    protected H2NucleotideFastQDataStore getSut() {
        return sut;
    }
    @Test
    public void get() throws DataStoreException{
        assertEquals("TATTTAAAATCTAATANGTCTTGATTTGAAATTGAAAGAGCAAAAATCTGATTGATTTTATTGAAGAATAATTTGATTTAATATATTCTTAAGTCTGTTT",
                NucleotideGlyph.convertToString(sut.get("SOLEXA1:4:1:12:1489#0/1").decode()));
    }
    
    
}
