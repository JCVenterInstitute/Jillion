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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.common.core.seq.fasta.nt.NucleotideSequenceFastaFileDataStoreBuilder;
import org.jcvi.common.core.seq.fasta.qual.QualitySequenceFastaDataStore;
import org.jcvi.common.core.seq.fasta.qual.QualitySequenceFastaFileDataStoreBuilder;
import org.jcvi.common.core.seq.trace.sanger.phd.ArtificalPhdDataStore;
import org.jcvi.common.core.seq.trace.sanger.phd.PhdDataStore;
import org.jcvi.common.core.symbol.qual.QualitySequenceDataStore;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceDataStore;
import org.jcvi.common.core.util.iter.StreamingIterator;
import org.jcvi.jillion.core.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultAceFileWriter {

    private final ResourceHelper resources = new ResourceHelper(TestAceFileUtil_writingAceContigs.class);
    private File tmpDir;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
   
    @Before
    public void setupTempFolder(){
    	tmpDir =folder.newFolder("temp");
    }

    @Test
    public void convertCtg2Ace() throws IOException, DataStoreException{
        File contigFile = resources.getFile("files/flu_644151.contig");
        File seqFile = resources.getFile("files/flu_644151.seq");
        File qualFile = resources.getFile("files/flu_644151.qual");

        final Date phdDate = new Date(0L);
        NucleotideSequenceDataStore nucleotideDataStore = FastaRecordDataStoreAdapter.adapt(NucleotideSequenceDataStore.class, new NucleotideSequenceFastaFileDataStoreBuilder(seqFile).build()); 
        final QualitySequenceFastaDataStore qualityFastaDataStore = new QualitySequenceFastaFileDataStoreBuilder(qualFile).build();
        QualitySequenceDataStore qualityDataStore = FastaRecordDataStoreAdapter.adapt(QualitySequenceDataStore.class, qualityFastaDataStore); 
        
        PhdDataStore phdDataStore = new ArtificalPhdDataStore(nucleotideDataStore, qualityDataStore, phdDate);
       
        AceFileContigDataStore aceDataStore = new AceAdapterContigFileDataStore(qualityFastaDataStore,phdDate,contigFile);

        File outputFile = folder.newFile();
        
        AceFileWriter sut = new AceFileWriterBuilder(outputFile,phdDataStore)
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
        NucleotideSequenceDataStore nucleotideDataStore = FastaRecordDataStoreAdapter.adapt(NucleotideSequenceDataStore.class, new NucleotideSequenceFastaFileDataStoreBuilder(seqFile).build()); 
        final QualitySequenceFastaDataStore qualityFastaDataStore = new QualitySequenceFastaFileDataStoreBuilder(qualFile).build();
        QualitySequenceDataStore qualityDataStore = FastaRecordDataStoreAdapter.adapt(QualitySequenceDataStore.class, qualityFastaDataStore); 
        
        PhdDataStore phdDataStore = new ArtificalPhdDataStore(nucleotideDataStore, qualityDataStore, phdDate);
       
        AceFileContigDataStore aceDataStore = new AceAdapterContigFileDataStore(qualityFastaDataStore,phdDate,contigFile);

        File outputFile = folder.newFile();
        
        AceFileWriter sut = new AceFileWriterBuilder(outputFile,phdDataStore)
        						.tmpDir(tmpDir)
        						.computeConsensusQualities()
        						.build();
        writeContigs(aceDataStore, sut);
        sut.close();
        AceFileContigDataStore reparsedAceDataStore = DefaultAceFileDataStore.create(outputFile);
        assertContigsAreEqual(aceDataStore, reparsedAceDataStore);
    }
    
    @Test
    public void convertCtg2AceWithBaseSegments() throws IOException, DataStoreException{
        File contigFile = resources.getFile("files/flu_644151.contig");
        File seqFile = resources.getFile("files/flu_644151.seq");
        File qualFile = resources.getFile("files/flu_644151.qual");

        final Date phdDate = new Date(0L);
        NucleotideSequenceDataStore nucleotideDataStore = FastaRecordDataStoreAdapter.adapt(NucleotideSequenceDataStore.class, new NucleotideSequenceFastaFileDataStoreBuilder(seqFile).build()); 
        final QualitySequenceFastaDataStore qualityFastaDataStore = new QualitySequenceFastaFileDataStoreBuilder(qualFile).build();
        QualitySequenceDataStore qualityDataStore = FastaRecordDataStoreAdapter.adapt(QualitySequenceDataStore.class, qualityFastaDataStore); 
        
        PhdDataStore phdDataStore = new ArtificalPhdDataStore(nucleotideDataStore, qualityDataStore, phdDate);
       
        AceFileContigDataStore aceDataStore = new AceAdapterContigFileDataStore(qualityFastaDataStore,phdDate,contigFile);

        File outputFile = folder.newFile();
        
        AceFileWriter sut = new AceFileWriterBuilder(outputFile,phdDataStore)
        						.tmpDir(tmpDir)
        						.includeBaseSegments()
        						.build();
        //writeContigs(aceDataStore, sut);
        //can't write out all contigs because some have ambiguities
        sut.write(aceDataStore.get("98"));
        sut.write(aceDataStore.get("97"));
        sut.write(aceDataStore.get("96"));
        sut.write(aceDataStore.get("95"));
        sut.close();       
        
        AceFileContigDataStore reparsedAceDataStore = DefaultAceFileDataStore.create(outputFile);
        assertContigHasSameRecords(aceDataStore.get("98"), reparsedAceDataStore.get("98"));
        assertContigHasSameRecords(aceDataStore.get("97"), reparsedAceDataStore.get("97"));
        assertContigHasSameRecords(aceDataStore.get("96"), reparsedAceDataStore.get("96"));
        assertContigHasSameRecords(aceDataStore.get("95"), reparsedAceDataStore.get("95"));
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
	            assertContigHasSameRecords(expectedContig, actualContig);
	        }
        }finally{
        	IOUtil.closeAndIgnoreErrors(contigIter);
        }
	}

	private void assertContigHasSameRecords(AceContig expectedContig,
			AceContig actualContig) {
		assertEquals("consensus", expectedContig.getConsensusSequence(), actualContig.getConsensusSequence());
		assertEquals("# reads", expectedContig.getNumberOfReads(), actualContig.getNumberOfReads());
		StreamingIterator<AceAssembledRead> readIter =null;
		try{
			readIter = expectedContig.getReadIterator();
			while(readIter.hasNext()){
				AceAssembledRead expectedRead = readIter.next();
				String id = expectedRead.getId();
				AceAssembledRead actualRead = actualContig.getRead(expectedRead.getId());
		        assertEquals(id + " basecalls", expectedRead.getNucleotideSequence(), actualRead.getNucleotideSequence());
		        assertEquals(id + " offset", expectedRead.getGappedStartOffset(), actualRead.getGappedStartOffset());
		        assertEquals(id + " validRange", expectedRead.getReadInfo().getValidRange(), actualRead.getReadInfo().getValidRange());
		        assertEquals(id + " dir", expectedRead.getDirection(), actualRead.getDirection());
		    
			}
		}finally{
			IOUtil.closeAndIgnoreErrors(readIter);
		}
	}
	private void writeContigs(AceFileContigDataStore aceDataStore,
			AceFileWriter sut) throws DataStoreException, IOException {
		StreamingIterator<AceContig> iter = aceDataStore.iterator();
        try{
        	while(iter.hasNext()){
        		AceContig next = iter.next();
				sut.write(next);
        	}        	
        }finally{
        	IOUtil.closeAndIgnoreErrors(iter);
        }
	}
	
    @Test
    public void rewritingAceShouldBeSimilar() throws IOException, DataStoreException{
    	File originalAce = resources.getFile("files/sample.ace");
    	
    	PhdDataStore phdDataStore = HighLowAceContigPhdDatastore.create(originalAce);
    	
    	 File outputFile = folder.newFile();
         
         AceFileWriter sut = new AceFileWriterBuilder(outputFile,phdDataStore)
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
