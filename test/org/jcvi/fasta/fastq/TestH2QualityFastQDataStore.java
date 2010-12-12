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

package org.jcvi.fasta.fastq;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.jcvi.datastore.DataStoreException;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.datastore.H2QualityDataStore;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestH2QualityFastQDataStore {
    static final FastQQualityCodec QUALITY_CODEC = FastQQualityCodec.ILLUMINA;
    
    String filepath = "files/example.fastq";
    ResourceFileServer resources = new ResourceFileServer(
            TestDefaultFastQFileDataStore.class);
    
    private H2QualityFastQDataStore sut;
    
    @Before
    public void setup() throws IOException, DataStoreException{
        File fastqFile = resources.getFile(filepath);
        final H2QualityDataStore datastore = new H2QualityDataStore();
        sut = createSUT(fastqFile, datastore);
    }
    protected H2QualityFastQDataStore createSUT(File fastQFile, H2QualityDataStore datastore ) throws FileNotFoundException{
        return new H2QualityFastQDataStore(fastQFile, QUALITY_CODEC,datastore);
    }
    
    
    protected H2QualityFastQDataStore getSut() {
        return sut;
    }
    
    @Test
    public void get() throws DataStoreException{
        List<PhredQuality> expected =QUALITY_CODEC
        .decode("abaab]_]aaa`bbabB`Wb__aa\\_]W]a`^[`\\T`aZa_aa`WXa``]_`[`^a^^[`^][a^Raaa\\V\\OQ]aYQ^aa^\\`GRTDP`^T^Lb^aR`S")
        .decode();
        
        assertEquals(expected, sut.get("SOLEXA1:4:1:12:1489#0/1").decode());

    }
}
