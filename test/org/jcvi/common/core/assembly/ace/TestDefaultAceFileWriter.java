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

package org.jcvi.common.core.assembly.ace;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.jcvi.common.core.assembly.ace.AceContig;
import org.jcvi.common.core.assembly.ace.AceFileContigDataStore;
import org.jcvi.common.core.assembly.ace.AcePlacedRead;
import org.jcvi.common.core.assembly.ace.DefaultAceFileDataStore;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.common.core.seq.fastx.fasta.nt.DefaultNucleotideSequenceFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.DefaultQualityFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.qual.QualitySequenceFastaDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.ArtificalPhdDataStore;
import org.jcvi.common.core.seq.read.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.symbol.qual.QualityDataStore;
import org.jcvi.common.core.symbol.qual.QualityDataStoreAdapter;
import org.jcvi.common.core.symbol.residue.nt.NucleotideDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideDataStoreAdapter;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.common.io.fileServer.ResourceFileServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultAceFileWriter {

    private final ResourceFileServer resources = new ResourceFileServer(TestAceFileUtil_writingAceContigs.class);
    private final File tmpDir;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
   
    public TestDefaultAceFileWriter(){
    	tmpDir =folder.newFolder("temp");
    }
    @Test
    public void convertCtg2Ace() throws IOException, DataStoreException{
        File contigFile = resources.getFile("files/flu_644151.contig");
        File seqFile = resources.getFile("files/flu_644151.seq");
        File qualFile = resources.getFile("files/flu_644151.qual");

        final Date phdDate = new Date(0L);
        NucleotideDataStore nucleotideDataStore = new NucleotideDataStoreAdapter(FastaRecordDataStoreAdapter.adapt(DefaultNucleotideSequenceFastaFileDataStore.create(seqFile))); 
        final QualitySequenceFastaDataStore qualityFastaDataStore = DefaultQualityFastaFileDataStore.create(qualFile);
        QualityDataStore qualityDataStore = new QualityDataStoreAdapter(FastaRecordDataStoreAdapter.adapt(qualityFastaDataStore)); 
        
        PhdDataStore phdDataStore = new ArtificalPhdDataStore(nucleotideDataStore, qualityDataStore, phdDate);
       
        AceFileContigDataStore aceDataStore = new AceAdapterContigFileDataStore(qualityFastaDataStore,phdDate,contigFile);

        File outputFile = File.createTempFile("test", ".ace",tmpDir);
        
        AceFileWriter sut = new DefaultAceFileWriter.Builder(outputFile,phdDataStore)
        						.tmpDir(tmpDir)
        						.build();
        writeContigs(aceDataStore, sut);
        sut.close();
        
        AceFileContigDataStore reparsedAceDataStore = DefaultAceFileDataStore.create(outputFile);
        assertContigsAreEqual(aceDataStore, reparsedAceDataStore);
    }
    
    @Test
    public void convertCtg2AceWithComputedConsensusQualities() throws IOException, DataStoreException{
        File contigFile = resources.getFile("files/flu_644151.contig");
        File seqFile = resources.getFile("files/flu_644151.seq");
        File qualFile = resources.getFile("files/flu_644151.qual");

        final Date phdDate = new Date(0L);
        NucleotideDataStore nucleotideDataStore = new NucleotideDataStoreAdapter(FastaRecordDataStoreAdapter.adapt(DefaultNucleotideSequenceFastaFileDataStore.create(seqFile))); 
        final QualitySequenceFastaDataStore qualityFastaDataStore = DefaultQualityFastaFileDataStore.create(qualFile);
        QualityDataStore qualityDataStore = new QualityDataStoreAdapter(FastaRecordDataStoreAdapter.adapt(qualityFastaDataStore)); 
        
        PhdDataStore phdDataStore = new ArtificalPhdDataStore(nucleotideDataStore, qualityDataStore, phdDate);
       
        AceFileContigDataStore aceDataStore = new AceAdapterContigFileDataStore(qualityFastaDataStore,phdDate,contigFile);

        File outputFile = File.createTempFile("test", ".ace",tmpDir);
        
        AceFileWriter sut = new DefaultAceFileWriter.Builder(outputFile,phdDataStore)
        						.tmpDir(tmpDir)
        						.computeConsensusQualities()
        						.build();
        writeContigs(aceDataStore, sut);
        sut.close();
        System.out.println(outputFile.getAbsolutePath());
        AceFileContigDataStore reparsedAceDataStore = DefaultAceFileDataStore.create(outputFile);
        assertContigsAreEqual(aceDataStore, reparsedAceDataStore);
    }
	private void assertContigsAreEqual(AceFileContigDataStore aceDataStore,
			AceFileContigDataStore reparsedAceDataStore)
			throws DataStoreException {
		assertEquals("# contigs", aceDataStore.getNumberOfRecords(), reparsedAceDataStore.getNumberOfRecords());
        
		StreamingIterator<AceContig> contigIter = aceDataStore.iterator();
        try{
	        while(contigIter.hasNext()){
	        	AceContig expectedContig = contigIter.next();
	            AceContig actualContig = reparsedAceDataStore.get(expectedContig.getId());            
	            assertEquals("consensus", expectedContig.getConsensusSequence(), actualContig.getConsensusSequence());
	            assertEquals("# reads", expectedContig.getNumberOfReads(), actualContig.getNumberOfReads());
	            StreamingIterator<AcePlacedRead> readIter =null;
	            try{
	            	readIter = expectedContig.getReadIterator();
	            	while(readIter.hasNext()){
	            		AcePlacedRead expectedRead = readIter.next();
	            		String id = expectedRead.getId();
	            		AcePlacedRead actualRead = actualContig.getRead(expectedRead.getId());
	  	                assertEquals(id + " basecalls", expectedRead.getNucleotideSequence(), actualRead.getNucleotideSequence());
	  	                assertEquals(id + " offset", expectedRead.getGappedStartOffset(), actualRead.getGappedStartOffset());
	  	                assertEquals(id + " validRange", expectedRead.getReadInfo().getValidRange(), actualRead.getReadInfo().getValidRange());
	  	                assertEquals(id + " dir", expectedRead.getDirection(), actualRead.getDirection());
	  	            
	            	}
	            }finally{
	            	IOUtil.closeAndIgnoreErrors(readIter);
	            }
	        }
        }finally{
        	IOUtil.closeAndIgnoreErrors(contigIter);
        }
	}
	private void writeContigs(AceFileContigDataStore aceDataStore,
			AceFileWriter sut) throws DataStoreException, IOException {
		StreamingIterator<AceContig> iter = aceDataStore.iterator();
        try{
        	while(iter.hasNext()){
        		sut.write(iter.next());
        	}        	
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
	}
	
    @Test
    public void rewritingAceShouldBeSimilar() throws IOException, DataStoreException{
    	File originalAce = resources.getFile("files/sample.ace");
    	
    	PhdDataStore phdDataStore = HiLowAceContigPhdDatastore.create(originalAce);
    	
    	 File outputFile = File.createTempFile("test", ".ace",tmpDir);
         
         AceFileWriter sut = new DefaultAceFileWriter.Builder(outputFile,phdDataStore)
         						.tmpDir(tmpDir)
         						.build();
         AceFileContigDataStore datastore = DefaultAceFileDataStore.create(originalAce);
         
         //lets write out the tags first to make sure they get put at the end correctly
         writeReadTags(datastore,sut);
         writeWholeAssemblyTags(datastore,sut);
         writeConsensusTags(datastore,sut);
         
         writeContigs(datastore, sut);
         
         sut.close();
         AceFileContigDataStore reparsedAceDataStore = DefaultAceFileDataStore.create(outputFile);
         assertContigsAreEqual(datastore, reparsedAceDataStore);
         
         assertWholeReadTagsAreEqual(datastore, reparsedAceDataStore);
         assertReadTagsAreEqual(datastore, reparsedAceDataStore);
         assertConsensusTagsAreEqual(datastore, reparsedAceDataStore);
         
    }
    
    private void assertConsensusTagsAreEqual(AceFileContigDataStore datastore,
			AceFileContigDataStore reparsedAceDataStore) throws DataStoreException {
		StreamingIterator<ConsensusAceTag> expected = datastore.getConsensusTagIterator();
		StreamingIterator<ConsensusAceTag> actual = datastore.getConsensusTagIterator();
		
		while(expected.hasNext()){
			assertTrue(actual.hasNext());
			assertEquals(expected.next(),actual.next());
		}
		assertFalse(actual.hasNext());
		
	}
	private void assertReadTagsAreEqual(AceFileContigDataStore datastore,
			AceFileContigDataStore reparsedAceDataStore) throws DataStoreException {
		StreamingIterator<ReadAceTag> expected = datastore.getReadTagIterator();
		StreamingIterator<ReadAceTag> actual = datastore.getReadTagIterator();
		
		while(expected.hasNext()){
			assertTrue(actual.hasNext());
			assertEquals(expected.next(),actual.next());
		}
		assertFalse(actual.hasNext());
		
	}
	private void assertWholeReadTagsAreEqual(AceFileContigDataStore datastore,
			AceFileContigDataStore reparsedAceDataStore) throws DataStoreException {
		StreamingIterator<WholeAssemblyAceTag> expected = datastore.getWholeAssemblyTagIterator();
		StreamingIterator<WholeAssemblyAceTag> actual = datastore.getWholeAssemblyTagIterator();
		
		while(expected.hasNext()){
			assertTrue(actual.hasNext());
			assertEquals(expected.next(),actual.next());
		}
		assertFalse(actual.hasNext());
	}
	private void writeReadTags(AceFileContigDataStore datastore,
			AceFileWriter sut) throws IOException, DataStoreException {
		StreamingIterator<ReadAceTag> iter = datastore.getReadTagIterator();
		try{
			while(iter.hasNext()){
				sut.write(iter.next());
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}		
	}
	
	private void writeWholeAssemblyTags(AceFileContigDataStore datastore,
			AceFileWriter sut) throws IOException, DataStoreException {
		StreamingIterator<WholeAssemblyAceTag> iter = datastore.getWholeAssemblyTagIterator();
		try{
			while(iter.hasNext()){
				sut.write(iter.next());
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}		
	}
	
	private void writeConsensusTags(AceFileContigDataStore datastore,
			AceFileWriter sut) throws IOException, DataStoreException {
		StreamingIterator<ConsensusAceTag> iter = datastore.getConsensusTagIterator();
		try{
			while(iter.hasNext()){
				sut.write(iter.next());
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(iter);
		}		
	}
}
