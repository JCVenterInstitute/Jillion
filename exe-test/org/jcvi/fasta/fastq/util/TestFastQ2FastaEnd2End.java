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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.DefaultQualityFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualityFastaDataStore;
import org.jcvi.common.core.seq.fastx.fastq.DefaultFastQFileDataStore;
import org.jcvi.common.core.seq.fastx.fastq.FastQDataStore;
import org.jcvi.common.core.seq.fastx.fastq.FastQQualityCodec;
import org.jcvi.common.core.seq.fastx.fastq.FastQRecord;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.jcvi.io.idReader.IdReaderException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author dkatzel
 *
 *
 */
public class TestFastQ2FastaEnd2End {

     private final ResourceFileServer RESOURCES = new ResourceFileServer(TestFastQ2FastaEnd2End.class);
     
     String id = "SOLEXA1:4:1:12:1692#0/1";
     String otherId = "SOLEXA1:4:1:12:1489#0/1";
     File ids;
     File seqOutputFile;
     File qualOutputFile;
     File fastQFile;
     @Rule
     public TemporaryFolder folder = new TemporaryFolder();
     
     @Before
     public void setup() throws IOException{
         seqOutputFile = folder.newFile("outputFile.fasta");
         qualOutputFile = folder.newFile("outputFile.qual");
         ids =folder.newFile("ids.lst");
         PrintWriter writer = new PrintWriter(ids);
         writer.println(id);
         writer.close();
         fastQFile = RESOURCES.getFile("files/example.fastq");
     }
     @Test
     public void ifNoFiltersThenIncludeAllIds() throws IOException, IdReaderException, DataStoreException{
         
         FastQDataStore<FastQRecord> originalDataStore = new DefaultFastQFileDataStore(fastQFile, FastQQualityCodec.ILLUMINA);
         Fastq2Fasta.main(new String[]{
                 "-s", seqOutputFile.getAbsolutePath(),
                 "-q", qualOutputFile.getAbsolutePath(),
                 fastQFile.getAbsolutePath()});
         NucleotideFastaDataStore filteredSeqDataStore = new DefaultNucleotideFastaFileDataStore(seqOutputFile);
         QualityFastaDataStore filteredQualityDataStore = new DefaultQualityFastaFileDataStore(qualOutputFile);
         
         assertEquals(2, filteredSeqDataStore.size());
         assertEquals(2, filteredQualityDataStore.size());
         assertEquals(originalDataStore.get(id).getNucleotides().decode(),filteredSeqDataStore.get(id).getValue().decode());
         assertEquals(originalDataStore.get(id).getQualities().decode(),filteredQualityDataStore.get(id).getValue().decode());
         
         assertEquals(originalDataStore.get(otherId).getNucleotides().decode(),filteredSeqDataStore.get(otherId).getValue().decode());
         assertEquals(originalDataStore.get(otherId).getQualities().decode(),filteredQualityDataStore.get(otherId).getValue().decode());
       
     }
     @Test
     public void supportSangerEncodedFastQWithDashSangerOption() throws IOException, IdReaderException, DataStoreException{
        File sangerFastQFile = RESOURCES.getFile("files/sanger.fastq");
        FastQDataStore<FastQRecord> originalDataStore = new DefaultFastQFileDataStore(sangerFastQFile, FastQQualityCodec.SANGER);
        Fastq2Fasta.main(new String[]{
                "-s", seqOutputFile.getAbsolutePath(),
                "-q", qualOutputFile.getAbsolutePath(),
                "-sanger",
                sangerFastQFile.getAbsolutePath()});
        NucleotideFastaDataStore filteredSeqDataStore = new DefaultNucleotideFastaFileDataStore(seqOutputFile);
        QualityFastaDataStore filteredQualityDataStore = new DefaultQualityFastaFileDataStore(qualOutputFile);
        
        assertEquals(2, filteredSeqDataStore.size());
        assertEquals(2, filteredQualityDataStore.size());
        assertEquals(originalDataStore.get(id).getNucleotides().decode(),filteredSeqDataStore.get(id).getValue().decode());
        assertEquals(originalDataStore.get(id).getQualities().decode(),filteredQualityDataStore.get(id).getValue().decode());
        
        assertEquals(originalDataStore.get(otherId).getNucleotides().decode(),filteredSeqDataStore.get(otherId).getValue().decode());
        assertEquals(originalDataStore.get(otherId).getQualities().decode(),filteredQualityDataStore.get(otherId).getValue().decode());
  
     }
    
     @Test
     public void includeOnlyIdsThatAreSpecified() throws IOException, IdReaderException, DataStoreException{
         
         FastQDataStore<FastQRecord> originalDataStore = new DefaultFastQFileDataStore(fastQFile, FastQQualityCodec.ILLUMINA);
         Fastq2Fasta.main(new String[]{"-i",ids.getAbsolutePath(),
                 "-s", seqOutputFile.getAbsolutePath(),
                 "-q", qualOutputFile.getAbsolutePath(),
                 fastQFile.getAbsolutePath()});
         NucleotideFastaDataStore filteredSeqDataStore = new DefaultNucleotideFastaFileDataStore(seqOutputFile);
         QualityFastaDataStore filteredQualityDataStore = new DefaultQualityFastaFileDataStore(qualOutputFile);
         
         assertEquals(1, filteredSeqDataStore.size());
         assertEquals(1, filteredQualityDataStore.size());
         assertFalse(filteredSeqDataStore.contains(otherId));
         assertFalse(filteredQualityDataStore.contains(otherId));
         assertEquals(originalDataStore.get(id).getNucleotides().decode(),filteredSeqDataStore.get(id).getValue().decode());
         assertEquals(originalDataStore.get(id).getQualities().decode(),filteredQualityDataStore.get(id).getValue().decode());
         
     }
     @Test
     public void excludeIdsThatAreSpecified() throws IOException, IdReaderException, DataStoreException{
         File fastQFile = RESOURCES.getFile("files/example.fastq");
         FastQDataStore<FastQRecord> originalDataStore = new DefaultFastQFileDataStore(fastQFile, FastQQualityCodec.ILLUMINA);
         Fastq2Fasta.main(new String[]{"-e",ids.getAbsolutePath(),
                 "-s", seqOutputFile.getAbsolutePath(),
                 "-q", qualOutputFile.getAbsolutePath(),
                 fastQFile.getAbsolutePath()});
         
         NucleotideFastaDataStore filteredSeqDataStore = new DefaultNucleotideFastaFileDataStore(seqOutputFile);
         QualityFastaDataStore filteredQualityDataStore = new DefaultQualityFastaFileDataStore(qualOutputFile);
        
         assertEquals(1, filteredSeqDataStore.size());
         assertEquals(1, filteredQualityDataStore.size());
         assertFalse(filteredSeqDataStore.contains(id));
         assertFalse(filteredQualityDataStore.contains(id));
         
         assertEquals(originalDataStore.get(otherId).getNucleotides().decode(),filteredSeqDataStore.get(otherId).getValue().decode());
         assertEquals(originalDataStore.get(otherId).getQualities().decode(),filteredQualityDataStore.get(otherId).getValue().decode());
        
     }
}
