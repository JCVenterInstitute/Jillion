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
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.datastore.DataStoreProviderHint;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualitySequenceFastaDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualitySequenceFastaFileDataStoreBuilder;
import org.jcvi.common.core.seq.fastx.fastq.FastqDataStore;
import org.jcvi.common.core.seq.fastx.fastq.FastqFileDataStoreBuilder;
import org.jcvi.common.core.seq.fastx.fastq.FastqQualityCodec;
import org.jcvi.common.core.testUtil.TestUtil;
import org.jcvi.common.core.testUtil.TestUtil.TriedToExitException;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * @author dkatzel
 *
 *
 */
public class TestFastQ2FastaEnd2End {
	private static SecurityManager previousManager=null;
	private static PrintStream OLD_STDOUT=null;
	private static PrintStream OLD_STDERR=null;
	
     private final ResourceFileServer RESOURCES = new ResourceFileServer(TestFastQ2FastaEnd2End.class);
     private ByteArrayOutputStream stdOutBytes;
 	private ByteArrayOutputStream stdErrBytes;
 	
     String id = "SOLEXA1:4:1:12:1692#0/1";
     String otherId = "SOLEXA1:4:1:12:1489#0/1";
     File ids;
     File seqOutputFile;
     File qualOutputFile;
     File fastQFile;
     @Rule
     public TemporaryFolder folder = new TemporaryFolder();
     
     @BeforeClass
 	public static void turnOffSystemExitAndRedirectStdOutAndErr(){
 		previousManager = System.getSecurityManager();
 		System.setSecurityManager(TestUtil.NON_EXITABLE_MANAGER);
 		
 		OLD_STDOUT =System.out;
 		OLD_STDERR =System.err;
 	}
 	
 	@AfterClass
 	public static void restoreSecurityManagerStdOutAndErr(){
 		System.setSecurityManager(previousManager);
 		System.setOut(OLD_STDOUT);
 		System.setErr(OLD_STDERR);
 	}
     @Before
     public void setup() throws IOException{
         seqOutputFile = folder.newFile("outputFile.fasta");
         qualOutputFile = folder.newFile("outputFile.qual");
         ids =folder.newFile("ids.lst");
         PrintWriter writer = new PrintWriter(ids);
         writer.println(id);
         writer.close();
         fastQFile = RESOURCES.getFile("files/example.fastq");
         
         stdOutBytes = new ByteArrayOutputStream();
 		stdErrBytes = new ByteArrayOutputStream();
 		System.setOut(new PrintStream(stdOutBytes));
 		System.setErr(new PrintStream(stdErrBytes));
     }
     @Test
     public void ifNoFiltersThenIncludeAllIds() throws IOException,  DataStoreException{
         
         FastqDataStore originalDataStore = new FastqFileDataStoreBuilder(fastQFile).hint(DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_SPEED).qualityCodec(FastqQualityCodec.ILLUMINA).build();
         Fastq2Fasta.main(new String[]{
                 "-s", seqOutputFile.getAbsolutePath(),
                 "-q", qualOutputFile.getAbsolutePath(),
                 fastQFile.getAbsolutePath()});
         NucleotideSequenceFastaDataStore filteredSeqDataStore = new NucleotideSequenceFastaFileDataStoreBuilder(seqOutputFile).build();
         QualitySequenceFastaDataStore filteredQualityDataStore = new QualitySequenceFastaFileDataStoreBuilder(qualOutputFile).build();
         
         assertEquals(2, filteredSeqDataStore.getNumberOfRecords());
         assertEquals(2, filteredQualityDataStore.getNumberOfRecords());
         assertEquals(originalDataStore.get(id).getNucleotideSequence(),filteredSeqDataStore.get(id).getSequence());
         assertEquals(originalDataStore.get(id).getQualitySequence(),filteredQualityDataStore.get(id).getSequence());
         
         assertEquals(originalDataStore.get(otherId).getNucleotideSequence(),filteredSeqDataStore.get(otherId).getSequence());
         assertEquals(originalDataStore.get(otherId).getQualitySequence(),filteredQualityDataStore.get(otherId).getSequence());
       
     }
     @Test
     public void supportSangerEncodedFastQWithDashSangerOption() throws IOException,  DataStoreException{
        File sangerFastQFile = RESOURCES.getFile("files/sanger.fastq");
        FastqDataStore originalDataStore = new FastqFileDataStoreBuilder(sangerFastQFile).hint(DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_SPEED).qualityCodec(FastqQualityCodec.SANGER).build();
        Fastq2Fasta.main(new String[]{
                "-s", seqOutputFile.getAbsolutePath(),
                "-q", qualOutputFile.getAbsolutePath(),
                "-sanger",
                sangerFastQFile.getAbsolutePath()});
        NucleotideSequenceFastaDataStore filteredSeqDataStore = new NucleotideSequenceFastaFileDataStoreBuilder(seqOutputFile).build();
        QualitySequenceFastaDataStore filteredQualityDataStore = new QualitySequenceFastaFileDataStoreBuilder(qualOutputFile).build();
        
        assertEquals(2, filteredSeqDataStore.getNumberOfRecords());
        assertEquals(2, filteredQualityDataStore.getNumberOfRecords());
        assertEquals(originalDataStore.get(id).getNucleotideSequence(),filteredSeqDataStore.get(id).getSequence());
        assertEquals(originalDataStore.get(id).getQualitySequence(),filteredQualityDataStore.get(id).getSequence());
        
        assertEquals(originalDataStore.get(otherId).getNucleotideSequence(),filteredSeqDataStore.get(otherId).getSequence());
        assertEquals(originalDataStore.get(otherId).getQualitySequence(),filteredQualityDataStore.get(otherId).getSequence());
  
     }
     @Test
     public void noWritersSpecifiedShouldThrowError() throws IOException,  DataStoreException{
        File sangerFastQFile = RESOURCES.getFile("files/sanger.fastq");
        try{
	        Fastq2Fasta.main(new String[]{
	                "-sanger",
	                sangerFastQFile.getAbsolutePath()});
	        fail("should exit");
        }catch(TriedToExitException expected){
        	assertEquals(1, expected.getExitCode());
        	String stdErrMessage = new String(stdErrBytes.toByteArray(), IOUtil.UTF_8).trim();
    		assertEquals(stdErrMessage,"must specify at least either -s or -q");
        }
     }
     @Test
     public void includeOnlyIdsThatAreSpecified() throws IOException,  DataStoreException{
         
         FastqDataStore originalDataStore = new FastqFileDataStoreBuilder(fastQFile).hint(DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_SPEED).qualityCodec(FastqQualityCodec.ILLUMINA).build();
         Fastq2Fasta.main(new String[]{"-i",ids.getAbsolutePath(),
                 "-s", seqOutputFile.getAbsolutePath(),
                 "-q", qualOutputFile.getAbsolutePath(),
                 fastQFile.getAbsolutePath()});
         NucleotideSequenceFastaDataStore filteredSeqDataStore = new NucleotideSequenceFastaFileDataStoreBuilder(seqOutputFile).build();
         QualitySequenceFastaDataStore filteredQualityDataStore = new QualitySequenceFastaFileDataStoreBuilder(qualOutputFile).build();
         
         assertEquals(1, filteredSeqDataStore.getNumberOfRecords());
         assertEquals(1, filteredQualityDataStore.getNumberOfRecords());
         assertFalse(filteredSeqDataStore.contains(otherId));
         assertFalse(filteredQualityDataStore.contains(otherId));
         assertEquals(originalDataStore.get(id).getNucleotideSequence(),filteredSeqDataStore.get(id).getSequence());
         assertEquals(originalDataStore.get(id).getQualitySequence(),filteredQualityDataStore.get(id).getSequence());
         
     }
     
     @Test
     public void onlywriteOutSeqIfOnlyUseSOption() throws IOException,  DataStoreException{
         
         FastqDataStore originalDataStore = new FastqFileDataStoreBuilder(fastQFile).hint(DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_SPEED).qualityCodec(FastqQualityCodec.ILLUMINA).build();
         Fastq2Fasta.main(new String[]{"-i",ids.getAbsolutePath(),
                 "-s", seqOutputFile.getAbsolutePath(),
                 fastQFile.getAbsolutePath()});
         NucleotideSequenceFastaDataStore filteredSeqDataStore = new NucleotideSequenceFastaFileDataStoreBuilder(seqOutputFile).build();
         assertEquals(0L, qualOutputFile.length());
         assertEquals(1, filteredSeqDataStore.getNumberOfRecords());
         assertFalse(filteredSeqDataStore.contains(otherId));

         assertEquals(originalDataStore.get(id).getNucleotideSequence(),filteredSeqDataStore.get(id).getSequence());
         
     }
     
     @Test
     public void onlywriteOutQualIfOnlyUseQOption() throws IOException,  DataStoreException{
         
         FastqDataStore originalDataStore = new FastqFileDataStoreBuilder(fastQFile).hint(DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_SPEED).qualityCodec(FastqQualityCodec.ILLUMINA).build();
         Fastq2Fasta.main(new String[]{"-i",ids.getAbsolutePath(),
                 "-q", qualOutputFile.getAbsolutePath(),
                 fastQFile.getAbsolutePath()});
         QualitySequenceFastaDataStore filteredQualityDataStore = new QualitySequenceFastaFileDataStoreBuilder(qualOutputFile).build();
         assertEquals(0L, seqOutputFile.length());
         assertEquals(1, filteredQualityDataStore.getNumberOfRecords());

         assertFalse(filteredQualityDataStore.contains(otherId));
         assertEquals(originalDataStore.get(id).getQualitySequence(),filteredQualityDataStore.get(id).getSequence());
         
     }
     
     @Test
     public void excludeIdsThatAreSpecified() throws IOException,  DataStoreException{
         File fastQFile = RESOURCES.getFile("files/example.fastq");
         FastqDataStore originalDataStore =  new FastqFileDataStoreBuilder(fastQFile).hint(DataStoreProviderHint.OPTIMIZE_RANDOM_ACCESS_SPEED).qualityCodec(FastqQualityCodec.ILLUMINA).build();
         Fastq2Fasta.main(new String[]{"-e",ids.getAbsolutePath(),
                 "-s", seqOutputFile.getAbsolutePath(),
                 "-q", qualOutputFile.getAbsolutePath(),
                 fastQFile.getAbsolutePath()});
         
         NucleotideSequenceFastaDataStore filteredSeqDataStore = new NucleotideSequenceFastaFileDataStoreBuilder(seqOutputFile).build();
         QualitySequenceFastaDataStore filteredQualityDataStore = new QualitySequenceFastaFileDataStoreBuilder(qualOutputFile).build();
        
         assertEquals(1, filteredSeqDataStore.getNumberOfRecords());
         assertEquals(1, filteredQualityDataStore.getNumberOfRecords());
         assertFalse(filteredSeqDataStore.contains(id));
         assertFalse(filteredQualityDataStore.contains(id));
         
         assertEquals(originalDataStore.get(otherId).getNucleotideSequence(),filteredSeqDataStore.get(otherId).getSequence());
         assertEquals(originalDataStore.get(otherId).getQualitySequence(),filteredQualityDataStore.get(otherId).getSequence());
        
     }
}
