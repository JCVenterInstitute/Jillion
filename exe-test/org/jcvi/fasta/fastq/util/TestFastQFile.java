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

package org.jcvi.fasta.fastq.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.fastq.DefaultFastQFileDataStore;
import org.jcvi.common.core.seq.fastx.fastq.FastQDataStore;
import org.jcvi.common.core.seq.fastx.fastq.FastQQualityCodec;
import org.jcvi.common.core.seq.fastx.fastq.FastQRecord;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.jcvi.common.io.idReader.IdReaderException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestFastQFile {

    private final ResourceFileServer RESOURCES = new ResourceFileServer(TestFastQFile.class);
    String id = "SOLEXA1:4:1:12:1692#0/1";
    String otherId = "SOLEXA1:4:1:12:1489#0/1";
    File ids;
    File outputFile;
    File fastQFile;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Before
    public void setup() throws IOException{
        outputFile = folder.newFile("outputFile.fastq");
        ids =folder.newFile("ids.lst");
        PrintWriter writer = new PrintWriter(ids);
        writer.println(id);
        writer.close();
        fastQFile = RESOURCES.getFile("files/example.fastq");
    }
    
   
    @Test
    public void includeOnlyIdsThatAreSpecified() throws IOException, IdReaderException, DataStoreException{
        
        FastQDataStore<FastQRecord> originalDataStore = new DefaultFastQFileDataStore(fastQFile, FastQQualityCodec.ILLUMINA);
        FastQFile.main(new String[]{"-i",ids.getAbsolutePath(),
                "-o", outputFile.getAbsolutePath(),
                fastQFile.getAbsolutePath()});
        FastQDataStore<FastQRecord> filteredDataStore = new DefaultFastQFileDataStore(outputFile, FastQQualityCodec.ILLUMINA);
        assertEquals(1, filteredDataStore.size());
        assertFalse(filteredDataStore.contains(otherId));
        assertEquals(originalDataStore.get(id),filteredDataStore.get(id));
    }
    @Test
    public void excludeIdsThatAreSpecified() throws IOException, IdReaderException, DataStoreException{
        File fastQFile = RESOURCES.getFile("files/example.fastq");
        FastQDataStore<FastQRecord> originalDataStore = new DefaultFastQFileDataStore(fastQFile, FastQQualityCodec.ILLUMINA);
        FastQFile.main(new String[]{"-e",ids.getAbsolutePath(),
                "-o", outputFile.getAbsolutePath(),
                fastQFile.getAbsolutePath()});
        
        FastQDataStore<FastQRecord> filteredDataStore = new DefaultFastQFileDataStore(outputFile, FastQQualityCodec.ILLUMINA);
        assertEquals(1, filteredDataStore.size());
        assertFalse(filteredDataStore.contains(id));
        
        assertEquals(originalDataStore.get(otherId),filteredDataStore.get(otherId));
    }
}
